package com.github.peco2282.durabilityviewer.sound;

import com.github.peco2282.durabilityviewer.Durabilityviewer;
import com.github.peco2282.durabilityviewer.handler.ConfigurationHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;

/**
 * @author gbl
 */
@SuppressWarnings("unused")
public class ItemBreakingWarner {
  private static SoundEvent sound;
  private int lastDurability;
  private ItemStack lastStack;

  public ItemBreakingWarner() {
    lastDurability = 1000;
    lastStack = null;
    ResourceLocation location;

    if (sound == null) {
      location = new ResourceLocation(Durabilityviewer.MODID, "tool_breaking");
//      sound = new SoundEvent(location);
      sound = SoundEvent.createVariableRangeEvent(location);
    }
  }

  public static void playWarningSound() {
    // System.out.append("playing warning sound");
    if (Minecraft.getInstance().player != null) {
      Minecraft.getInstance().player.playSound(sound, 100, 100);
    }
  }

  public boolean checkBreaks(ItemStack stack) {
    lastStack = stack;
    if (stack == null || !stack.isDamageableItem())
      return false;
    int newDurability = stack.getMaxDamage() - stack.getDamageValue();
    if (newDurability < lastDurability
        && newDurability < ConfigurationHandler.getMinDurability()
        && newDurability * 100 / ConfigurationHandler.getMinPercent() < stack.getMaxDamage()) {
      lastDurability = newDurability;
      return true;
    }
    lastDurability = newDurability;
    return false;
  }
}