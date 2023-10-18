package com.kmarinos.externalsqltablemonitoring.core.entity;

import java.util.Collection;

public interface EntityWatcher<T> {
  Class<T> getWatchedEntityClass();
  Collection<T>fetchCurrentState();
  Collection<T>fetchChangedState();

}
