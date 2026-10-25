package com.jclip.core;

import java.time.Instant;

public record ClipboardItem(long id, String content, Instant copiedAt) {
  public boolean isEmpty() {
    return content() == null || content().isBlank();
  }
}
