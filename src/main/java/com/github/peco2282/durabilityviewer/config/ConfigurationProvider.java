/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.peco2282.durabilityviewer.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author gbl
 */
@SuppressWarnings("unused")
public class ConfigurationProvider {

  static ConfigurationProvider instance;

  private final Map<String, ModConfigurationHandler> knownMods;

  private ConfigurationProvider() {
    knownMods = new HashMap<>();
  }

  public static ConfigurationProvider getInstance() {
    if (instance == null)
      instance = new ConfigurationProvider();
    return instance;
  }

  public static void register(String modName, ModConfigurationHandler handler) {
    getInstance().knownMods.put(modName, handler);
  }

  public static boolean hasMod(String modName) {
    return getInstance().knownMods.containsKey(modName);
  }

  public static ModConfigurationHandler getHandler(String modName) {
    return getInstance().knownMods.get(modName);
  }

  public static File getSuggestedFile(String modid) {
    File dir = new File("config");
    if (!(dir.exists())) {
      boolean mkdir = dir.mkdir();
    }
    if (!(dir.isDirectory())) {
      System.err.println("Can't make directory " + dir.getAbsolutePath() + ", config subsystem will not work");
    }
    return new File(dir, modid + ".json");
  }

  public static Set<String> getRegisteredMods() {
    return getInstance().knownMods.keySet();
  }
}