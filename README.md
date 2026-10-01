# Ai on Device

An Android application that uses an on-device Large Language Model (LLM) to provide AI inference locally on the device.

## ✨ Features

- 🤖 On-device LLM inference
- 🔒 Privacy-focused local AI
- 📱 Android application
- ⚡ No cloud API required for inference
- 🌐 Can work without internet after the model is available locally
- 🎨 Jetpack Compose UI
- 🧠 Gemma 3 1B model
- 💾 Local model execution

## 🏗️ Architecture

```text
User
 ↓
Jetpack Compose UI
 ↓
ChatViewModel
 ↓
LLM Repository / Engine
 ↓
Gemma LLM
 ↓
On-device inference