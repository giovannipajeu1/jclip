package com.jclip.platform;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;

public final class MenuBarIcon {

    private final Runnable onShow;
    private final Runnable onQuit;
    private TrayIcon trayIcon;

    public MenuBarIcon(Runnable onShow, Runnable onQuit) {
        this.onShow = onShow;
        this.onQuit = onQuit;
    }

    public void install() throws AWTException {
        if (!SystemTray.isSupported()) {
            throw new AWTException("SystemTray não suportado nesse sistema.");
        }

        Image iconImage = buildIcon();

        PopupMenu menu = new PopupMenu();

        MenuItem showItem = new MenuItem("Mostrar JClip");
        showItem.addActionListener(e -> onShow.run());
        menu.add(showItem);

        menu.addSeparator();

        MenuItem quitItem = new MenuItem("Sair");
        quitItem.addActionListener(e -> onQuit.run());
        menu.add(quitItem);

        trayIcon = new TrayIcon(iconImage, "JClip", menu);
        trayIcon.setImageAutoSize(true);
        trayIcon.addActionListener(e -> onShow.run());

        SystemTray.getSystemTray().add(trayIcon);
    }

    public void remove() {
        if (trayIcon != null) {
            SystemTray.getSystemTray().remove(trayIcon);
            trayIcon = null;
        }
    }

    private Image buildIcon() {
        int size = 18;
        java.awt.image.BufferedImage image =
                new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g = image.createGraphics();
        g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new java.awt.Color(30, 30, 30));
        g.setStroke(new java.awt.BasicStroke(1.6f));
        g.drawRoundRect(3, 2, 12, 14, 3, 3);
        g.fillRoundRect(6, 1, 6, 3, 1, 1);
        g.dispose();
        return image;
    }
}
