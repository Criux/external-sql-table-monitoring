package com.kmarinos.externalsqltablemonitoring.core.notification.impl;

import com.kmarinos.externalsqltablemonitoring.core.notification.NotificationHandler;
import com.kmarinos.externalsqltablemonitoring.core.notification.domain.SendNotification;
import com.kmarinos.externalsqltablemonitoring.model.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConsoleNotificationHandler implements NotificationHandler {

  public void send(SendNotification<?> notification) {
    log.debug("*** Notification START ***");
    log.debug("To: {}",
        Optional.ofNullable(notification.getToNotify()).map(User::getEmail).orElse(""));
    log.debug("Contact person: {}",
        Optional.ofNullable(notification.getContactPerson()).map(User::getEmail).orElse(""));
    log.debug("Title: {}", notification.getTitle());
    log.debug("Message: \n{}", notification.getMessage());
    log.debug("MessageData: {}",
        Optional.ofNullable(notification.getMessageData()).orElse(new HashMap<>()).entrySet()
            .stream().map(e -> e.getKey() + ":" + e.getValue().get()).collect(
                Collectors.joining(",")));
    log.debug("Disclaimer: {}", String.join(",",
        Optional.ofNullable(notification.getDisclaimer()).orElse(new ArrayList<>())));
    log.debug("Contact: \n{}",notification.getContact());
    log.debug("*** Notification END ***");
  }
}
