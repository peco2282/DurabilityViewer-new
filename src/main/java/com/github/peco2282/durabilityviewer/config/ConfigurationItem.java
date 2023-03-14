package com.github.peco2282.durabilityviewer.config;

import java.util.function.Consumer;

public class ConfigurationItem {

  public String key;
  public String toolTip;
  public Object defaultValue;
  public Object minValue, maxValue;
  public Consumer<Object> changeHandler;
  private Object value;

  public ConfigurationItem(String key, String toolTip, Object value, Object defaultValue, Object minValue, Object maxValue) {
    this(key, toolTip, value, defaultValue, minValue, maxValue, null);
  }

  public ConfigurationItem(String key, String toolTip, Object value, Object defaultValue, Object minValue, Object maxValue, Consumer<Object> handler) {
    this.key = key;
    this.toolTip = toolTip;
    this.value = value;
    this.defaultValue = defaultValue;
    this.minValue = minValue;
    this.maxValue = maxValue;
    this.changeHandler = handler;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object o) {
    this.value = o;
    if (changeHandler != null) {
      changeHandler.accept(o);
    }
  }
}