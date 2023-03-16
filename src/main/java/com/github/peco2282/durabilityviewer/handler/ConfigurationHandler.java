package com.github.peco2282.durabilityviewer.handler;

import com.github.peco2282.durabilityviewer.Durabilityviewer;
import com.github.peco2282.durabilityviewer.config.Configuration;
import com.github.peco2282.durabilityviewer.config.ModConfigurationHandler;
import com.github.peco2282.durabilityviewer.event.ConfigChangedEvent;
import com.github.peco2282.durabilityviewer.gui.Corner;
import net.minecraft.ChatFormatting;

import java.io.File;

@SuppressWarnings("unused")
public class ConfigurationHandler implements ModConfigurationHandler {
  private static final String[] warnModes = {
      "durabilityviewer.config.warnmode.none",
      "durabilityviewer.config.warnmode.sound",
      "durabilityviewer.config.warnmode.visual",
      "durabilityviewer.config.warnmode.both",
  };
  private static ConfigurationHandler instance;

  private Configuration config;
  private ChatFormatting tooltipColor;
  private String configFileName;

  private int corner = 0;
  private int color = 5;
  private boolean effectDuration;
  private int minPercent = 10;
  private int minDurability = 100;
  private boolean showPlayerServerName;
  private int showDamageOverPercent;
  private boolean armorAroundHotbar;
  private boolean showChestIcon;
  private boolean showPercentValues;
  private int warnMode;

  public static ConfigurationHandler getInstance() {
    if (instance == null)
      instance = new ConfigurationHandler();
    return instance;
  }

  public static String getConfigFileName() {
    return getInstance().configFileName;
  }

  public static ChatFormatting getTooltipColor() {
    return getInstance().tooltipColor;
  }

  public static Corner getCorner() {
    return Corner.values()[getInstance().corner];
  }

  public static boolean showEffectDuration() {
    return getInstance().effectDuration;
  }

  public static int getMinPercent() {
    return getInstance().minPercent;
  }

  public static int getMinDurability() {
    return getInstance().minDurability;
  }

  public static boolean showPlayerServerName() {
    return getInstance().showPlayerServerName;
  }

  public static int showDamageOverPercent() {
    return getInstance().showDamageOverPercent;
  }

  public static boolean getArmorAroundHotbar() {
    return getInstance().armorAroundHotbar;
  }

  public static boolean getShowChestIcon() {
    return getInstance().showChestIcon;
  }

  public static boolean getShowPercentValues() {
    return getInstance().showPercentValues;
  }

  public static int getWarnMode() {
    return getInstance().warnMode;
  }

  public void load(final File configFile) {
    if (config == null) {
      config = new Configuration(configFile);
      configFileName = configFile.getPath();
      loadConfig();
    }
  }

  public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
    if (event.getModID().equals(Durabilityviewer.MODID)) {
      loadConfig();
    }
  }

  @Override
  public void onConfigChanging(ConfigChangedEvent.OnConfigChangingEvent event) {
    if (event.getModID().equals(Durabilityviewer.MODID)) {
      switch (event.getItem()) {
        case "durabilityviewer.config.corner" -> corner = (int) (Integer) (event.getNewValue());
        case "durabilityviewer.config.armorhotbar" -> armorAroundHotbar = (boolean) (Boolean) (event.getNewValue());
        case "durabilityviewer.config.showfreeslots" -> showChestIcon = (boolean) (Boolean) (event.getNewValue());
      }
    }
  }

  private void loadConfig() {

    config.migrate("HUD Corner", "durabilityviewer.config.corner");
    config.migrate("Effect Duration", "durabilityviewer.config.effectduration");
    config.migrate("Percent to show damage", "durabilityviewer.config.showdamagepercent");
    config.migrate("Tooltip Color", "durabilityviewer.config.tooltipcolor");
    config.migrate("Minimum Percent", "durabilityviewer.config.minpercent");
    config.migrate("Minimum Durability", "durabilityviewer.config.mindurability");
    config.migrate("Set window title", "durabilityviewer.config.setwindowtitle");
    config.migrate("Show all trinkets", "durabilityviewer.config.showalltrinkets");
    config.migrate("Armor around hotbar", "durabilityviewer.config.armorhotbar");
    config.migrate("Show chest icon", "durabilityviewer.config.showfreeslots");

    corner = config.getSelection("durabilityviewer.config.corner", Configuration.CATEGORY_CLIENT, corner,
        new String[]{
            "durabilityviewer.config.bottom_right",
            "durabilityviewer.config.bottom_left",
            "durabilityviewer.config.top_right",
            "durabilityviewer.config.top_left",
        },
        "durabilityviewer.config.tt.corner");
    armorAroundHotbar = config.getBoolean("durabilityviewer.config.armorhotbar", Configuration.CATEGORY_CLIENT, armorAroundHotbar, "durabilityviewer.config.tt.armorhotbar");
    color = config.getInt("durabilityviewer.config.tooltipcolor", Configuration.CATEGORY_CLIENT, color, 0, 15, "durabilityviewer.config.tt.tooltipcolor");
    effectDuration = config.getBoolean("durabilityviewer.config.effectduration", Configuration.CATEGORY_CLIENT, true, "durabilityviewer.config.tt.effectduration");
    minPercent = config.getInt("durabilityviewer.config.minpercent", Configuration.CATEGORY_CLIENT, minPercent, 1, 100, "durabilityviewer.config.tt.minpercent");
    minDurability = config.getInt("durabilityviewer.config.mindurability", Configuration.CATEGORY_CLIENT, minDurability, 1, 1500, "durabilityviewer.config.tt.mindurability");
    showPlayerServerName = config.getBoolean("durabilityviewer.config.setwindowtitle", Configuration.CATEGORY_CLIENT, true, "durabilityviewer.config.tt.setwindowtitle");
    showDamageOverPercent = config.getInt("durabilityviewer.config.showdamagepercent", Configuration.CATEGORY_CLIENT, 80, 0, 100, "durabilityviewer.config.tt.showdamagepercent");
    showChestIcon = config.getBoolean("durabilityviewer.config.showfreeslots", Configuration.CATEGORY_CLIENT, true, "durabilityviewer.config.tt.showfreeslots");
    // showAllTrinkets = config.getBoolean("durabilityviewer.config.showalltrinkets", Configuration.CATEGORY_CLIENT, true, "durabilityviewer.config.tt.showalltrinkets");
    showPercentValues = config.getBoolean("durabilityviewer.config.percentvalues", Configuration.CATEGORY_CLIENT, false, "durabilityviewer.config.tt.percentvalues");
    warnMode = config.getSelection("durabilityviewer.config.warnmode", Configuration.CATEGORY_CLIENT, 1, warnModes, "durabilityviewer.config.tt.warnmode");

    tooltipColor = ChatFormatting.getById(color);
    if (config.hasChanged())
      config.save();
  }

  public Configuration getConfig() {
    return getInstance().config;
  }
}