# Arquitetura — JClip

> Documento vivo. Será atualizado a cada fase conforme decisões concretas forem tomadas.

## Visão geral

O JClip observa o clipboard do macOS, persiste os itens copiados num banco local SQLite e
expõe o histórico via uma overlay JavaFX acionada por um hotkey global (⌘⇧V).

```
┌─────────────────────────────────────────────┐
│  macOS NSPasteboard  ◄──── (JNA bindings)   │
│         ▲                                   │
│         │ polling 500 ms                    │
│  ┌──────┴──────┐    ┌──────────────┐        │
│  │  Watcher    │───►│ History Store│──SQLite│
│  └─────────────┘    └──────┬───────┘        │
│                            │                │
│  Global Hotkey ⌘⇧V ──► JavaFX Overlay       │
│  (NSEvent via JNA)       (lista + search)   │
│                                             │
│  Menu Bar Icon (SystemTray + AWT fallback)  │
└─────────────────────────────────────────────┘
```

## Módulos (pacotes)

| Pacote | Responsabilidade |
|---|---|
| `com.jclip.core` | Modelo de domínio (ClipboardItem), watcher, orchestration |
| `com.jclip.platform` | Bindings JNA pra APIs nativas macOS (NSPasteboard, NSEvent) |
| `com.jclip.storage` | Repositório SQLite, migrations, DTOs de persistência |
| `com.jclip.ui` | JavaFX controllers, FXML, estilos CSS |

## Decisões arquiteturais relevantes (ADR-style, inline)

**ADR-001 — Polling vs. event-driven no clipboard**
NSPasteboard não emite eventos nativos de mudança. A alternativa seria registrar um
`NSPasteboardSubscriber` (API privada, não documentada, risco de rejeição pela Apple).
Decisão: **polling a 500 ms com `changeCount`**, que é a técnica usada pelo próprio Finder.
Baixo custo (apenas lê um `long`) e sem dependência de API privada.

**ADR-002 — Persistência local: SQLite vs. JSON em arquivo**
Para 500-5000 itens com busca textual rápida, SQLite com índice FTS5 vence JSON em
performance de busca e em consumo de memória (não carrega tudo na heap). Decisão: **SQLite**.

**ADR-003 — Hotkey global: JNA vs. biblioteca Java pura**
Nenhuma biblioteca Java pura registra hotkey global no macOS pós-Catalina sem permissões
de acessibilidade. Decisão: **JNA + CGEventTap**, que requer permissão de Acessibilidade
do usuário (prompt padrão do macOS).

## Fases subsequentes

Cada fase vai adicionar sua própria seção aqui com decisões concretas, trade-offs e links
pra PRs relevantes.
