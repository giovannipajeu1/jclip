package com.jclip.storage;

import static org.assertj.core.api.Assertions.assertThat;

import com.jclip.core.ClipboardItem;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class HistoryRepositoryTest {

  @TempDir Path tempDir;

  private HistoryRepository repository;

  @BeforeEach
  void setUp() throws Exception {
    Path dbPath = tempDir.resolve("test.db");
    repository = new HistoryRepository(dbPath);
  }

  @Test
  void deveRetornarListaVaziaQuandoNadaFoiSalvo() throws Exception {
    List<ClipboardItem> items = repository.listAll();
    assertThat(items).isEmpty();
  }

  @Test
  void deveSalvarEListarUmItem() throws Exception {
    ClipboardItem item = new ClipboardItem(0L, "ola mundo", Instant.now());
    repository.save(item);

    List<ClipboardItem> items = repository.listAll();

    assertThat(items).hasSize(1);
    assertThat(items.get(0).content()).isEqualTo("ola mundo");
  }

  @Test
  void deveSalvarMultiplosItens() throws Exception {
    repository.save(new ClipboardItem(0L, "primeiro", Instant.now()));
    repository.save(new ClipboardItem(0L, "segundo", Instant.now()));
    repository.save(new ClipboardItem(0L, "terceiro", Instant.now()));

    List<ClipboardItem> items = repository.listAll();

    assertThat(items).hasSize(3);
  }

  @Test
  void deveOrdenarItensPorDataDescendente() throws Exception {
    Instant mesPassado = Instant.parse("2026-09-01T10:00:00Z");
    Instant ontem = Instant.parse("2026-10-24T10:00:00Z");
    Instant hoje = Instant.parse("2026-10-25T10:00:00Z");

    repository.save(new ClipboardItem(0L, "antigo", mesPassado));
    repository.save(new ClipboardItem(0L, "recente", hoje));
    repository.save(new ClipboardItem(0L, "intermediario", ontem));

    List<ClipboardItem> items = repository.listAll();

    assertThat(items)
        .extracting(ClipboardItem::content)
        .containsExactly("recente", "intermediario", "antigo");
  }

  @Test
  void deveAtribuirIdAutoIncrementado() throws Exception {
    repository.save(new ClipboardItem(0L, "a", Instant.parse("2026-10-25T10:00:00Z")));
    repository.save(new ClipboardItem(0L, "b", Instant.parse("2026-10-25T11:00:00Z")));

    List<ClipboardItem> items = repository.listAll();

    assertThat(items).extracting(ClipboardItem::id).allMatch(id -> id > 0);
    assertThat(items.get(0).id()).isNotEqualTo(items.get(1).id());
  }

  @Test
  void devePreservarConteudoComCaracteresEspeciais() throws Exception {
    String conteudo = "linha 1\nlinha 2\t'aspas simples'\"aspas duplas\"\\barra invertida";
    repository.save(new ClipboardItem(0L, conteudo, Instant.now()));

    List<ClipboardItem> items = repository.listAll();

    assertThat(items).hasSize(1);
    assertThat(items.get(0).content()).isEqualTo(conteudo);
  }
}
