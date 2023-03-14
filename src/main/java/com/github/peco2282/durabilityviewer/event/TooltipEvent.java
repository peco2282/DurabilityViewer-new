package com.github.peco2282.durabilityviewer.event;

import com.github.peco2282.durabilityviewer.handler.ConfigurationHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

@SuppressWarnings("SuspiciousMethodCalls")
public class TooltipEvent {
  @SubscribeEvent
  public void onItemTooltip(final ItemTooltipEvent event) {
    if (!event.getFlags().isAdvanced() && !event.getItemStack().isEmpty()) {
      final ItemStack itemStack = event.getItemStack();
      if (itemStack.isDamaged()) {
        ConfigurationHandler.getInstance();
        @NotNull final String toolTip = ConfigurationHandler.getTooltipColor() +
            I18n.get("tooltip.durability",
                (itemStack.getMaxDamage() - itemStack.getDamageValue()) +
                    " / " +
                    itemStack.getMaxDamage()
            );
        if (!event.getToolTip().contains(toolTip)) {
          event.getToolTip().add(Component.literal(toolTip));
        }
      }
    }

    if (Screen.hasAltDown()) {      // hasAltDown
      CompoundTag tag = event.getItemStack().getTag();
      if (tag != null) {
        addCompoundTag("", event.getToolTip(), tag);
      }
    }
  }

  private void addCompoundTag(String prefix, List<Component> list, CompoundTag tag) {
    TreeSet<String> sortedKeys = new TreeSet<>(tag.getAllKeys());
    for (String key : sortedKeys) {
      Tag elem = tag.get(key);
      switch (Objects.requireNonNull(elem).getId()) {
        case 2 -> list.add(Component.literal(prefix + key + ": §2" + tag.getShort(key)));
        case 3 -> list.add(Component.literal(prefix + key + ": §3" + tag.getInt(key)));
        case 6 -> list.add(Component.literal(prefix + key + ": §6" + tag.getDouble(key)));
        case 8 -> list.add(Component.literal(prefix + key + ": §8" + tag.getString(key)));
        case 9 -> list.add(Component.literal(prefix + key + ": §9List, " + ((ListTag) elem).size() + " items"));
        case 10 -> {
          list.add(Component.literal(prefix + key + ": §aCompound"));
          if (Screen.hasShiftDown()) {      // hasShiftDown
            addCompoundTag(prefix + "    ", list, (CompoundTag) elem);
          }
        }
        default -> list.add(Component.literal(prefix + key + ": Type " + elem.getId()));
      }
    }
  }
}