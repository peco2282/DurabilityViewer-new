package com.github.peco2282.durabilityviewer.config;

import java.util.List;

public interface IConfiguration {
  List<String> getKeys();

  Object getValue(String option);

  void setValue(String option, Object value);

  Object getDefault(String option);

  Object getMin(String option);

  Object getMax(String option);

  String getTooltip(String option);

  boolean isSelectList(String option);

  String[] getListOptions(String option);
}
