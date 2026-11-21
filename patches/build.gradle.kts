import com.android.tools.r8.CompilationMode
import com.android.tools.r8.D8
import com.android.tools.r8.D8Command
import com.android.tools.r8.OutputMode
import com.android.tools.r8.utils.ArchiveResourceProvider
import ru.vyarus.gradle.plugin.animalsniffer.AnimalSniffer

plugins {
    alias(libs.plugins.animalsniffer)
}

group = "app.yannick"

patches {
    about {
        name = "Yannick's Morphe Patches"
        description = "Independent custom patches for Android apps"
        source = "https://github.com/yannickdelop4/morphe-patches"
        author = "yannickdelop4"
        contact = "https://github.com/yannickdelop4"
        website = "https://github.com/yannickdelop4/morphe-patches"
        license = "GPLv3"
    }
}

val patchListGeneratorClasspath = configurations.create("patchListGeneratorClasspath")

dependencies {
    compileOnly(libs.gson)
    patchListGeneratorClasspath(libs.gson)
    signature(libs.android.api.signature) { artifact { type = "signature" } }
}

animalsniffer {
    sourceSets = emptyList()
}

tasks {
    register<JavaExec>("generatePatchesList") {
        description = "Build patch list for Morphe Manager"
        dependsOn(build)
        classpath = sourceSets["main"].runtimeClasspath + patchListGeneratorClasspath
        mainClass.set("util.PatchListGeneratorKt")
    }

    val desugarPatchesClasses = register("desugarPatchesClasses") {
        val patchesFile = jar.flatMap { it.archiveFile }
        val classpath = sourceSets["main"].compileClasspath
        val output = layout.buildDirectory.file("animalsniffer/patches-desugared.jar")
        inputs.file(patchesFile)
        inputs.files(classpath)
        outputs.file(output)
        doLast {
            val patchesPath = patchesFile.get().asFile.toPath()
            val outputPath = output.get().asFile.toPath()
            D8.run(
                D8Command.builder()
                    .addProgramResourceProvider(ArchiveResourceProvider.fromArchive(patchesPath, true))
                    .addClasspathFiles(classpath.files.filter { it.exists() }.map { it.toPath() })
                    .setMode(CompilationMode.DEBUG)
                    .setMinApiLevel(26)
                    .setOutput(outputPath, OutputMode.ClassFile)
                    .build()
            )
        }
    }

    val extractPatchesClasses = register<Sync>("extractPatchesClasses") {
        from(desugarPatchesClasses.map { zipTree(it.outputs.files.singleFile) }) {
            include("**/*.class")
        }
        into(layout.buildDirectory.dir("animalsniffer/patches-classes"))
    }

    val checkAndroidApi = register<AnimalSniffer>("checkAndroidApi") {
        description = "Checks that patch code only uses Android APIs."
        group = "verification"
        source(extractPatchesClasses)
        classpath = sourceSets["main"].compileClasspath
        animalsnifferSignatures = configurations["signature"]
        animalsnifferClasspath = configurations["animalsniffer"]
        sourcesDirs = sourceSets["main"].allSource.sourceDirectories
        reports.text.outputLocation = layout.buildDirectory.file("reports/animalsniffer/patches.text")
        reports.csv.outputLocation = layout.buildDirectory.file("reports/animalsniffer/patches.csv")
    }

    check {
        dependsOn(checkAndroidApi)
    }

    named("buildAndroid") {
        dependsOn(checkAndroidApi)
    }

    publish {
        dependsOn("generatePatchesList")
    }
}
