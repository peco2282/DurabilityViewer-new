package com.github.peco2282.durabilityviewer.config;

import java.util.Map;

public class ConfigurationMinecraftColor {
  public int colorIndex;
  public String type;         // for gson

  public ConfigurationMinecraftColor(int index) {
    colorIndex = index;
    this.type = this.getClass().getSimpleName();
  }

  public static ConfigurationMinecraftColor fromJsonMap(Map<String, ?> map) {
    ConfigurationMinecraftColor result = new ConfigurationMinecraftColor(0);
    try {
      result.colorIndex = (int) (double) map.get("colorIndex");
    } catch (Exception ex) {
      System.err.println("Exception " + ex + " when reading MinecraftColor from config");
    }
    return result;
  }

  @Override
  public String toString() {
    return this.getClass().getName() + "[index=" + colorIndex + "]";
  }
}