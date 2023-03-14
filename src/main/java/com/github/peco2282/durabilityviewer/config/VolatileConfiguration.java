package com.github.peco2282.durabilityviewer.config;

import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gbl
 */
@SuppressWarnings("unused")
public class VolatileConfiguration implements IConfiguration {

  private final Map<String, ConfigurationItem> items;

  public VolatileConfiguration() {
    items = new HashMap<>();
  }

  public void addItem(ConfigurationItem item) {
    items.put(item.key, item);
  }

  @Override
  public List<String> getKeys() {
    return Lists.newArrayList(items.keySet());
  }

  @Override
  public Object getValue(String option) {
    return items.get(option).getValue();
  }

  @Override
  public void setValue(String option, Object value) {
    ConfigurationItem item = items.get(option);
    if (item == null) {
      return;
    }
    item.setValue(value);
  }

  @Override
  public Object getDefault(String option) {
    return items.get(option).defaultValue;
  }

  @Override
  public Object getMin(String option) {
    return items.get(option).minValue;
  }

  @Override
  public Object getMax(String option) {
    return items.get(option).maxValue;
  }

  @Override
  public String getTooltip(String option) {
    return items.get(option).toolTip;
  }

  @Override
  public boolean isSelectList(String option) {
    return items.get(option) instanceof ConfigurationSelectList;
  }

  @Override
  public String[] getListOptions(String option) {
    ConfigurationItem item = items.get(option);
    if (item instanceof ConfigurationSelectList) {
      return ((ConfigurationSelectList) item).getOptions();
    }
    return null;
  }
}