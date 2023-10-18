package com.kmarinos.externalsqltablemonitoring.monitored.rental;

import com.kmarinos.externalsqltablemonitoring.core.entity.EntityWatcher;
import com.kmarinos.externalsqltablemonitoring.monitored.rental.domain.Rental;
import java.util.Collection;
import org.springframework.stereotype.Component;

@Component
public class RentalWatcher implements EntityWatcher<Rental> {

  @Override
  public Class<Rental> getWatchedEntityClass() {
    return null;
  }

  @Override
  public Collection<Rental> fetchCurrentState() {
    return null;
  }

  @Override
  public Collection<Rental> fetchChangedState() {
    return null;
  }
}
