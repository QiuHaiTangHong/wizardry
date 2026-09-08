# Electroblob's Wizardry (NeoForge 26.2 移植版)

本项目是 **Electroblob's Wizardry** (原项目: [GitHub](https://github.com/Electroblob77/Wizardry)) 往现代 Minecraft
高版本（基于 **NeoForge 26.2** / Java 25 架构）的重构与移植尝试。

> **免责声明**：请不要抱有太大的希望。这纯粹是一个为了深入学习现代 Minecraft Mod 开发、理解数据组件（Data
> Components）以及底层渲染管线重构而设立的**个人学习/技术验证项目**。

---

## 构建指南

构建流程结合了 Java 与 C++ 的混合编译体系。在开始之前，请务必配置好相应的底层工具链。

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

##### 核心概念

该 API 的设计围绕以下几个核心接口展开，它们共同构成了一个完整的数据处理流程：

- **`IDataParser<P, C, R>`**: 数据解析器接口，是整个系统的核心。它定义了如何将一个 JSON 数据对象转换为游戏内可用的数据结果。
    - `P`: 解析出的原始数据类型 (Parsed Data)。
    - `C`: 解析时所需的上下文类型 (Parser Context)，可以为 `null`。
    - `R`: 最终生成的、可供游戏使用的数据结果类型 (Result Data)。
- **`IResultData`**: 所有最终数据结果必须实现的接口。它用于标记和分类数据，便于后续的类型安全查找。
- **`IParserContext`**: 解析器上下文接口。某些复杂的解析器可能需要一个上下文对象来在解析多个文件时共享状态或数据。
- **`AbstractWizardryDataManager`**: 作为整个数据管理系统的单例入口，负责协调资源的扫描、事件的触发以及最终存储快照的管理。

#### 数据加载流程

1. **注册解析器**: 模组在初始化时，会触发 `RegisterDataParserEvent` 事件。监听该事件的代码会将自己的 `IDataParser`
   实例注册到事件提供的注册表中。
2. **资源重载准备**: 当游戏资源重载时，`AbstractWizardryDataManager` 会首先触发 `DataParserBefore`
   事件。需要共享状态的解析器可以监听此事件，创建并注册自己的 `IParserContext` 实例。
3. **资源发现**: `AbstractWizardryDataManager` 会根据预设的路径扫描所有数据包（Data Packs）中的 JSON 文件。
4. **解析与转换**: 对于每个找到的 JSON 文件：
    - 读取其根节点的 `"parser"` 字段，该字段的值是一个标识符（如 `wizardry:spell`），用于确定使用哪个 `IDataParser`。
    - 调用对应解析器的 `parserItem(JsonElement json)` 方法，将 JSON 内容解析为中间数据对象 `P`。
    - 接着调用 `transformItemToResult(...)` 方法，将中间数据 `P` 和从 `DataParserBefore` 事件中获取的上下文 `C`
      转换为最终的游戏数据对象 `R`。
5. **缓存**: 所有成功解析的数据结果 `R` 会按照其类型 `R.getClass()` 进行分组，并存储在一个不可变的快照（`storageSnapshot`
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

* **`parser`**: **必填字段**。一个字符串，指定了处理该文件所需的 `IDataParser` 的唯一标识符。
* **条件加载**: 该 API 原生支持 NeoForge 的条件加载系统。你可以在 JSON 文件的根节点使用 `"conditions"`
  数组，只有当所有条件都满足时，文件才会被加载。

**示例：一个法术数据文件 (`data/wizardry/spells/magic_missile.json`)**

```json
{
  "parser": "wizardry:spell",
  "element": "magic",
  "tier": "apprentice",
  "type": "projectile",
  "cost": 50,
  "cooldown": 20
}
```

在这个例子中，`"parser": "wizardry:spell"` 告诉系统使用 ID 为 `wizardry:spell` 的解析器来处理这个文件。解析器会读取
`element`, `tier` 等字段，并最终生成一个 `Spell` 对象（该类实现了 `IResultData`）。

---

## GUI 元素系统 (GUI Element System)

### 核心接口

* **`IElement`**: GUI 组件的基础抽象接口。定义了所有 GUI 元素共有的基本行为，如绘制逻辑、鼠标点击检测、键盘输入处理以及坐标定位。
* **`IAtomElement`**: 表示不可再分的“原子”UI 组件。这些是基础的视觉或交互控件，例如按钮、文本标签、图标或进度条。它们直接继承自 `IElement` 并实现具体的渲染和交互逻辑。
* **`IContainerElement`**: 一种特殊的 UI 容器接口，它可以包含多个子 `IElement`。容器负责管理子元素的布局排列、层级关系以及事件分发。
* **`IHybridElement`**: 代表由多个基础元素组合而成的复合组件。它可能结合了不同的原子元素功能，或者实现了特定的业务逻辑封装（例如一个集成了图标、标题和描述信息的“卡片”组件）。

### 辅助类与工具

为了支持 GUI 系统的灵活性和可扩展性，项目中还引入了以下辅助组件：

* **`Context`**: 在执行 GUI 渲染或交互操作时提供的环境上下文。它可能包含当前的屏幕尺寸、缩放比例、当前选中的窗口状态或临时数据存储，供元素逻辑判断使用。
* **`Format`**: 用于定义 UI 元素在显示时的格式化规范。例如，文本的对齐方式、颜色主题、字体样式或边框样式。确保不同来源的 UI 元素能够以统一且美观的方式呈现。
* **`PageTurner`**: 在处理分页列表或大型图鉴界面时使用的辅助类。它帮助管理当前查看的页面索引、每页显示的元素数量以及翻页按钮的状态。
* **`SectionFormatConverter`**: 负责将原始章节数据 (`SectionData`) 转换为一组格式化后的页面元素(`PageElement`)。