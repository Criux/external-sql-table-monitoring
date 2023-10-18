package com.kmarinos.externalsqltablemonitoring.model;

import java.util.Comparator;

public class EmailSettingsComparator implements Comparator<EmailSetting> {

  @Override
  public int compare(EmailSetting o1, EmailSetting o2) {
    if(o1==null||o1.getTemplate()==null||o1.getTemplate().getSortOrder()==null){
      return 1;
    }
    if(o2==null||o2.getTemplate()==null||o2.getTemplate().getSortOrder()==null){
      return -1;
    }
    var order1 = o1.getTemplate().getSortOrder();
    var order2 = o2.getTemplate().getSortOrder();
    return order1.compareTo(order2);
  }
}
