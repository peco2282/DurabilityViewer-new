package com.github.peco2282.durabilityviewer.config;

import java.util.function.Consumer;

public class ConfigurationSelectList extends ConfigurationItem {

  final String[] options;

  public ConfigurationSelectList(String key, String toolTip, String[] options, Object value, Object defaultValue) {
    this(key, toolTip, options, value, defaultValue, null);
  }

  public ConfigurationSelectList(String key, String toolTip, String[] options, Object value, Object defaultValue, Consumer<Object> changeHandler) {
    super(key, toolTip, value, defaultValue, 0, 0, changeHandler);
    this.options = options;
  }

  public String[] getOptions() {
    return options;
  }
}