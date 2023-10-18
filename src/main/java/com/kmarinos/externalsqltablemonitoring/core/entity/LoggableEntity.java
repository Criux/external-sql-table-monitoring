package com.kmarinos.externalsqltablemonitoring.core.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class LoggableEntity {
  private Map<String,String> metadata = new HashMap<>();
  private LoggableEntity parent;
  public abstract String internalId();
  public abstract String businessId();
  public abstract String businessObjectType();

  @Override
  public boolean equals(Object o){
    if(this==o) return true;
    LoggableEntity that = (LoggableEntity) o;
    return Objects.equals(this.businessId(),that.businessId());
  }
  @Override
  public int hashCode(){return Objects.hash(businessId());}

  public void addMetadata(String name,String value){
    if(name!=null&&!name.isBlank()){
      metadata.put(name,value);
    }
  }
  public String metadata(){
    return metadata.entrySet().stream().map(e->e.getKey()+":"+e.getValue()).collect(Collectors.joining(",","[","]"));
  }
  public TreeInfo chain(Function<LoggableEntity,String>...infoToLog){return chain(new ArrayList<>(),infoToLog);}
  public TreeInfo chain(List<LoggableEntity> alreadyVisited,Function<LoggableEntity,String>...infoToLog){
    List<String[]> toReturn = new ArrayList<>();
    alreadyVisited.add(this);
    String[] textLine = Arrays.stream(infoToLog).map(toLog->toLog.apply(this)).collect(Collectors.toList()).toArray(String[]::new);
    toReturn.add(textLine);
    var info = new TreeInfo(toReturn);
    if(parent != null&&!alreadyVisited.contains(parent)){
      info.merge(parent.chain(alreadyVisited, infoToLog));
    }
    return info;
  }
  public void setParent(LoggableEntity parent){this.parent=parent;}

  @Data
  private static class TreeInfo{
    List<String[]> rowsOfFields;
    IntSupplier pos;
    public static Function<TreeInfo,String> position = info->"["+info.getPos().getAsInt()+"]";
    public TreeInfo(List<String[]>rowsOfFields){this.rowsOfFields=rowsOfFields;}
    public void merge(TreeInfo toMerge){this.getRowsOfFields().addAll(toMerge.getRowsOfFields());}
    public List<String[]>andGet(){return rowsOfFields;}
    public void andLog(String message, BiConsumer<String,Object[]>consumer){andLog(message,info->null,consumer);}
    public void andLog(String message, Function<TreeInfo,String>prefixProvider,BiConsumer<String,Object[]>consumer){
      pos = new AtomicInteger(0)::incrementAndGet;
      rowsOfFields.forEach(row->consumer.accept(getPrefixFormatted(prefixProvider)+message,row));
    }
    private String getPrefixFormatted(@NonNull Function<TreeInfo,String>prefixProvider){
      String prefix = prefixProvider.apply(this);
      if(prefix==null||prefix.isEmpty()){
        prefix = "";
      }else{
        prefix+=" ";
      }
      return prefix;
    }
  }
  public static class MyLoggableEntity extends LoggableEntity{
    @Override
    public boolean equals(Object o){return super.equals(o);}
    @Override
    public String internalId(){return "internalId";}
    @Override
    public String businessId(){return "businessId";}
    @Override
    public String businessObjectType(){return "businessObjectType";}
  }
  public static class MyOtherLoggableEntity extends LoggableEntity{
    @Override
    public boolean equals(Object o){return super.equals(o);}
    @Override
    public String internalId(){return "other internalId";}
    @Override
    public String businessId(){return "other businessId";}
    @Override
    public String businessObjectType(){return "businessObjectType";}
  }
  /*
  HOW TO USE
   */
  public static void main(String[] args) {
    var toLog = new MyLoggableEntity();
    var toLog2 = new MyOtherLoggableEntity();
    toLog.setParent(toLog2);
    toLog2.setParent(toLog);
    toLog.chain(LoggableEntity::businessId,LoggableEntity::internalId).andLog(
        "Entity with {} and {}",TreeInfo.position,log::info
    );
  }

}
