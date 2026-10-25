package com.jclip.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class ClipboardItemTest {

    @Test
    void deveSerVazioQuandoContentNulo() {
        ClipboardItem item = new ClipboardItem(1L, null, Instant.now());

        assertThat(item.isEmpty()).isTrue();
    }

    @Test
    void deveSerVazioQuandoContentSoEspacos() {
        ClipboardItem item = new ClipboardItem(1L, "   ", Instant.now());
        assertThat(item.isEmpty()).isTrue();
    }
}

