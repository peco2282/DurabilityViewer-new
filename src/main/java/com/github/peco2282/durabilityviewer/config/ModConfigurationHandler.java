package com.github.peco2282.durabilityviewer.config;

import com.github.peco2282.durabilityviewer.event.ConfigChangedEvent;

public interface ModConfigurationHandler {
  void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event);

  default void onConfigChanging(ConfigChangedEvent.OnConfigChangingEvent event) {
  }

  Configuration getConfig();

  default IConfiguration getIConfig() {
    return getConfig();
  }
}
