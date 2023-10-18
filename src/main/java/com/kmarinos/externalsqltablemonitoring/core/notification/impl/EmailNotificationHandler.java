package com.kmarinos.externalsqltablemonitoring.core.notification.impl;

import com.kmarinos.externalsqltablemonitoring.core.entity.EntityChangedEvent;
import com.kmarinos.externalsqltablemonitoring.core.notification.domain.SendNotification;
import com.kmarinos.externalsqltablemonitoring.model.repo.UserRepository;
import com.kmarinos.externalsqltablemonitoring.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailNotificationHandler extends SpELAwareNotificationHandler {

  private final EmailService emailService;
  private final UserRepository userRepository;

  public EmailNotificationHandler(EmailService emailService, UserRepository userRepository) {
    super(new SpelExpressionParser());
    this.emailService = emailService;
    this.userRepository = userRepository;
  }
  @Override
  public void sendConverted(SendNotification<?> notification){
    var ctx = notification.getCtx();
    if(ctx instanceof EntityChangedEvent<?>){
      sendOnEntityChangedEvent((SendNotification<? extends EntityChangedEvent<?>>) notification);
    }else{
      throw new IllegalStateException("Unexpected value: "+notification.getCtx());
    }
  }
  private void sendOnEntityChangedEvent(SendNotification<? extends EntityChangedEvent<?>> notification){
    var shouldNotify = userRepository.isUserReceivingNotificationsForEvent(notification.getToNotify(),notification.getCtx().getClass().getName());
    if(shouldNotify){
      log.info("Sending email for event {} to user {} in language {}",
          notification.getCtx().getClass().getSimpleName(),
          notification.getToNotify().getUsername(),
          notification.getLanguage()
          );
      emailService.sendNotificationAsEmail(notification);
    }else{
      log.info("The user {} does not want to be notified for the event of type {}",notification.getToNotify().getUsername(),notification.getCtx().getClass().getSimpleName());
    }
  }

}
