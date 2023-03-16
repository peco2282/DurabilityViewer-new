package com.github.peco2282.durabilityviewer.gui;

import com.github.peco2282.durabilityviewer.Durabilityviewer;
import com.github.peco2282.durabilityviewer.handler.ConfigurationHandler;
import com.github.peco2282.durabilityviewer.indicator.InventorySlotsIndicator;
import com.github.peco2282.durabilityviewer.indicator.ItemCountIndicator;
import com.github.peco2282.durabilityviewer.indicator.ItemDamageIndicator;
import com.github.peco2282.durabilityviewer.indicator.ItemIndicator;
import com.github.peco2282.durabilityviewer.sound.ItemBreakingWarner;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Collection;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.glfwSetWindowTitle;

@SuppressWarnings("unused")
public class GuiItemDurability extends Gui {
  private static final int iconWidth = 16;
  private static final int iconHeight = 16;
  private static final int spacing = 2;
  private static boolean visible;
  private final Minecraft minecraft;
  private final Font fontRenderer;
  private final ItemRenderer itemRenderer;
  private long lastWarningTime;
  private ItemStack lastWarningItem;
  private final ItemBreakingWarner mainHandWarner;
  private final ItemBreakingWarner offHandWarner;
  private final ItemBreakingWarner helmetWarner;
  private final ItemBreakingWarner chestWarner;
  private final ItemBreakingWarner pantsWarner;
  private final ItemBreakingWarner bootsWarner;

  public GuiItemDurability() {
    super(Minecraft.getInstance(), Minecraft.getInstance().getItemRenderer());
    minecraft = Minecraft.getInstance();
    fontRenderer = minecraft.font;
    itemRenderer = minecraft.getItemRenderer();
    visible = true;

    mainHandWarner = new ItemBreakingWarner();
    offHandWarner = new ItemBreakingWarner();
    helmetWarner = new ItemBreakingWarner();
    chestWarner = new ItemBreakingWarner();
    pantsWarner = new ItemBreakingWarner();
    bootsWarner = new ItemBreakingWarner();
  }

  public static void toggleVisibility() {
    visible = !visible;
  }

  private int getInventoryArrowCount() {
    int arrows = 0;
    for (final ItemStack stack : Objects.requireNonNull(minecraft.player).getInventory().items) {
      if (isArrow(stack)) {
        arrows += stack.getCount();
      }
    }
    return arrows;
  }

  private ItemStack getFirstArrowStack() {
    if (isArrow(Objects.requireNonNull(minecraft.player).getItemInHand(InteractionHand.OFF_HAND))) {
      return minecraft.player.getItemInHand(InteractionHand.OFF_HAND);
    }
    if (isArrow(minecraft.player.getItemInHand(InteractionHand.MAIN_HAND))) {
      return minecraft.player.getItemInHand(InteractionHand.MAIN_HAND);
    }
    int size = minecraft.player.getInventory().getContainerSize();
    for (int i = 0; i < size; ++i) {
      final ItemStack itemstack = minecraft.player.getInventory().getItem(i);
      if (this.isArrow(itemstack)) {
        return itemstack;
      }
    }
    return null;
  }

  private boolean isArrow(final ItemStack stack) {
    return !stack.isEmpty() && stack.getItem() instanceof ArrowItem;
  }

  @SubscribeEvent(priority = EventPriority.NORMAL)
  public void onRender(final RenderGuiOverlayEvent.Post event) {

    // This needs to be done before everything else to make sure
    // the title change that occurs when logging off gets through.
    String newTitle = Durabilityviewer.changedWindowTitle();
    if (newTitle != null) {
      glfwSetWindowTitle(minecraft.getWindow().getWindow(), newTitle);
    }

    if (!visible
        || event.isCanceled()
        // ||  minecraft.player.abilities.isCreativeMode
        || !(event.getOverlay().equals(VanillaGuiOverlay.HOTBAR.type())))
      return;

    LocalPlayer effectivePlayer = minecraft.player;
    ItemStack needToWarn = null;

    // @TODO: remove duplicate code
    ItemIndicator mainHand = new ItemDamageIndicator(Objects.requireNonNull(effectivePlayer).getItemBySlot(EquipmentSlot.MAINHAND));
    ItemIndicator offHand = new ItemDamageIndicator(effectivePlayer.getItemBySlot(EquipmentSlot.OFFHAND));
    ItemIndicator boots = new ItemDamageIndicator(effectivePlayer.getItemBySlot(EquipmentSlot.FEET));
    ItemIndicator leggings = new ItemDamageIndicator(effectivePlayer.getItemBySlot(EquipmentSlot.LEGS));
    ItemIndicator chestplate = new ItemDamageIndicator(effectivePlayer.getItemBySlot(EquipmentSlot.CHEST));
    ItemIndicator helmet = new ItemDamageIndicator(effectivePlayer.getItemBySlot(EquipmentSlot.HEAD));
    ItemIndicator arrows = null;
    ItemIndicator invSlots = ConfigurationHandler.getShowChestIcon() ? new InventorySlotsIndicator(minecraft.player.getInventory()) : null;

    if (mainHandWarner.checkBreaks(effectivePlayer.getItemBySlot(EquipmentSlot.MAINHAND)))
      needToWarn = effectivePlayer.getItemBySlot(EquipmentSlot.MAINHAND);
    if (needToWarn == null && offHandWarner.checkBreaks(effectivePlayer.getItemBySlot(EquipmentSlot.OFFHAND)))
      needToWarn = effectivePlayer.getItemBySlot(EquipmentSlot.OFFHAND);
    if (needToWarn == null && bootsWarner.checkBreaks(effectivePlayer.getItemBySlot(EquipmentSlot.FEET)))
      needToWarn = effectivePlayer.getItemBySlot(EquipmentSlot.FEET);
    if (needToWarn == null && pantsWarner.checkBreaks(effectivePlayer.getItemBySlot(EquipmentSlot.LEGS)))
      needToWarn = effectivePlayer.getItemBySlot(EquipmentSlot.LEGS);
    if (needToWarn == null && chestWarner.checkBreaks(effectivePlayer.getItemBySlot(EquipmentSlot.CHEST)))
      needToWarn = effectivePlayer.getItemBySlot(EquipmentSlot.CHEST);
    if (needToWarn == null && helmetWarner.checkBreaks(effectivePlayer.getItemBySlot(EquipmentSlot.HEAD)))
      needToWarn = effectivePlayer.getItemBySlot(EquipmentSlot.HEAD);
    if (needToWarn != null) {
      if ((ConfigurationHandler.getWarnMode() & 1) == 1) {
        ItemBreakingWarner.playWarningSound();
      }
      lastWarningTime = System.currentTimeMillis();
      lastWarningItem = needToWarn;
    }

    if (mainHand.getItemStack().getItem() instanceof BowItem || offHand.getItemStack().getItem() instanceof BowItem) {
      arrows = new ItemCountIndicator(getFirstArrowStack(), getInventoryArrowCount());
    }
    Window mainWindow = Minecraft.getInstance().getWindow();
    PoseStack stack = event.getPoseStack();

    RenderSize armorSize, toolsSize;
    armorSize = this.renderItems(stack, 0, 0, false, RenderPos.left, 0, boots, leggings, chestplate, helmet);
    toolsSize = this.renderItems(stack, 0, 0, false, RenderPos.right, 0, invSlots, mainHand, offHand, arrows);

    int totalHeight = (Math.max(toolsSize.height, armorSize.height));
    int totalWidth = (Math.max(toolsSize.width, armorSize.width));
    int xposArmor, xposTools, ypos, xpos;

    switch (ConfigurationHandler.getCorner()) {
      case TOP_LEFT -> {
        xposArmor = 5;
        xposTools = 5 + armorSize.width;
        ypos = 5;
      }
      case TOP_RIGHT -> {
        xposArmor = mainWindow.getGuiScaledWidth() - 5 - armorSize.width;
        xposTools = mainWindow.getGuiScaledWidth() - 5 - armorSize.width - toolsSize.width;
        ypos = 60;   // below buff/debuff effects
      }
      case BOTTOM_LEFT -> {
        xposArmor = 5;
        xposTools = 5 + armorSize.width;
        ypos = mainWindow.getGuiScaledHeight() - 5 - totalHeight;
      }
      case BOTTOM_RIGHT -> {
        xposArmor = mainWindow.getGuiScaledWidth() - 5 - armorSize.width;
        xposTools = mainWindow.getGuiScaledWidth() - 5 - armorSize.width - toolsSize.width;
        ypos = mainWindow.getGuiScaledHeight() - 5 - totalHeight;
      }
      default -> {
        return;
      }
    }

    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

    if (ConfigurationHandler.getArmorAroundHotbar()) {
      int leftOffset = -120;
      int rightOffset = 100;
      if (!effectivePlayer.getItemBySlot(EquipmentSlot.OFFHAND).isEmpty()) {
        if (minecraft.options.mainHand().get() == HumanoidArm.RIGHT) {
          leftOffset -= 20;
        } else {
          rightOffset += 20;
        }
      }
      int helmetTextWidth = fontRenderer.width(helmet.getDisplayValue());
      int chestTextWidth = fontRenderer.width(chestplate.getDisplayValue());
      this.renderItems(stack, mainWindow.getGuiScaledWidth() / 2 + leftOffset - helmetTextWidth, mainWindow.getGuiScaledHeight() - iconHeight * 2 - 2, true, RenderPos.left, helmetTextWidth + iconWidth + spacing, helmet);
      this.renderItems(stack, mainWindow.getGuiScaledWidth() / 2 + leftOffset - chestTextWidth, mainWindow.getGuiScaledHeight() - iconHeight - 2, true, RenderPos.left, chestTextWidth + iconWidth + spacing, chestplate);
      this.renderItems(stack, mainWindow.getGuiScaledWidth() / 2 + rightOffset, mainWindow.getGuiScaledHeight() - iconHeight * 2 - 2, true, RenderPos.right, armorSize.width, leggings);
      this.renderItems(stack, mainWindow.getGuiScaledWidth() / 2 + rightOffset, mainWindow.getGuiScaledHeight() - iconHeight - 2, true, RenderPos.right, armorSize.width, boots);
      if (ConfigurationHandler.getCorner().isRight()) {
        xposTools += armorSize.width;
      } else {
        xposTools -= armorSize.width;
      }
    } else {
      this.renderItems(stack, xposArmor, ypos, true, ConfigurationHandler.getCorner().isLeft() ? RenderPos.left : RenderPos.right, armorSize.width, helmet, chestplate, leggings, boots);
    }
    this.renderItems(stack, xposTools, ypos, true, ConfigurationHandler.getCorner().isRight() ? RenderPos.right : RenderPos.left, toolsSize.width, invSlots, mainHand, offHand, arrows);
    long timeSinceLastWarning = System.currentTimeMillis() - lastWarningTime;
    if (timeSinceLastWarning < 1000 && (ConfigurationHandler.getWarnMode() & 2) == 2) {
      renderItemBreakingOverlay(stack, lastWarningItem, timeSinceLastWarning);
    }
  }

  private void renderItemBreakingOverlay(PoseStack matrices, ItemStack itemStack, long timeDelta) {
    Window mainWindow = Minecraft.getInstance().getWindow();
    float alpha = 1.0f - ((float) timeDelta / 1000.0f);
    float xWarn = mainWindow.getGuiScaledWidth() / 2f;
    float yWarn = mainWindow.getGuiScaledHeight() / 2f;
    float scale = 5.0f;

    GuiComponent.fill(matrices, 0, 0, mainWindow.getGuiScaledWidth(), mainWindow.getGuiScaledHeight(),
        0xff0000 + ((int) (alpha * 128) << 24));

    PoseStack stack = RenderSystem.getModelViewStack();
    stack.pushPose();
    stack.scale(scale, scale, scale);
    RenderSystem.applyModelViewMatrix();

    itemRenderer.renderGuiItem(itemStack, (int) ((xWarn) / scale - 8), (int) ((yWarn) / scale - 8));

    stack.popPose();
    RenderSystem.applyModelViewMatrix();

    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
  }

  @SubscribeEvent(priority = EventPriority.NORMAL)
  public void afterRenderStatusEffects(final RenderGuiOverlayEvent.Post event) {

    if (!visible
        || event.isCanceled()
        // ||  minecraft.player.abilities.isCreativeMode
        || !(event.getOverlay().equals(VanillaGuiOverlay.POTION_ICONS.type())))
      return;

    if (ConfigurationHandler.showEffectDuration()) {
      // a lot of this is copied from net/minecraft/client/gui/GuiIngame.java
      Window mainWindow = minecraft.getWindow();
      Collection<MobEffectInstance> collection = Objects.requireNonNull(minecraft.player).getActiveEffects();
      int posGood = 0, posBad = 0;
      for (MobEffectInstance potioneffect : Ordering.natural().reverse().sortedCopy(collection)) {
        if (potioneffect.isVisible()) {
          MobEffect potion = potioneffect.getEffect();
          int xpos = mainWindow.getGuiScaledWidth();
          int ypos;
          if (potion.isBeneficial()) {
            posGood += 25;
            xpos -= posGood;
            ypos = 15;
          } else {
            posBad += 25;
            xpos -= posBad;
            ypos = 41;
          }
          int duration = potioneffect.getDuration();
          String show;
          if (duration > 1200)
            show = (duration / 1200) + "m";
          else
            show = (duration / 20) + "s";
          fontRenderer.draw(event.getPoseStack(), show, xpos + 2, ypos, ItemIndicator.color_yellow);        // draw
        }
      }
    }
  }

  private RenderSize renderItems(PoseStack stack, int xpos, int ypos, boolean reallyDraw, RenderPos numberPos, int maxWidth, ItemIndicator... items) {
    RenderSize result = new RenderSize(0, 0);

    for (ItemIndicator item : items) {
      if (item != null && !item.isEmpty() && item.isItemStackDamageable()) {
        String displayString = item.getDisplayValue();
        int width = fontRenderer.width(displayString);
        if (width > result.width)
          result.width = width;
        if (reallyDraw) {
          int color = item.getDisplayColor();
          itemRenderer.renderAndDecorateItem(item.getItemStack(), numberPos == RenderPos.left ? xpos + maxWidth - iconWidth - spacing : xpos, ypos + result.height);
          fontRenderer.draw(
              stack,
              displayString,
              numberPos != RenderPos.right ?
                  xpos : xpos + iconWidth + spacing,
              ypos + result.height +
                  fontRenderer.lineHeight / 2f +
                  (numberPos == RenderPos.over ? 10 : 0),
              color
          );
        }
        result.height += 16;
      }
    }
    if (result.width != 0)
      result.width += iconWidth + spacing * 2;
    return result;
  }

  private enum RenderPos {
    left, over, right
  }

  private static class RenderSize {
    int width;
    int height;

    RenderSize(int w, int h) {
      width = w;
      height = h;
    }
  }
}