package com.jclip;

public final class JClipApp {

    private JClipApp() {}

    public static void main(String[] args) {
        java.awt.Toolkit.getDefaultToolkit();
        javafx.application.Application.launch(com.jclip.ui.JClipFxApp.class, args);
    }
}
