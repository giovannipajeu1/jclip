package com.jclip.ui;

import com.jclip.core.ClipboardItem;
import com.jclip.core.ClipboardWatcher;
import com.jclip.storage.HistoryRepository;
import javafx.application.Application;
import javafx.application.Platform;
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

    @Override
    public void start(Stage primaryStage) throws Exception {
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
                }
            }
        });

        Label emptyState = new Label("Nada no clipboard ainda.\nCopie algo (⌘C) pra começar.");
        emptyState.getStyleClass().add("empty-state");
        emptyState.setAlignment(Pos.CENTER);

        StackPane listContainer = new StackPane(listView, emptyState);
        emptyState.visibleProperty().bind(javafx.beans.binding.Bindings.isEmpty(allItems));
        listView.visibleProperty().bind(javafx.beans.binding.Bindings.isNotEmpty(allItems));

        VBox root = new VBox(6, title, searchField, listContainer);
        root.setPadding(new Insets(16));
        VBox.setVgrow(listContainer, Priority.ALWAYS);

        Scene scene = new Scene(root, 360, 480);
        scene.getStylesheets().add(getClass().getResource("/styles/jclip.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("JClip");
        primaryStage.show();

        startWatcher();

        primaryStage.setOnCloseRequest(e -> {
            if (watcher != null) {
                watcher.stop();
            }
        });
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
