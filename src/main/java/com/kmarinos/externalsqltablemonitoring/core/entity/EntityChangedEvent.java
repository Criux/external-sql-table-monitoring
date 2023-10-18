package com.kmarinos.externalsqltablemonitoring.core.entity;

import com.kmarinos.externalsqltablemonitoring.model.User;
import java.util.List;
import lombok.Data;

@Data
public class EntityChangedEvent<T> {
  T oldState;
  T newState;
  List<User> toNotify;

}
