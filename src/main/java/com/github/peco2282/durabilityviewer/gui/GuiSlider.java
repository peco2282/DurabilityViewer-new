package com.github.peco2282.durabilityviewer.gui;

import com.github.peco2282.durabilityviewer.SliderValueConsumer;
import com.github.peco2282.durabilityviewer.config.IConfiguration;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class GuiSlider extends AbstractWidget {

  Type type;
  boolean dragging;
  double sliderValue, defaultValue, min, max;
  String configOption;
  SliderValueConsumer parent;

  @SuppressWarnings("OverridableMethodCallInConstructor")
  public GuiSlider(SliderValueConsumer optionScreen, int x, int y, int width, int height, IConfiguration config, String option) {
    super(x, y, width, height, Component.literal("?"));
    Object value = config.getValue(option);
    if (value instanceof Double) {
      this.setMessage(Component.literal(Double.toString((Double) value)));
      this.min = (Double) config.getMin(option);
      this.max = (Double) config.getMax(option);
      this.defaultValue = (Double) config.getDefault(option);
      sliderValue = ((Double) value - min) / (max - min);
      type = Type.DOUBLE;
    } else if (value instanceof Float) {
      this.setMessage(Component.literal(Float.toString((Float) value)));
      this.min = (Float) config.getMin(option);
      this.max = (Float) config.getMax(option);
      this.defaultValue = (Float) config.getDefault(option);
      sliderValue = ((Float) value - min) / (max - min);
      type = Type.FLOAT;
    } else {
      this.setMessage(Component.literal(Integer.toString((Integer) value)));
      this.min = (Integer) config.getMin(option);
      this.max = (Integer) config.getMax(option);
      this.defaultValue = (Integer) config.getDefault(option);
      sliderValue = ((Integer) value - min) / (max - min);
      type = Type.INT;
    }

    this.configOption = option;
    this.parent = optionScreen;
    optionScreen.setMouseReleased(false);
  }

  public GuiSlider(SliderValueConsumer optionScreen, int x, int y, int width, int height, int val, int min, int max, String option) {
    super(x, y, width, height, Component.empty());
    this.setMessage(Component.literal("" + val));
    this.min = min;
    this.max = max;
    this.sliderValue = (float) ((val - min) / (max - min));
    this.type = Type.INT;
    this.configOption = option;
    this.parent = optionScreen;
  }

  /**
   * Callable from the outside (parent), to set a new value. Updates the
   * slider position and text, but does not emit "updated" events.
   *
   * @param value the new value, in terms between min and max.
   */
  public void reinitialize(double value) {
    this.sliderValue = (value - min) / (max - min);
    switch (type) {
      case DOUBLE -> this.setMessage(Component.literal(Double.toString(value)));
      case FLOAT -> this.setMessage(Component.literal(Float.toString((float) value)));
      case INT -> this.setMessage(Component.literal(Integer.toString((int) value)));
    }
  }

  /**
   * Called when the user clicks, drags, or otherwise moves the slider.
   * Resets the text message to reflect the new value, and tells our
   * parent the config has changed.
   *
   * @param value The new slider value. As the slider always uses a
   *              range between 0 and 1 internally, this value is expected to
   *              be in that range too.
   */
  private void updateValue(double value) {
    switch (type) {
      case DOUBLE -> {
        double doubleVal = value * (max - min) + min;
        this.setMessage(Component.literal(String.format("%.2f", doubleVal)));
        parent.onConfigChanging(configOption, doubleVal);
      }
      case FLOAT -> {
        float floatVal = (float) (value * (max - min) + min);
        this.setMessage(Component.literal(String.format("%.2f", floatVal)));
        parent.onConfigChanging(configOption, floatVal);
      }
      case INT -> {
        int intVal = (int) (value * (max - min) + min + 0.5);
        this.setMessage(Component.literal(String.format("%d", intVal)));
        parent.onConfigChanging(configOption, intVal);
      }
    }
  }

  @Override
  protected void renderBg(@NotNull PoseStack stack, @NotNull Minecraft mc, int mouseX, int mouseY) {
    if (this.visible) {
      if (this.dragging) {
        this.sliderValue = (float) (mouseX - (this.getX() + 4)) / (float) (this.width - 8);
        this.sliderValue = Mth.clamp(this.sliderValue, 0.0D, 1.0D);
        updateValue(this.sliderValue);
        if (parent.wasMouseReleased()) {
          this.dragging = false;
        }
      }
      RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
      RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
      this.blit(stack, this.getX() + (int) (this.sliderValue * (double) (this.width - 8)), this.getY(), 0, 66, 4, 20);
      this.blit(stack, this.getX() + (int) (this.sliderValue * (double) (this.width - 8)) + 4, this.getY(), 196, 66, 4, 20);
    }
  }

  /*
   * Called when the left mouse button is pressed over this button. This method is specific to Widget.
   */
  @Override
  public final void onClick(double mouseX, double mouseY) {
    this.sliderValue = (mouseX - (double) (this.getX() + 4)) / (double) (this.width - 8);
    this.sliderValue = Mth.clamp(this.sliderValue, 0.0D, 1.0D);
    updateValue(sliderValue);
    this.dragging = true;
    parent.setMouseReleased(false);
  }

  /*
   * Called when the left mouse button is released. This method is specific to Widget.
   */
  @Override
  public void onRelease(double mouseX, double mouseY) {
    this.dragging = false;
  }

  @Override
  public void onFocusedChanged(boolean b) {
    // called when the user presses the "reset" button next to the slider
    sliderValue = (defaultValue - min) / (max - min);
    updateValue(sliderValue);
    super.onFocusedChanged(b);
  }

  @Override
  public void updateWidgetNarration(@NotNull NarrationElementOutput p_169152_) {
  }

  private enum Type {INT, FLOAT, DOUBLE}
}