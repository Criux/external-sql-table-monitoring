package com.kmarinos.externalsqltablemonitoring.core.notification;

import com.kmarinos.externalsqltablemonitoring.core.entity.DataChangedEventListener;
import com.kmarinos.externalsqltablemonitoring.core.entity.EntityChangedEvent;
import com.kmarinos.externalsqltablemonitoring.core.notification.domain.SendNotification;
import java.util.Collection;

public interface NotificationAwareListener<T> extends DataChangedEventListener<T, SendNotification<? extends EntityChangedEvent<T>>> {

  Collection<SendNotification<? extends EntityChangedEvent<T>>> createNotifications(EntityChangedEvent<T> event);
  @Override
  default Collection<SendNotification<? extends EntityChangedEvent<T>>> handleEvent(EntityChangedEvent<T> event){
    return createNotifications(event);
  }
}
