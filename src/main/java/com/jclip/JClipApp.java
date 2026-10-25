package com.jclip;
import com.jclip.core.ClipboardItem;
import com.jclip.core.ClipboardReader;
import com.jclip.core.ClipboardWatcher;
import com.jclip.storage.HistoryRepository;

public final class JClipApp {

    private JClipApp() {}

    public static void main(String[] args) throws Exception {
        HistoryRepository repo = new HistoryRepository();
        ClipboardWatcher watcher = new ClipboardWatcher();

        watcher.start(text -> {
            try {
                repo.save(new ClipboardItem(0L, text, java.time.Instant.now()));
                System.out.println("Salvo: " + text);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Thread.sleep(30000);

        System.out.println("\n=== HISTÓRICO ===");
        repo.listAll().forEach(System.out::println);
    }
}



