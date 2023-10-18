package com.kmarinos.externalsqltablemonitoring.core.notification.domain;

import com.kmarinos.externalsqltablemonitoring.model.User;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendNotification<T> {
  User toNotify;
  User contactPerson;
  T ctx;
  String title;
  String message;
  Map<String, Supplier<?>> messageData;
  List<String>disclaimer;
  String contact;
  String language;

}
