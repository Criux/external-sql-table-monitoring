package com.kmarinos.externalsqltablemonitoring.core;

import com.kmarinos.externalsqltablemonitoring.core.entity.DataChangedEventListener;
import com.kmarinos.externalsqltablemonitoring.core.entity.EntityWatcher;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChangeListenerRegistration {

  private final ProcessChanges processChanges;
  @Autowired
  List<EntityWatcher> entityWatchers;
  @Autowired
  List<DataChangedEventListener> listeners;

  @PostConstruct
  public void init(){
    if(entityWatchers!=null){
      entityWatchers.forEach(watcher->processChanges.registerEntityForMonitoring(watcher.getWatchedEntityClass(),watcher::fetchCurrentState,watcher::fetchChangedState));
    }
    if(listeners!=null){
      listeners.forEach(listener->processChanges.monitorEvent(listener.getEventClass(),listener::createChangeEvent,listener::getTriggerOnCondition, listener::handleEvent));
    }
  }
}
