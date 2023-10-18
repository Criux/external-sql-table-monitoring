package com.kmarinos.externalsqltablemonitoring.exceptionhandling.exceptions;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang.StringUtils;

public class InputValidationException extends RuntimeException{
  public InputValidationException(Class clazz,Map<String,String> errorMessages){
    super(InputValidationException.generateMessage(clazz.getSimpleName(),errorMessages));
  }
  private static String generateMessage(String entity, Map<String,String> searchParams){
    return StringUtils.capitalize(entity)+" is not valid. Errors in fields: "+searchParams.entrySet().stream().map(e->""+e.getKey()+"("+e.getValue()+")").collect(
        Collectors.joining(", "));
  }

}
