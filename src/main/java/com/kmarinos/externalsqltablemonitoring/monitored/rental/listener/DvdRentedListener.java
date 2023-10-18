package com.kmarinos.externalsqltablemonitoring.monitored.rental.listener;

import com.kmarinos.externalsqltablemonitoring.MonitoredDBClient;
import com.kmarinos.externalsqltablemonitoring.core.entity.EntityChangedEvent;
import com.kmarinos.externalsqltablemonitoring.core.entity.EntityChangedListener;
import com.kmarinos.externalsqltablemonitoring.core.notification.impl.StandardNotificationProvider;
import com.kmarinos.externalsqltablemonitoring.model.User;
import com.kmarinos.externalsqltablemonitoring.model.repo.UserRepository;
import com.kmarinos.externalsqltablemonitoring.monitored.rental.domain.Rental;
import com.kmarinos.externalsqltablemonitoring.monitored.rental.event.DvdRentedEvent;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DvdRentedListener extends StandardNotificationProvider<Rental> implements
    EntityChangedListener<Rental> {
  private final UserRepository userRepository;
  private final MonitoredDBClient monitoredDBClient;

  @Override
  public Class<? extends EntityChangedEvent<Rental>> getEventClass() {
    return DvdRentedEvent.class;
  }

  @Override
  public boolean getTriggerOnCondition(Rental oldState, Rental newState) {
    return oldState==null&&newState!=null;
  }

  @Override
  public void extendEvent(EntityChangedEvent<Rental> event, Rental oldState, Rental newState) {
    EntityChangedListener.super.extendEvent(event, oldState, newState);
    ((DvdRentedEvent)event).setFilm(monitoredDBClient.getFilmFromRental(newState.getId()));
  }

  @Override
  public List<User> getToNotifyForEvent(EntityChangedEvent<Rental> event) {
    return userRepository.findById(event.getNewState().getCustomerId()).map(List::of).orElse(new ArrayList<>());
  }

  @Override
  public String withMessages(String languageCode) {
    return """
        Film ${film.title} was rented at ${newState.rentalDate}
        """;
  }

  @Override
  public String withTitles(String languageCode) {
    return """
        Film ${film.title} was rented at ${newState.rentalDate}
        """;
  }

}
