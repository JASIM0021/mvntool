
# 🚀 mvntool - The Maven Dependency Manager for Humans

A modern CLI tool that simplifies managing Maven dependencies for Spring Boot and Java developers — no more manual `pom.xml` edits!

Inspired by the simplicity of `npm install <package>`, `mvntool` brings a seamless, scriptable experience for:

- 🔍 Searching for Maven packages
- 📦 Installing dependencies directly into your `pom.xml`
- 🔄 Updating existing dependencies

---

## Demo


https://github.com/user-attachments/assets/92fac595-0282-4c87-834d-6c41be99169d



---

## ✨ Features

- Clean and intuitive CLI powered by [picocli](https://picocli.info/)
- Works with any Maven-based Java/Spring Boot project
- Supports `install`, `search`, and `update` commands
- Automatically modifies your `pom.xml`
- Cross-platform support (macOS, Linux, Windows)

---

## 🧩 Installation

### 📦 macOS/Linux

Install via shell script:

```bash
curl -sSL https://raw.githubusercontent.com/JASIM0021/mvntool/refs/heads/main/install.sh | bash
```

Make sure `~/.local/bin` is in your `PATH`. Restart your terminal or run:

```bash
export PATH="$PATH:$HOME/.local/bin"
```

### 🪟 Windows (PowerShell)

Run this in PowerShell:

```powershell
irm https://raw.githubusercontent.com/JASIM0021/mvntool/refs/heads/main/install.ps1 | iex
```

---

## ⚙️ Usage

```bash
mvntool install spring-boot-starter-web
mvntool search kafka
mvntool update spring-boot-starter-data-jpa
```

Need help?

```bash
mvntool --help
```

---

## 🛠 Commands

| Command       | Description                                 |
|---------------|---------------------------------------------|
| `install`     | Adds a Maven package to your `pom.xml`      |
| `search`      | Searches Maven Central for dependencies     |
| `update`      | Updates version of a specified dependency   |

---

## 🧑‍💻 For Contributors

We welcome contributions!

### 💡 Getting Started

1. Fork the repository.
2. Clone your fork:
   ```bash
   git clone https://github.com/your-username/mvntool.git
   cd mvntool
   ```
3. Build and test locally:
   ```bash
    mvn clean package  
   java -jar ./target/mvntool-1.0.0.jar  install spring-boot 
   ```

### 🔧 Project Structure

- `src/main/java/org/mvntool/cli/` — Main CLI logic
- `commands/` — `InstallCommand`, `SearchCommand`, `UpdateCommand`

### 📦 Releasing a new version?

Update the version in:
- `install.sh` download URL
- `README.md` commands (optional)

---

## 📃 License

MIT License © [Jasim Uddin](https://github.com/JASIM0021)

---

## ❤️ Acknowledgements

Inspired by developer productivity tools like:
- `npm` (Node.js)
- `brew` (Homebrew)
- `cargo` (Rust)

---

## 📫 Stay in Touch

Found an issue? Have a feature request? [Open an issue](https://github.com/JASIM0021/mvntool/issues) or drop a ⭐ if you like the tool!
