package com.kmarinos.externalsqltablemonitoring.core.entity;

import java.util.Collection;

public interface DataChangedEventListener<T,U> {

  Class<? extends EntityChangedEvent<T>> getEventClass();
  EntityChangedEvent<T> createChangeEvent(T oldState,T newState);
  boolean getTriggerOnCondition(T oldState,T newState);
  Collection<U> handleEvent(EntityChangedEvent<T>event);
  default boolean isDifferent(Object obj1,Object obj2){
    return (obj1==null&&obj2!=null)||(obj2==null&&obj1!=null)||(obj1!=null&&!obj1.equals(obj2));
  }

}
