package com.kmarinos.externalsqltablemonitoring.core.notification;

import com.kmarinos.externalsqltablemonitoring.core.notification.domain.SendNotification;
import org.springframework.context.event.EventListener;

public interface NotificationHandler {

  @EventListener
  void send(SendNotification<?> notification);
}
