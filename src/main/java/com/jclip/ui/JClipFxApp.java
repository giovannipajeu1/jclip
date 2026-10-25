package com.jclip.ui;

import com.jclip.core.ClipboardItem;
import com.jclip.core.ClipboardWatcher;
import com.jclip.platform.GlobalHotkey;
import com.jclip.platform.MenuBarIcon;
import com.jclip.storage.HistoryRepository;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.Instant;

public class JClipFxApp extends Application {

    private HistoryRepository repo;
    private ObservableList<ClipboardItem> allItems;
    private ClipboardWatcher watcher;
    private GlobalHotkey hotkey;
    private MenuBarIcon trayIcon;
    private Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        Platform.setImplicitExit(false);

        repo = new HistoryRepository();
        allItems = FXCollections.observableArrayList(repo.listAll());

        Label title = new Label("Clipboard");
        title.getStyleClass().add("title-label");

        TextField searchField = new TextField();
        searchField.setPromptText("Buscar...");

        FilteredList<ClipboardItem> filteredItems = new FilteredList<>(allItems, item -> true);
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            String query = newText == null ? "" : newText.toLowerCase().trim();
            filteredItems.setPredicate(item ->
                    query.isEmpty() || item.content().toLowerCase().contains(query)
            );
        });

        ListView<ClipboardItem> listView = new ListView<>(filteredItems);
        listView.setCellFactory(lv -> new ClipboardItemCell());

        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                ClipboardItem selected = listView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    copyToClipboard(selected.content());
                    stage.hide();
                }
            }
        });

        Label emptyState = new Label("Nada no clipboard ainda.\nCopie algo (⌘C) pra começar.");
        emptyState.getStyleClass().add("empty-state");
        emptyState.setAlignment(Pos.CENTER);

        StackPane listContainer = new StackPane(listView, emptyState);
        emptyState.visibleProperty().bind(Bindings.isEmpty(allItems));
        listView.visibleProperty().bind(Bindings.isNotEmpty(allItems));

        VBox root = new VBox(6, title, searchField, listContainer);
        root.setPadding(new Insets(16));
        VBox.setVgrow(listContainer, Priority.ALWAYS);

        Scene scene = new Scene(root, 360, 480);
        scene.getStylesheets().add(getClass().getResource("/styles/jclip.css").toExternalForm());

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                stage.hide();
            }
        });

        stage.setScene(scene);
        stage.setTitle("JClip");

        stage.setOnCloseRequest(e -> {
            e.consume();
            stage.hide();
        });

        stage.show();
        stage.toFront();
        stage.requestFocus();

        startWatcher();
        installTray();
        installHotkey();
    }

    private void installHotkey() {
        hotkey = new GlobalHotkey(() -> Platform.runLater(this::toggleWindow));
        Thread t = new Thread(() -> {
            try {
                hotkey.register();
            } catch (Exception e) {
                System.err.println("Falha ao registrar hotkey global. Dê permissão de Acessibilidade pro Java em Ajustes do Sistema > Privacidade e Segurança > Acessibilidade.");
                e.printStackTrace();
            }
        }, "hotkey-register");
        t.setDaemon(true);
        t.start();
    }

    private void installTray() {
        java.awt.EventQueue.invokeLater(() -> {
            trayIcon = new MenuBarIcon(
                    () -> Platform.runLater(this::showWindow),
                    () -> Platform.runLater(this::quit)
            );
            try {
                trayIcon.install();
            } catch (Exception e) {
                System.err.println("Não foi possível criar ícone na barra de menu.");
                e.printStackTrace();
            }
        });
    }

    private void toggleWindow() {
        if (primaryStage.isShowing()) {
            primaryStage.hide();
        } else {
            showWindow();
        }
    }

    private void showWindow() {
        primaryStage.show();
        primaryStage.toFront();
        primaryStage.requestFocus();
    }

    private void quit() {
        if (watcher != null) watcher.stop();
        if (hotkey != null) hotkey.unregister();
        if (trayIcon != null) trayIcon.remove();
        Platform.exit();
        System.exit(0);
    }

    private void startWatcher() {
        watcher = new ClipboardWatcher();
        watcher.start(text -> {
            try {
                ClipboardItem item = new ClipboardItem(0L, text, Instant.now());
                repo.save(item);
                Platform.runLater(() -> allItems.add(0, item));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void copyToClipboard(String text) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
    }

    private static final class ClipboardItemCell extends ListCell<ClipboardItem> {
        @Override
        protected void updateItem(ClipboardItem item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
            } else {
                String preview = item.content().replace("\n", " ").trim();
                if (preview.length() > 80) {
                    preview = preview.substring(0, 80) + "...";
                }
                setText(preview);
            }
        }
    }
}
