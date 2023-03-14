package com.github.peco2282.durabilityviewer.event;

import com.github.peco2282.durabilityviewer.Durabilityviewer;
import com.github.peco2282.durabilityviewer.gui.GuiItemDurability;
import com.github.peco2282.durabilityviewer.handler.KeyHandler;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class KeyInputEvent {
  @SubscribeEvent
  public void keyPressed(final InputEvent.Key e) {
    if (KeyHandler.showHud.consumeClick()) {
      if (e.getModifiers() == 0) {
        GuiItemDurability.toggleVisibility();
      } else {
        Durabilityviewer.openConfigScreen();
      }
    }
  }
}