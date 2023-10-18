package com.kmarinos.externalsqltablemonitoring.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
  @Id
  @EqualsAndHashCode.Include
  int id;
  String firstname;
  String lastname;
  String username;
  boolean employee;
  String companyType;
  String email;

}
