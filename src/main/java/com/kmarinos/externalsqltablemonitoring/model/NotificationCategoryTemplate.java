package com.kmarinos.externalsqltablemonitoring.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class NotificationCategoryTemplate {

  @Id
  @UuidGenerator
  @Include
  String id;
  String nameKey;
  Integer sortOrder;
  @ElementCollection
  private Map<String,String> nameTranslations;
}
