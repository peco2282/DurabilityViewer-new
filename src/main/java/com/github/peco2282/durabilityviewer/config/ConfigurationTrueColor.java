package com.github.peco2282.durabilityviewer.config;

import java.util.Map;

public class ConfigurationTrueColor {
  public int red, green, blue;
  public String type;             // for gson

  public ConfigurationTrueColor(int rgb) {
    red = (rgb >> 16) & 0xff;
    green = (rgb >> 8) & 0xff;
    blue = rgb & 0xff;
    this.type = this.getClass().getSimpleName();
  }

  public static ConfigurationTrueColor fromJsonMap(Map<String, ?> map) {
    ConfigurationTrueColor result = new ConfigurationTrueColor(0);
    try {
      result.red = (int) (double) map.get("red");
      result.green = (int) (double) map.get("green");
      result.blue = (int) (double) map.get("blue");
    } catch (Exception ex) {
      System.err.println("Exception " + ex + " when reading TrueColor from config");
    }
    return result;
  }

  public int getInt() {
    return red << 16 | green << 8 | blue;
  }
}