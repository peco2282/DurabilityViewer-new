package com.github.peco2282.durabilityviewer.color;

import com.github.peco2282.durabilityviewer.gui.GuiModOptions;
import com.github.peco2282.durabilityviewer.config.ConfigurationMinecraftColor;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

public class ColorSelector extends AbstractWidget {

  private final ColorButton[] buttons;
  private ConfigurationMinecraftColor currentColor;
  private String option;
  private AbstractWidget element;
  private final GuiModOptions optionScreen;

  private final int[] standardColors = {
      0x000000, 0x0000AA, 0x00AA00, 0x00AAAA,
      0xAA0000, 0xAA00AA, 0xFFAA00, 0xAAAAAA,
      0x555555, 0x5555FF, 0x55FF55, 0x55FFFF,
      0xFF5555, 0xFF55FF, 0xFFFF55, 0xFFFFFF
  };

  public ColorSelector(GuiModOptions optionScreen, MutableComponent message) {
    super(0, 0, 120, 120, message);
    buttons = new ColorButton[16];
    this.optionScreen = optionScreen;
  }

  public void init() {
    MutableComponent buttonText = Component.literal("");
    this.setX((optionScreen.width - width) / 2);
    this.setY((optionScreen.height - height) / 2);
    for (int i = 0; i < 16; i++) {
      buttons[i] = new ColorButton(
          this, getX() + (i / 4) * 25, getY() + (i % 4) * 25, 20, 20, buttonText, i, standardColors[i]
      );
    }
    visible = false;
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (visible) {
      for (ColorButton colorButton : buttons) {
        if (colorButton.mouseClicked(mouseX, mouseY, button))
          return true;
      }
    }
    return false;
  }

  @Override
  public void render(
      @NotNull PoseStack stack,
      int mouseX,
      int mouseY,
      float partialTicks
  ) {
    if (visible) {
      // renderButton(stack, mouseX, mouseY, partialTicks);
      for (int i = 0; i < 16; i++) {
        buttons[i].render(stack, mouseX, mouseY, partialTicks);
      }
    }
  }

  public void setLink(String option, AbstractWidget element) {
    this.option = option;
    this.element = element;
  }

  public ConfigurationMinecraftColor getCurrentColor() {
    return currentColor;
  }

  public void setCurrentColor(ConfigurationMinecraftColor color) {
    currentColor = color;
  }

  public void onColorSelected(int color) {
    currentColor.colorIndex = color;
    optionScreen.onConfigChanging(option, currentColor);
    element.setMessage(null);
    optionScreen.subscreenFinished();
  }

  @Override
  public void updateWidgetNarration(@NotNull NarrationElementOutput p_169152_) {
  }

  private static class ColorButton extends AbstractWidget {

    private final ColorSelector parent;
    private final int index;
    private final int color;

    public ColorButton(
        ColorSelector parent,
        int x,
        int y,
        int width,
        int height,
        MutableComponent message,
        int index,
        int color
    ) {
      super(x, y, width, height, message);
      this.index = index;
      this.color = color;
      this.parent = parent;
    }

    @Override
    protected void renderBg(
        @NotNull PoseStack stack,
        @NotNull Minecraft mc,
        int mouseX,
        int mouseY
    ) {
      if (this.visible) {
        super.renderBg(stack, mc, mouseX, mouseY);

        int x1 = this.getX() + 3;
        int x2 = this.getX() + this.width - 3;
        int y1 = this.getY() + 3;
        int y2 = this.getY() + this.height - 3;
        if (index == parent.getCurrentColor().colorIndex) {

          GuiComponent.fill(stack, x1, y1, x2, y2, 0xffffffff);
          x1++;
          y1++;
          x2--;
          y2--;
        }
        GuiComponent.fill(stack, x1, y1, x2, y2, color | 0xff000000);
      }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
      // System.out.println("selected "+Integer.toHexString(color)+" from button "+this.index);
      parent.onColorSelected(this.index);
    }

    @Override
    public void updateWidgetNarration(@NotNull NarrationElementOutput p_169152_) {
    }
  }
}