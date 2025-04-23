#!/bin/bash

REPO_URL="https://github.com/JASIM0021/mvntool"
INSTALL_DIR="$HOME/.mvntool"
BIN_DIR="$HOME/.local/bin"
JAR_NAME="mvntool.jar"
SCRIPT_NAME="mvntool"

echo "📦 Installing mvntool..."

mkdir -p "$INSTALL_DIR"
mkdir -p "$BIN_DIR"

echo "⬇️ Downloading latest version..."
curl -sSL "$REPO_URL/target/mvntool-1.0.0.jar" -o "$INSTALL_DIR/$JAR_NAME"

echo "📝 Creating executable script..."
cat > "$BIN_DIR/$SCRIPT_NAME" <<EOL
#!/bin/bash
java -jar "$INSTALL_DIR/$JAR_NAME" "\$@"
EOL

chmod +x "$BIN_DIR/$SCRIPT_NAME"

if [[ ":$PATH:" != *":$BIN_DIR:"* ]]; then
    echo "🔧 Adding to PATH..."
    echo "export PATH=\"\$PATH:$BIN_DIR\"" >> "$HOME/.bashrc"
    echo "export PATH=\"\$PATH:$BIN_DIR\"" >> "$HOME/.zshrc"
    export PATH="$PATH:$BIN_DIR"
fi

echo "🎉 Installation complete! Try: mvntool --help"