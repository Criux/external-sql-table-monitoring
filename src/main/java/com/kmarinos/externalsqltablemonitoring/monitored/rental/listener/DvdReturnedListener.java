package com.kmarinos.externalsqltablemonitoring.monitored.rental.listener;

import com.kmarinos.externalsqltablemonitoring.MonitoredDBClient;
import com.kmarinos.externalsqltablemonitoring.core.entity.EntityChangedEvent;
import com.kmarinos.externalsqltablemonitoring.core.entity.EntityChangedListener;
import com.kmarinos.externalsqltablemonitoring.core.notification.impl.StandardNotificationProvider;
import com.kmarinos.externalsqltablemonitoring.model.User;
import com.kmarinos.externalsqltablemonitoring.model.repo.UserRepository;
import com.kmarinos.externalsqltablemonitoring.monitored.rental.domain.Rental;
import com.kmarinos.externalsqltablemonitoring.monitored.rental.event.DvdRentedEvent;
import com.kmarinos.externalsqltablemonitoring.monitored.rental.event.DvdReturnedEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DvdReturnedListener extends StandardNotificationProvider<Rental> implements
    EntityChangedListener<Rental> {
  private final UserRepository userRepository;
  private final MonitoredDBClient monitoredDBClient;

  @Override
  public Class<? extends EntityChangedEvent<Rental>> getEventClass() {
    return DvdReturnedEvent.class;
  }

  @Override
  public boolean getTriggerOnCondition(Rental oldState, Rental newState) {
    return oldState.getReturnDate()==null&&newState.getReturnDate()!=null;
  }
  @Override
  protected Map<String, Supplier<?>> generateSecondaryMessageData(){return Map.of("customer",()->"DUMMY CUSTOMER");}

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
        Film ${film.title} was returned at ${newState.rentalDate} from ${customer}
        """;
  }

  @Override
  public String withTitles(String languageCode) {
    return """
        Film ${film.title} was returned at ${newState.rentalDate}
        """;
  }

}
