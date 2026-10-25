package com.jclip.core;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.util.Optional;

public final class ClipboardReader {

  private ClipboardReader() {}

  public static Optional<String> currentText() {
    try {
      String content =
          (String)
              Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
      return Optional.ofNullable(content);
    } catch (Exception e) {
      return Optional.empty();
    }
  }
}
