package com.kmarinos.externalsqltablemonitoring.monitored.film.domain;

import com.kmarinos.externalsqltablemonitoring.core.entity.LoggableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
public class Film extends LoggableEntity {
  @Id
  @Include
  int id;
  String title;
  String description;

  @Override
  public String internalId() {
    return String.valueOf(id);
  }

  @Override
  public String businessId() {
    return title;
  }

  @Override
  public String businessObjectType() {
    return this.getClass().getSimpleName();
  }
}
