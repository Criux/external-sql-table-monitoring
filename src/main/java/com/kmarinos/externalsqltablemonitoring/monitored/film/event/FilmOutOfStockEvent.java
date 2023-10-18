package com.kmarinos.externalsqltablemonitoring.monitored.film.event;

import com.kmarinos.externalsqltablemonitoring.core.entity.EntityChangedEvent;
import com.kmarinos.externalsqltablemonitoring.monitored.film.domain.Film;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FilmOutOfStockEvent extends EntityChangedEvent<Film> {

}
