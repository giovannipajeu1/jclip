package com.jclip;
import com.jclip.core.ClipboardItem;
import com.jclip.core.ClipboardReader;
import com.jclip.core.ClipboardWatcher;
import com.jclip.storage.HistoryRepository;

public final class JClipApp {

    private JClipApp() {}

    public static void main(String[] args) {
        javafx.application.Application.launch(com.jclip.ui.JClipFxApp.class, args);
    }
}



