package com.kmarinos.externalsqltablemonitoring.core.notification.impl;

import com.kmarinos.externalsqltablemonitoring.MonitoredDBClient;
import com.kmarinos.externalsqltablemonitoring.core.entity.EntityChangedEvent;
import com.kmarinos.externalsqltablemonitoring.core.notification.AbstractNotificationProvider;
import com.kmarinos.externalsqltablemonitoring.core.notification.domain.NotificationTemplateProvider;
import com.kmarinos.externalsqltablemonitoring.core.notification.domain.SendNotification;
import com.kmarinos.externalsqltablemonitoring.model.User;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class StandardNotificationProvider<T> extends AbstractNotificationProvider<T> {

  @Autowired
  private NotificationTemplateProvider notificationTemplateProvider;
  @Autowired
  private MonitoredDBClient monitoredDBClient;

  protected Map<String, Supplier<?>> generateSecondaryMessageData(){return new HashMap<>();}

  @Override
  public SendNotification.SendNotificationBuilder<EntityChangedEvent<T>> getBuilder(EntityChangedEvent<T> event, User user,
      Function<String,String> titlesProvider,Function<String,String>messageProvider){
    String languageCode = monitoredDBClient.getPreferredLanguageForUser(user.getUsername());
    return SendNotification.<EntityChangedEvent<T>>builder()
        .title(titlesProvider.apply(languageCode))
        .language(languageCode)
        .message(notificationTemplateProvider.getStandardMessage(languageCode,user,messageProvider.apply(languageCode)))
        .disclaimer(notificationTemplateProvider.getStandardDisclaimer(languageCode))
        .contact(notificationTemplateProvider.getStandardContact(languageCode))
        .messageData(generateSecondaryMessageData());
  }
}
