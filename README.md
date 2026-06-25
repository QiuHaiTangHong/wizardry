# 🧙‍♂️ Electroblob's Wizardry (NeoForge 26.2 移植版)

本项目是经典魔法模组 **Electroblob's Wizardry** (原项目: [GitHub](https://github.com/Electroblob77/Wizardry)) 往现代 Minecraft 高版本（基于 **NeoForge 26.2** / Java 25 架构）的重构与移植尝试。

> ⚠️ **免责声明**：请不要抱有太大的希望。这纯粹是一个为了深入学习现代 Minecraft Mod 开发、理解数据组件（Data Components）以及底层渲染管线重构而设立的**个人学习/技术验证项目**。

---

## 🚀 构建指南

由于本项目涉及部分特定的跨平台本地调用或高性能加速库，构建流程结合了 Java 与 C++ 的混合编译体系。在开始之前，请务必配置好相应的底层工具链。

### 📌 1. 环境准备

请确保你的系统已安装以下工具，并将其二进制目录（`bin`）正确配置到系统的环境变量 `Path` 中：

* **Java 25 SDK** (推荐使用 GraalVM 或 Eclipse Temurin，并确保 `JAVA_HOME` 指向该版本)
* **CMake** (3.25+ 推荐，用于管理跨平台 C/C++ 编译流程)
* **Ninja** (用于提供比传统 Make 更快、更高效的构建后端)

> 💡 **验证环境**：配置完成后，打开终端（Terminal）或命令提示符（CMD）运行以下命令。若均能正常输出版本号，则说明环境就绪：
> ```bash
> java -version
> cmake --version
> ninja --version
> ```

### 🔨 2. 编译与构建步骤

项目采用 Gradle 进行依赖管理和混淆映射。请在项目根目录下执行以下脚本：

#### 🔹 编译并打包模组 (Build)
```bash
./gradlew build
