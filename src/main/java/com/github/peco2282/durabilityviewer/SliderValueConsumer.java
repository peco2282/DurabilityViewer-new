/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.peco2282.durabilityviewer;

public interface SliderValueConsumer {
  void onConfigChanging(String option, Object value);

  boolean wasMouseReleased();

  void setMouseReleased(boolean value);
}