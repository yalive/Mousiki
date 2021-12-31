#!/usr/bin/env kotlin
import java.io.File

val newVersion = args.first()
val semVersion = newVersion.split(".")
if (semVersion.count() != 3) {
    error("Not using semantic versioning!!")
}
semVersion[0].toIntOrNull() ?: error("Major version must be integer")
semVersion[1].toIntOrNull() ?: error("Minor version must be integer")
semVersion[2].toIntOrNull() ?: error("Patch version must be integer")

val input = File("./iOS-framwork/shared.podspec")
val versionLine = input.readLines().map { it.trim() }
    .first { it.startsWith("spec.version") }

val currentVersion = versionLine.split("=")[1]
    .trim()
    .removePrefix("'")
    .removeSuffix("'")

println("currentVersion=${currentVersion}")
println("newVersion=${newVersion}")

// Replace version
val tempFile = File.createTempFile("version", null)
tempFile.printWriter().use { writer ->
    input.forEachLine { line ->
        writer.println(
            when {
                line.trim().startsWith("spec.version") -> line.replace(currentVersion, newVersion)
                else -> line
            }
        )
    }
}

check(input.delete() && tempFile.renameTo(input)) { "failed to replace file" }