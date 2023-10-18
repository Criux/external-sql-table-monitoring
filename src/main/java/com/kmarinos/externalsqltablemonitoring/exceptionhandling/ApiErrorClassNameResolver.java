package com.kmarinos.externalsqltablemonitoring.exceptionhandling;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;

public class ApiErrorClassNameResolver extends TypeIdResolverBase {

  @Override
  public String idFromValue(Object o) {
    return "error";
  }

  @Override
  public String idFromValueAndType(Object o, Class<?> aClass) {
    return idFromValue(o);
  }

  @Override
  public Id getMechanism() {
    return Id.CUSTOM;
  }
}
