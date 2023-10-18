package com.kmarinos.externalsqltablemonitoring.monitored.film;

import com.kmarinos.externalsqltablemonitoring.MonitoredDBClient;
import com.kmarinos.externalsqltablemonitoring.core.entity.EntityWatcher;
import com.kmarinos.externalsqltablemonitoring.monitored.film.domain.Film;
import com.kmarinos.externalsqltablemonitoring.monitored.film.domain.FilmRepository;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FilmWatcher implements EntityWatcher<Film> {

  @Autowired
  private FilmRepository filmRepository;
  @Autowired
  private MonitoredDBClient monitoredDBClient;

  @Override
  public Class<Film> getWatchedEntityClass() {
    return Film.class;
  }

  @Override
  public Collection<Film> fetchCurrentState() {
    return filmRepository.findAll();
  }

  @Override
  public Collection<Film> fetchChangedState() {
    return monitoredDBClient.fetchCurrentFilms();
  }
}
