package com.kmarinos.externalsqltablemonitoring.core.notification;

import com.kmarinos.externalsqltablemonitoring.core.entity.EntityChangedEvent;
import com.kmarinos.externalsqltablemonitoring.core.entity.EntityChangedListener;
import com.kmarinos.externalsqltablemonitoring.core.notification.domain.SendNotification;
import com.kmarinos.externalsqltablemonitoring.model.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractNotificationProvider<T> {
  private BiFunction<EntityChangedEvent<T>, User, SendNotification.SendNotificationBuilder<EntityChangedEvent<T>>> fromBuilder(
      Function<String,String> titles,Function<String,String> messages){
    return (event,user)->getBuilder(event,user,titles,messages);
  }
  public Collection<SendNotification<? extends EntityChangedEvent<T>>> createNotifications(EntityChangedEvent<T> event){
    return myCustomNotifications(event,fromBuilder(this::withTitles,this::withMessages));
  }
  public Collection<SendNotification<? extends EntityChangedEvent<T>>> myCustomNotifications(EntityChangedEvent<T> event, BiFunction<EntityChangedEvent<T>,User,SendNotification.SendNotificationBuilder<EntityChangedEvent<T>>> builderProvider){
    List<SendNotification<? extends EntityChangedEvent<T>>> notifications = new ArrayList<>();
    for(User userToNotify:event.getToNotify()){
      if(userToNotify.getEmail() == null){
        log.warn("The user {} {} ({}) has no email. No notification can be sent.",userToNotify.getFirstname(),userToNotify.getLastname(),userToNotify.getUsername());
        continue;
      }
      notifications.add(builderProvider.andThen(b->b.ctx(event).toNotify(userToNotify).build()).apply(event,userToNotify));
    }
    return notifications;
  }
  public abstract String withMessages(String languageCode);
  public abstract String withTitles(String languageCode);
  public abstract SendNotification.SendNotificationBuilder<EntityChangedEvent<T>> getBuilder(EntityChangedEvent<T> event,User user,Function<String,String> titlesProvider,Function<String,String>messagesProvider);
}
