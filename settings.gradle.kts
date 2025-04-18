rootProject.name = "ExamplePlugin"

// 如果在子项目中，比如 ./bukkit/nms，请使用 :bukkit:nms
include(":nms")
// 如果在子项目中，比如 ./bukkit/nms，请使用 bukkit/nms
File("nms").listFiles()?.forEach { file ->
    // 这里已经隐含了 dir 必须是一个文件夹，因此无需额外判断
    if (File(file, "build.gradle.kts").exists()) {
        // 同上
        include(":nms:${file.name}")
    }
}
