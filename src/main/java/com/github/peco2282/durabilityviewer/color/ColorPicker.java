package com.github.peco2282.durabilityviewer.color;


import com.github.peco2282.durabilityviewer.gui.GuiModOptions;
import com.github.peco2282.durabilityviewer.gui.GuiSlider;
import com.github.peco2282.durabilityviewer.SliderValueConsumer;
import com.github.peco2282.durabilityviewer.config.ConfigurationTrueColor;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

public class ColorPicker extends AbstractWidget implements SliderValueConsumer {

  private final GuiModOptions optionScreen;
  private ColorDisplayAreaButton colorDisplay;
  private GuiSlider redSlider, greenSlider, blueSlider;
  private String option;
  private AbstractWidget element;
  private int currentColor;

  public ColorPicker(GuiModOptions optionScreen, int initialRGB, Component message) {
    super(0, 0, 250, 100, message);
    this.currentColor = initialRGB;
    this.optionScreen = optionScreen;
  }

  public void init() {
    MutableComponent buttonITextComponent = Component.literal("");
    this.setX((optionScreen.width - width) / 2);
    this.setY((optionScreen.height - height) / 2);
    colorDisplay = new ColorDisplayAreaButton(getX(), getY(), 20, 100, buttonITextComponent, currentColor);
    redSlider = new GuiSlider(this, getX() + 50, getY(), 200, 20, (currentColor >> 16) & 0xff, 0, 255, "red");
    greenSlider = new GuiSlider(this, getX() + 50, getY() + 40, 200, 20, (currentColor >> 16) & 0xff, 0, 255, "green");
    blueSlider = new GuiSlider(this, getX() + 50, getY() + 80, 200, 20, (currentColor >> 16) & 0xff, 0, 255, "blue");
    visible = false;
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (visible) {
      return redSlider.mouseClicked(mouseX, mouseY, button) || greenSlider.mouseClicked(mouseX, mouseY, button) || blueSlider.mouseClicked(mouseX, mouseY, button);
    }
    return false;
  }

  @Override
  public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
    if (visible) {
      optionScreen.getFontRenderer().draw(stack, "R", getX() + 30, getY() + 10, 0xff0000);
      optionScreen.getFontRenderer().draw(stack, "G", getX() + 30, getY() + 50, 0x00ff00);
      optionScreen.getFontRenderer().draw(stack, "B", getX() + 30, getY() + 90, 0x0000ff);
      colorDisplay.render(stack, mouseX, mouseY, partialTicks);
      redSlider.render(stack, mouseX, mouseY, alpha);
      greenSlider.render(stack, mouseX, mouseY, alpha);
      blueSlider.render(stack, mouseX, mouseY, alpha);
    }
  }

  public void setLink(String option, AbstractWidget element) {
    this.option = option;
    this.element = element;
  }

  public void setCurrentColor(ConfigurationTrueColor color) {
    currentColor = color.getInt();
    colorDisplay.setColor(currentColor);
    redSlider.reinitialize(color.red);
    greenSlider.reinitialize(color.green);
    blueSlider.reinitialize(color.blue);
  }

  public void onDoneButton() {
    optionScreen.onConfigChanging(option, new ConfigurationTrueColor(currentColor));
    element.setMessage(null);
    optionScreen.subscreenFinished();
  }

  @Override
  public void onConfigChanging(String color, Object value) {
    switch (color) {
      case "red" -> currentColor = (currentColor & 0x00ffff) | (Integer) value << 16;
      case "green" -> currentColor = (currentColor & 0xff00ff) | (Integer) value << 8;
      case "blue" -> currentColor = (currentColor & 0xffff00) | (Integer) value;
    }
    colorDisplay.setColor(currentColor);
    optionScreen.onConfigChanging(option, new ConfigurationTrueColor(currentColor));
  }

  @Override
  public boolean wasMouseReleased() {
    return optionScreen.wasMouseReleased();
  }

  @Override
  public void setMouseReleased(boolean value) {
    optionScreen.setMouseReleased(value);
  }

  @Override
  public void updateWidgetNarration(@NotNull NarrationElementOutput p_169152_) {
  }

  private static class ColorDisplayAreaButton extends AbstractWidget {

    private int rgb;

    public ColorDisplayAreaButton(int x, int y, int width, int height, Component message, int rgb) {
      super(x, y, width, height, message);
      this.rgb = rgb;
    }

    public void setColor(int rgb) {
      this.rgb = rgb;
    }

    @Override
    protected void renderBg(@NotNull PoseStack stack, @NotNull Minecraft mc, int mouseX, int mouseY) {
      if (this.visible) {
        GuiComponent.fill(stack, getX(), getY(), getX() + width, getY() + height, rgb | 0xff000000);
      }
    }

    @Override
    public void updateWidgetNarration(@NotNull NarrationElementOutput p_169152_) {
    }
  }
}