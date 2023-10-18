package com.kmarinos.externalsqltablemonitoring.monitored.rental.domain;

import com.kmarinos.externalsqltablemonitoring.core.entity.LoggableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Rental extends LoggableEntity {
  @Id
  @Include
  int id;
  int filmId;
  LocalDateTime rentalDate;
  LocalDateTime returnDate;
  int customerId;


  @Override
  public String internalId() {
    return String.valueOf(id);
  }

  @Override
  public String businessId() {
    return internalId();
  }

  @Override
  public String businessObjectType() {
    return this.getClass().getSimpleName();
  }
}
