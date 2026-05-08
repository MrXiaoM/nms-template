plugins {
    java
    id ("com.gradleup.shadow") version "9.3.0"
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

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:24.0.0")

    // NMS 接口以及实现
    for (nms in project.project(":nms").subprojects) {
        if (nms.name == "shared") implementation(nms)
        else add("shadowLink", nms)
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
        // 添加 shadowLink 配置到打包任务，不在代码进行依赖引用，单纯打包 NMS 实现进去，即可杂交编译目标
        configurations.add(project.configurations.getByName("shadowLink"))
        // shadowJar 新版本需要手动添加需要打包的配置
        configurations.add(project.configurations.runtimeClasspath.get())
        // 将 top.mrxiaom.example 换成你自己的包
        relocate("nms.impl", "top.mrxiaom.example.nms")
    }
    val jarName = "${project.name}-$version.jar"
    val copyTask = register<Copy>("copyBuildArtifact") {
        dependsOn(shadowJar)
        from(shadowJar.get().outputs)
        rename { jarName }
        into(rootProject.file("out"))
    }
    build {
        dependsOn(copyTask)
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
