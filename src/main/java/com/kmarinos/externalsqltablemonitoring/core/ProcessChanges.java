package com.kmarinos.externalsqltablemonitoring.core;

import com.kmarinos.externalsqltablemonitoring.core.entity.EntityChangedEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings({"unchecked","rawtypes"})
public class ProcessChanges {

  @Value("${monitoring.monitor-events:false}")
  private Boolean canMonitor;

  private final ApplicationEventPublisher applicationEventPublisher;
  private final ConfigurableApplicationContext context;
  private final AtomicInteger counter = new AtomicInteger(0);
  private final Map<Class<? extends EntityChangedEvent>, BiPredicate> conditions = new HashMap<>();
  private final Map<Class<? extends EntityChangedEvent>, Function> consumers = new HashMap<>();
  private final Map<Class, BiFunction> eventInstanceSuppliers = new HashMap<>();
  private final Map<Class, Supplier<? extends Collection>> oldStateSuppliers = new HashMap<>();
  private final Map<Class, Supplier<? extends Collection>> newStateSuppliers = new HashMap<>();
  private final List<Class> monitoredObjects = new ArrayList<>();

  public <T> void registerEntityForMonitoring(Class<T> entityClass,
      Supplier<? extends Collection<T>> oldStateSupplier,
      Supplier<? extends Collection<T>> newStateSupplier) {
    monitoredObjects.add(entityClass);
    oldStateSuppliers.put(entityClass, oldStateSupplier);
    newStateSuppliers.put(entityClass, newStateSupplier);
  }

  public <T> void monitorEvent(Class<? extends EntityChangedEvent<T>> eventClass,
      BiFunction<T, T, ? extends EntityChangedEvent<T>> eventInstanceSupplier,
      BiPredicate<T, T> condition,
      Function<? extends EntityChangedEvent<T>, Collection> consumer) {
    conditions.put(eventClass,condition);
    consumers.put(eventClass,consumer);
    eventInstanceSuppliers.put(eventClass,eventInstanceSupplier);
  }
  @EventListener(EntityChangedEvent.class)
  public void handleEntityChanged(EntityChangedEvent event){
    Optional.ofNullable(consumers.get(event.getClass())).map(c->{
      //chain events
      Optional.ofNullable(((Function<EntityChangedEvent,Collection>)c).apply(event)).map(events->{
        events.forEach(applicationEventPublisher::publishEvent);
        return null;
      });
      return null;
    });
  }
  @Scheduled(fixedRate = 1000L)
  private void runCycle(){
    if(canMonitor){
      checkEntityChanges();
    }
  }
  private void checkEntityChanges(){
    if(counter.get()<2){
      counter.incrementAndGet();
    }
    for (Class monitoredObject:monitoredObjects){
      var repo = new Repositories(context.getBeanFactory()).getRepositoryFor(monitoredObject);
      var oldStateObjects = oldStateSuppliers.get(monitoredObject).get();
      var newStateObjects = newStateSuppliers.get(monitoredObject).get();
      if(counter.get()==1){
        log.info("Checking for changes during downtime for {}",monitoredObject.getSimpleName());
      }
      for (Object newStateObject:newStateObjects){
        var oldStateObject = getMatching(oldStateObjects,newStateObject);
        this.conditions.forEach((eventClass,condition)->{
          try{
            if(condition.test(oldStateObject,newStateObject)){
              var changeEvent = eventInstanceSuppliers.getOrDefault(eventClass,(a,b)->{
                try{
                  EntityChangedEvent entityChangedEvent = eventClass.getConstructor().newInstance();
                  entityChangedEvent.setOldState(a);
                  entityChangedEvent.setNewState(b);
                  return entityChangedEvent;
                }catch (InstantiationException|IllegalAccessException| InvocationTargetException|NoSuchMethodException e){
                  throw new RuntimeException(e);
                }
              }).apply(oldStateObject,newStateObject);
              applicationEventPublisher.publishEvent(changeEvent);
            }
          }
          //this exception is thrown when the conditions to test don't match the classes that are
          //tested. Since all conditions are in the same map, the conditions that throw this exception
          //can be safely ignored.
          catch (ClassCastException e){
            //ignored
          }
        });
        repo.map(o ->((CrudRepository)o).save(newStateObject));
      }
    }
    if(counter.get()==1){
      log.info("Ready to process events...");
    }
  }
  private <T> T getMatching(Collection<T> list, T toMatch){
    for(T existing:list){
      if(existing.equals(toMatch)){
        return existing;
      }
    }
    return toMatch;
  }
}
