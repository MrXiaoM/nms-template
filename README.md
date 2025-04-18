# nms-template

方便快速添加的 NMS 调用模板项目。这个模板有以下特性
+ 使用 Kotlin DSL 构建脚本
+ 依赖于 shadow 插件
+ 杂交编译目标，允许子模块编译为 Java 17 目标，主模块则使用 Java 8 目标，提升插件兼容性
+ 支持所有 Spigot/Paper 服务端
+ 不使用 paperweight 等插件来构建 NMS 依赖，单纯使用网络仓库，加快构建速度

## 1.复制模板

将本仓库的 `buildSrc` 文件夹复制到项目目录，但如果你的项目已经有 `buildSrc` 了，只需复制其中的 `ExtraHelper.kt` 即可。有它之后，可以减少很多工作量。

将本仓库的 `nms` 文件夹复制到项目目录。

## 2.添加子项目

编辑 `settings.gradle.kts`，在结尾添加以下代码
```kotlin
// 如果在子项目中，比如 ./bukkit/nms，请使用 :bukkit:nms
include(":nms")
// 如果在子项目中，比如 ./bukkit/nms，请使用 bukkit/nms
File("nms").listFiles()?.forEach { dir ->
    // 这里已经隐含了 dir 必须是一个文件夹，因此无需额外判断
    if (File(dir, "build.gradle.kts").exists()) {
        // 同上
        include(":nms:${dir.name}")
    }
}
```

## 3.配置依赖

在需要引用 `nms` 的项目构建脚本 `build.gradle.kts` 中添加以下内容
```kotlin
// 新建 shadowLink 配置
configurations.create("shadowLink")
dependencies{
    // ... 其它依赖

    // NMS 接口以及实现
    for (nms in project.project(":nms").also {
        implementation(it)
    }.subprojects) {
        if (nms.name == "shared") implementation(nms)
        if (nms.name.startsWith("v")) add("shadowLink", nms)
    }
}
tasks {
    shadowJar {
        archiveClassifier.set("") // 个人习惯
        
        // 添加 shadowLink 配置到打包任务，不在代码进行依赖引用，单纯打包 NMS 实现进去，即可杂交编译目标
        configurations.add(project.configurations.getByName("shadowLink"))
        // 将 top.mrxiaom.example 换成你自己的包
        relocate("nms.impl", "top.mrxiaom.example.nms")
    }
    build { dependsOn(shadowJar) } // 个人习惯
}
```

## 4.进行设置

打开 `nms/build.gradle.kts`，文件开头有一点配置项
```kotlin
val targetJavaVersion = 8
val source = NMSSource.RoseWoodDev
val sharedSpigotAPI = "1.21"
```
比较重要的是 `NMSSource`，即 NMS 依赖来源。目前可以使用 `RoseWoodDev` 或者 `CodeMC`，如果你想的话，在文件结尾还可以添加更多依赖源。 

## 5.最终使用

以下代码写进插件主类
```java
@Override
public void onEnable() {
    // 初始化 NMS，失败时卸载插件
    if (!Versions.init(getLogger())) {
        Bukkit.getPluginManager().disablePlugin(this);
        return;
    }
    // ...你自己的插件加载逻辑
}
```

```java
public void foo() {
    // 需要使用的时候，通过 Versions 来获取自己放出的 NMS 接口
    ILivingEntity nms = Versions.getLivingEntity();
}
```

需要添加版本支持，复制 nms 子项目中 `v1_xx_Rx` 命名格式的任意一个项目，并且重新命名（[这里 (VERSION_TO_REVISION)](https://github.com/tr7zw/Item-NBT-API/blob/master/item-nbt-api/src/main/java/de/tr7zw/changeme/nbtapi/utils/MinecraftVersion.java)是规则），修改构建脚本中 `setupNMS` 函数的参数即可。

如果需要添加类，先在 `shared` 模块添加接口，然后再在所有 `v1_xx_Rx` 子项目中添加实现，最后在 `Versions.java` 中添加版本对应关系、反射初始化实现即可。

有时可能会遇到一些 NMS 方法需要引用 authlib、 datafixer、

先下载 `https://bmclapi2.bangbang93.com/version/版本/json` 然后打开（你有客户端的话，打开 `.minecraft/versions/版本/版本.json` 也行），格式化，并且搜索相关依赖。  
这里以 1.21 为例，搜索 `authlib` 会得到下面这段
```json5
{
  "downloads": {
    // ... downloads 没啥用，省略
  },
  "name": "com.mojang:authlib:6.0.54"
}
```
复制 `name` 的值，添加到相应版本的 `dependencies` 里
```kotlin
setupJava(21)
dependencies {
    setupNMS("1.21")
    compileOnly("com.mojang:authlib:6.0.54")
}
```
因为 `1.17+` 的依赖基本都要 Java 17 才能引用，`1.20.5+` 要 Java 21，所以还要加个 `setupJava` 设定子项目的编译目标版本。

### 1.7.x

以上配置方法只适用于 `1.8+` 的版本。如果你需要 `1.7.x`，不能使用 `setupNMS` 方法，应该手动添加 `CodeMC` 的仓库，并添加 `craftbukkit` 依赖。
```kotlin
repositories {
    maven("https://repo.codemc.io/repository/nms/")
}
dependencies {
    compileOnly("org.bukkit.bukkit:1.7.10-R0.1-SHAPSHOT")
    compileOnly("org.bukkit.craftbukkit:1.7.10-R0.1-SHAPSHOT")
}
```

更低的版本（`1.6` 及以下）我没找到，可能已经随着 [DMCA Takedown](https://github.com/github/dmca/blob/master/2014/2014-09-05-CraftBukkit.md)，彻底消失在视野之外了。这么老的版本，即使是模组服估计也不会再考虑，不作支持也是合理的。

# 鸣谢
+ [RoseWoodDev 仓库](https://repo.rosewooddev.io) 分发 NMS 依赖 (较快)
+ [CodeMC 仓库](https://repo.codemc.io/) 分发 NMS 依赖 (速度时快时慢，但是平台体量大，不容易挂)
+ [PaperMC 仓库](https://repo.papermc.io/) 分发 `com.mojang` 包下的几乎所有原版依赖
