package com.jclip.core;

import java.util.function.Consumer;

public final class ClipboardWatcher {

  private volatile boolean running = false;
  private String lastContent = "";

  public void start(Consumer<String> onNewContent) {
    running = true;
    Thread thread =
        new Thread(
            () -> {
              while (running) {
                ClipboardReader.currentText()
                    .ifPresent(
                        text -> {
                          if (!text.equals(lastContent)) {
                            lastContent = text;
                            onNewContent.accept(text);
                          }
                        });
                try {
                  Thread.sleep(500);
                } catch (InterruptedException e) {
                  Thread.currentThread().interrupt();
                  return;
                }
              }
            });
    thread.setDaemon(true);
    thread.start();
  }

  public void stop() {
    running = false;
  }
}
