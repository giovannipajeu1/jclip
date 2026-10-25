<div align="center">

<img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/java/java-original.svg" width="90" alt="Java" />

# 📋 JClip

### Clipboard history manager para macOS. ⌘⇧V abre o histórico.

[![Java](https://img.shields.io/badge/Java-21_LTS-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](#)
[![JavaFX](https://img.shields.io/badge/JavaFX-23-1F8AC0?style=for-the-badge)](#)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue?style=for-the-badge)](LICENSE)
[![Status](https://img.shields.io/badge/status-WIP-orange?style=for-the-badge)](#)

</div>

## 🎯 O que é

**JClip** é um gerenciador de histórico de clipboard para macOS, inspirado no `Win+V` do Windows. Toda vez que você copia algo (texto, imagem ou arquivo), o JClip guarda localmente. Com `⌘⇧V` você abre uma janela flutuante com o histórico, busca o que precisa e cola.

No mundo macOS existem alternativas pagas (Paste, Alfred Clipboard, Raycast). O JClip é open-source, grátis, e serve como projeto de aprendizado para quem quer explorar:

* Integração Java com APIs nativas macOS (via **JNA**)
* UI desktop moderna com **JavaFX**
* Persistência local embarcada com **SQLite**
* Packaging de app nativo (`.app` / `.dmg`) com `jpackage`
* Code signing e notarização Apple

## 🛠️ Stack

| Camada | Tecnologia |
|---|---|
| Runtime | Java 21 LTS (Temurin, ARM64) |
| UI | JavaFX 23 + FXML + CSS |
| Nativo macOS | JNA (NSPasteboard, NSEvent) |
| Persistência | SQLite via `sqlite-jdbc` |
| Logging | SLF4J + Logback |
| Build | Maven 3.10+ |
| Testes | JUnit 5 + AssertJ + Mockito |
| Format/Lint | Spotless (Google Java Format) |
| CI | GitHub Actions |

## 🚀 Build & Run (desenvolvimento)

### Pré-requisitos

* macOS 12+ (Apple Silicon ou Intel)
* JDK 21 LTS ([Temurin](https://adoptium.net/))
* Maven 3.10+

### Rodar localmente

```bash
git clone https://github.com/giovannipajeu1/jclip.git
cd jclip

mvn verify

mvn javafx:run
```

### Build do .app distribuível (Fase 5)

```bash
mvn package
jpackage --input target/ \
         --name JClip \
         --main-jar jclip-0.1.0-SNAPSHOT.jar \
         --type dmg
```

## 🤝 Contribuindo

PRs são bem-vindos. Antes de abrir:

1. Fork o repo e crie branch `feature/sua-feature`
2. Siga [Conventional Commits](https://www.conventionalcommits.org/)
3. Rode `mvn spotless:apply verify` antes do push
4. Abra PR descrevendo o que e o porquê

## 📄 License

[Apache License 2.0](LICENSE). Use, forke, feche o código, faça o que quiser desde que mantenha a atribuição.

<div align="center">

Feito em São Tomé das Letras, MG ☕

</div>
