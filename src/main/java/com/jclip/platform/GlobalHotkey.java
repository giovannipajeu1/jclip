package com.jclip.platform;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class GlobalHotkey {

  private final Runnable onTrigger;

  public GlobalHotkey(Runnable onTrigger) {
    this.onTrigger = onTrigger;
  }

  public void register() throws NativeHookException {
    Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
    logger.setLevel(Level.WARNING);
    logger.setUseParentHandlers(false);

    GlobalScreen.registerNativeHook();
    GlobalScreen.addNativeKeyListener(
        new NativeKeyListener() {
          @Override
          public void nativeKeyPressed(NativeKeyEvent e) {
            boolean cmd = (e.getModifiers() & NativeKeyEvent.META_MASK) != 0;
            boolean shift = (e.getModifiers() & NativeKeyEvent.SHIFT_MASK) != 0;
            if (cmd && shift && e.getKeyCode() == NativeKeyEvent.VC_V) {
              onTrigger.run();
            }
          }
        });
  }

  public void unregister() {
    try {
      GlobalScreen.unregisterNativeHook();
    } catch (NativeHookException ignored) {
    }
  }
}
