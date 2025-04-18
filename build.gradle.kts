plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

val targetJavaVersion = 8

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://mirrors.huaweicloud.com/repository/maven")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.rosewooddev.io/repository/public/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    mavenCentral()
    maven("https://jitpack.io/")
    maven("https://oss.sonatype.org/content/groups/public/")
}

// 新建 shadowLink 配置
configurations.create("shadowLink")
@Suppress("VulnerableLibrariesLocal")
dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21-R0.1-SNAPSHOT")
    implementation("org.jetbrains:annotations:24.0.0")

    // NMS 接口以及实现
    for (nms in project.project(":nms").also {
        implementation(it)
    }.subprojects) {
        if (nms.name == "shared") implementation(nms)
        if (nms.name.startsWith("v")) add("shadowLink", nms)
    }
}
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    if (JavaVersion.current() < javaVersion) {
        val lang = JavaLanguageVersion.of(targetJavaVersion)
        toolchain.languageVersion.set(lang)
    }
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

tasks {
    shadowJar {
        archiveClassifier.set("")

        // 添加 shadowLink 配置到打包任务，不在代码进行依赖引用，单纯打包 NMS 实现进去，即可杂交编译目标
        configurations.add(project.configurations.getByName("shadowLink"))
        // 将 top.mrxiaom.example 换成你自己的包
        relocate("nms.impl", "top.mrxiaom.example.nms")
    }
    build {
        dependsOn(shadowJar)
    }

    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        sourceCompatibility = targetJavaVersion.toString()
        targetCompatibility = targetJavaVersion.toString()
    }

    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        from("LICENSE")
        from(sourceSets.main.get().resources.srcDirs) {
            expand(mapOf("version" to project.version))
            include("plugin.yml")
        }
    }
}
