package com.kmarinos.externalsqltablemonitoring.monitored.film.listener;

import com.kmarinos.externalsqltablemonitoring.core.entity.EntityChangedEvent;
import com.kmarinos.externalsqltablemonitoring.core.entity.EntityChangedListener;
import com.kmarinos.externalsqltablemonitoring.core.notification.impl.StandardNotificationProvider;
import com.kmarinos.externalsqltablemonitoring.model.User;
import com.kmarinos.externalsqltablemonitoring.model.repo.UserRepository;
import com.kmarinos.externalsqltablemonitoring.monitored.film.domain.Film;
import com.kmarinos.externalsqltablemonitoring.monitored.film.event.FilmReleasedEvent;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FilmReleasedListener extends StandardNotificationProvider<Film> implements
    EntityChangedListener<Film> {
  private final UserRepository userRepository;

  @Override
  public Class<? extends EntityChangedEvent<Film>> getEventClass() {
    return FilmReleasedEvent.class;
  }

  @Override
  public boolean getTriggerOnCondition(Film oldState, Film newState) {
    return oldState==null&&newState!=null;
  }

  @Override
  public List<User> getToNotifyForEvent(EntityChangedEvent<Film> event) {
    return userRepository.fetchUsersReceivingNotificationsForEvent(event.getClass().getSimpleName());
  }

  @Override
  public String withMessages(String languageCode) {
    return """
        The film **${newState.title}** was created.
        """;
  }

  @Override
  public String withTitles(String languageCode) {
    return """
        The film **${newState.title}** was created.
        """;
  }
}
