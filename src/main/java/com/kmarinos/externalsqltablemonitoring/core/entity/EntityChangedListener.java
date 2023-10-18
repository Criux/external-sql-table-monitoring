package com.kmarinos.externalsqltablemonitoring.core.entity;

import com.kmarinos.externalsqltablemonitoring.core.notification.NotificationAwareListener;
import com.kmarinos.externalsqltablemonitoring.model.User;
import com.sun.jdi.InvocationException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

public interface EntityChangedListener<T extends LoggableEntity> extends
    NotificationAwareListener<T> {
  org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(EntityChangedListener.class);

  default void extendEvent(EntityChangedEvent<T>event,T oldState,T newState){}
  List<User> getToNotifyForEvent(EntityChangedEvent<T>event);
  default EntityChangedEvent<T> createChangeEvent(T oldState,T newState){
    EntityChangedEvent<T> event = null;
    log.info("Creating change event for type {} and business id {}",newState.businessObjectType(),newState.businessId());
    try{
      event = getEventClass().getDeclaredConstructor().newInstance();
    }catch(InstantiationException|IllegalAccessException| InvocationTargetException|NoSuchMethodException e){
      log.error("Cannot create event for class {}",getEventClass());
      return null;
    }
    extendEvent(event,oldState,newState);
    event.setOldState(oldState);
    event.setNewState(newState);
    event.setToNotify(getToNotifyForEvent(event));
    log.info("To notify: {}",event.getToNotify().stream().map(User::getEmail).collect(
        Collectors.joining(",","[","]")));
    return event;
  }
}
