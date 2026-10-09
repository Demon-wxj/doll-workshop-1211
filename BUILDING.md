# 构建

安装 **JDK 21 和 Gradle 8.12**。工程使用 Fabric Loom 1.9 系列，不附 JDK、Gradle 缓存或 Wrapper 二进制。依赖从 Fabric Maven、Maven Central 和 Gradle Plugin Portal 获取，首次构建需要网络。

从各依赖的官方发行合法取得以下版本，放入 `deps/`，文件名必须与 `build.gradle` 一致：

| 文件 | 构建用途 |
| --- | --- |
| `geckolib-fabric-1.21.1-4.7.6.jar` | `modImplementation`，运行依赖 |
| `trinkets-3.10.0.jar` | `modCompileOnly`，饰品兼容代码 |
| `accessories-fabric-1.1.0-beta.53+1.21.1.jar` | `modCompileOnly`，饰品兼容代码 |

这些 JAR 不随仓库提供，也不要提交到 Git；各依赖的作者和许可由其官方发行保留。

在仓库根目录运行：

```powershell
gradle --version
gradle clean build --console=plain
```

产物位于 `build/libs/`。安装普通重映射 JAR，`-sources.jar` 和开发 JAR 不放入游戏。仓库不含本机修复依赖、测试世界或历史验证入口，构建不会自动安装游戏。

构建不会补回已排除的 ARR 纹理、模型和声音；完整显示需另备有权使用的资源。代码的 GPL 来源和美术例外见 [许可说明](THIRD_PARTY_NOTICES.md)。
