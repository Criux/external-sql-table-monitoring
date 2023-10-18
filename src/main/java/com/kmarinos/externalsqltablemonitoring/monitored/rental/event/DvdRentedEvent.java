package com.kmarinos.externalsqltablemonitoring.monitored.rental.event;

import com.kmarinos.externalsqltablemonitoring.core.entity.EntityChangedEvent;
import com.kmarinos.externalsqltablemonitoring.monitored.film.domain.Film;
import com.kmarinos.externalsqltablemonitoring.monitored.rental.domain.Rental;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DvdRentedEvent extends EntityChangedEvent<Rental> {
Film film;
}
