package com.kmarinos.externalsqltablemonitoring;

import com.kmarinos.externalsqltablemonitoring.core.TimeParser;
import com.kmarinos.externalsqltablemonitoring.model.User;
import com.kmarinos.externalsqltablemonitoring.monitored.film.domain.Film;
import com.kmarinos.externalsqltablemonitoring.sql.SQLClient;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MonitoredDBClient {
  @Value("${monitoring.downtime-tolerance}:1 hour")
  String downtimeTolerance;

  private final SQLClient sqlClient;
  private LocalDateTime filmTS;
  private LocalDateTime rentalTS;

  @PostConstruct
  public void init(){
    filmTS = LocalDateTime.now().minus(TimeParser.parseTemporalAmount(downtimeTolerance));
    rentalTS = LocalDateTime.now().minus(TimeParser.parseTemporalAmount(downtimeTolerance));
  }

  public Optional<User> getUserFromUsername(String username){
    throw new RuntimeException("NOT YET IMPLEMENTED");
  }
  public List<User>getAllRegisteredUsers(){
    throw new RuntimeException("NOT YET IMPLEMENTED");
  }
  public String getPreferredLangaugeForUser(String username){
    throw new RuntimeException("NOT YET IMPLEMENTED");
  }

  public Collection<Film> fetchCurrentFilms() {
    throw new RuntimeException("NOT YET IMPLEMENTED");
  }

  public Film getFilmFromRental(int id) {
    throw new RuntimeException("NOT YET IMPLEMENTED");
  }

  public String getPreferredLanguageForUser(String username) {
    throw new RuntimeException("NOT YET IMPLEMENTED");
  }

  public Optional<Object> getuserFromSession(String userSessionId) {
    throw new RuntimeException("NOT YET IMPLEMENTED");
  }
}
