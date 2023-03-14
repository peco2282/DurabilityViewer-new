package com.github.peco2282.durabilityviewer.event;


public class ConfigChangedEvent {

  public static class OnConfigChangedEvent extends ConfigChangedEvent {
    String mod;

    public OnConfigChangedEvent(String mod) {
      this.mod = mod;
    }

    public String getModID() {
      return mod;
    }

  }

  public static class OnConfigChangingEvent extends ConfigChangedEvent {
    String mod, item;
    Object newValue;

    public OnConfigChangingEvent(String mod, String item, Object newValue) {
      this.mod = mod;
      this.item = item;
      this.newValue = newValue;
    }

    public String getModID() {
      return mod;
    }

    public String getItem() {
      return item;
    }

    public Object getNewValue() {
      return newValue;
    }
  }
}