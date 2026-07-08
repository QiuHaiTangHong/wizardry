# Electroblob's Wizardry (NeoForge 26.2 移植版)

本项目是 **Electroblob's Wizardry** (原项目: [GitHub](https://github.com/Electroblob77/Wizardry)) 往现代 Minecraft
高版本（基于 **NeoForge 26.2** / Java 25 架构）的重构与移植尝试。

> **免责声明**：请不要抱有太大的希望。这纯粹是一个为了深入学习现代 Minecraft Mod 开发、理解数据组件（Data
> Components）以及底层渲染管线重构而设立的**个人学习/技术验证项目**。

---

## 构建指南

由于本项目涉及部分特定的跨平台本地调用或高性能加速库，构建流程结合了 Java 与 C++ 的混合编译体系。在开始之前，请务必配置好相应的底层工具链。

### 1. 环境准备

请确保你的系统已安装以下工具，并将其二进制目录（`bin`）正确配置到系统的环境变量 `Path` 中：

* **Java 25 SDK** (推荐使用 GraalVM 或 Eclipse Temurin，并确保 `JAVA_HOME` 指向该版本)

> **验证环境**：配置完成后，打开终端（Terminal）或命令提示符（CMD）运行以下命令。若均能正常输出版本号，则说明环境就绪：
> ```bash
> java -version
> ```

### 2. 编译与构建步骤

项目采用 Gradle 进行依赖管理和混淆映射。请在项目根目录下执行以下脚本：

#### 编译并打包模组 (Build)

```bash
./gradlew build
```

### 3. API 接口

该模组提供了一套灵活且强大的数据驱动 API，用于定义和管理游戏内的各种数据。这套系统基于 `AbstractWizardryDataManager`
核心类构建，实现了数据的加载、解析、缓存和访问。

#### 核心概念

该 API 的设计围绕以下几个核心接口展开，它们共同构成了一个完整的数据处理流程：

- **`IDataParser<P, C, R>`**: 数据解析器接口，是整个系统的核心。它定义了如何将一个 JSON 数据对象转换为游戏内可用的数据结果。
    - `P`: 解析出的原始数据类型 (Parsed Data)。
    - `C`: 解析时所需的上下文类型 (Parser Context)，可以为 `null`。
    - `R`: 最终生成的、可供游戏使用的数据结果类型 (Result Data)。
- **`IResultData`**: 所有最终数据结果必须实现的接口。它用于标记和分类数据，便于后续的类型安全查找。
- **`IParserContext`**: 解析器上下文接口。某些复杂的解析器可能需要一个上下文对象来在解析多个文件时共享状态或数据。

#### 数据加载流程

1. **注册解析器**: 模组在初始化时，会将实现了 `IDataParser` 接口的解析器实例注册到 `AbstractWizardryDataManager` 的内部注册表中。
2. **资源发现**: 当游戏资源重载时，`AbstractWizardryDataManager` 会根据预设的路径（通过 `FileToIdConverter`
   定义）扫描所有数据包（Data Packs）中的 JSON 文件。
3. **解析与转换**: 对于每个找到的 JSON 文件：
    - 读取其根节点的 `"parser"` 字段，该字段的值是一个标识符（如 `wizardry:spell`），用于确定使用哪个 `IDataParser`。
    - 调用对应解析器的 `parserItem(JsonElement json)` 方法，将 JSON 内容解析为中间数据对象 `P`。
    - 接着调用 `transformItemToResult(...)` 方法，将中间数据 `P` 和上下文 `C` 转换为最终的游戏数据对象 `R`。
4. **缓存**: 所有成功解析的数据结果 `R` 会按照其类型 `R.getClass()` 进行分组，并存储在一个不可变的快照（`storageSnapshot`
   ）中，以供游戏运行时快速查询。

#### 数据访问

数据加载完成后，可以通过 `AbstractWizardryDataManager` 提供的公共方法来访问：

- **`<T extends IResultData> Optional<T> getData(Identifier id, Class<T> expectedType)`**:
  根据数据的唯一标识符（`Identifier`）和期望的类型（`Class`）来获取单个数据对象。这是一个类型安全的方法，如果找不到或类型不匹配，会返回
  `Optional.empty()`。

- **`<T extends IResultData> Map<Identifier, T> getAllDataByType(Class<T> expectedType)`**:
  获取指定类型的所有已加载数据，返回一个从 `Identifier` 到数据对象的映射。

#### JSON 文件格式

所有通过此系统加载的数据文件都必须是 JSON 格式，并遵循以下结构：

```json
{
  "parser": "命名空间:解析器路径",
  "// ...": "其他由具体解析器定义的字段"
}
```
