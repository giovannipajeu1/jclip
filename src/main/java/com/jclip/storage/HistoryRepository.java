package com.jclip.storage;

import com.jclip.core.ClipboardItem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public final class HistoryRepository {

    private static final String DB_PATH = System.getProperty("user.home") + "/.jclip/jclip.db";

    private Connection openConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
    }

    public HistoryRepository() throws IOException, SQLException {
        Path dbDir = Paths.get(DB_PATH).getParent();
        if (!Files.exists(dbDir)) {
            Files.createDirectories(dbDir);
        }
        try (Connection connection = openConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("""
                CREATE TABLE IF NOT EXISTS clipboard_history (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    content TEXT NOT NULL,
                    copied_at TEXT NOT NULL
                )
            """);
        }
    }

    public void save(ClipboardItem item) throws SQLException {
        String sql = "INSERT INTO clipboard_history (content, copied_at) VALUES (?, ?)";
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, item.content());
            statement.setString(2, item.copiedAt().toString());
            statement.executeUpdate();
        }
    }

    public List<ClipboardItem> listAll() throws SQLException {
        String sql = "SELECT id, content, copied_at FROM clipboard_history ORDER BY copied_at DESC";
        List<ClipboardItem> items = new ArrayList<>();
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                items.add(new ClipboardItem(
                        rs.getLong("id"),
                        rs.getString("content"),
                        Instant.parse(rs.getString("copied_at"))
                ));
            }
        }
        return items;
    }
}
