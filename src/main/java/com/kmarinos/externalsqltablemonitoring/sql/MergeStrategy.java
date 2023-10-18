package com.kmarinos.externalsqltablemonitoring.sql;

import java.util.function.BinaryOperator;

public enum MergeStrategy {
  KEEP_ORIGINAL,ALWAYS_REPLACE,THROW_EXCEPTION;
  public static <T>BinaryOperator<T>getStrategyOperator(MergeStrategy mergeStrategy){
    return switch (mergeStrategy){
      case KEEP_ORIGINAL -> (a,b)->a;
      case ALWAYS_REPLACE -> (a,b)->b;
      case THROW_EXCEPTION -> null;
    };
  }
}
