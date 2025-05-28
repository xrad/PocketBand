import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

project.tasks.register("stk") {
    val inputFile = File(project.projectDir, "src/commonMain/stkgen/def.txt")

    // Define the output files (header and Kotlin files)
    val cppHeaderFile = File(project.projectDir, "build/generated/stkgen/cpp/stkgen.h")
    val kotlinFile = File(project.projectDir, "build/generated/stkgen/kotlin/StkGen.kt")

    // Set the input and output to trigger the task only when the input file changes
    inputs.file(inputFile)
    outputs.files(cppHeaderFile, kotlinFile)

    doLast {
        // Simulate generation of C++ header file from interface.txt
        cppHeaderFile.writeText("""
            // Generated C++ Header
            // Based on ${inputFile.name}
            
            #ifndef GENERATED_HEADER_H
            #define GENERATED_HEADER_H
                 
        """.trimIndent())

        // Simulate generation of Kotlin file from interface.txt
        kotlinFile.writeText("""
            package de.nullgrad.pocketband.stk
            
            // Generated Kotlin File
            // Based on ${inputFile.name}
            
            object StkGen {
            
        """.trimIndent())

        inputFile.forEachLine {
            if (it.startsWith("//") || it.isBlank()) {
                cppHeaderFile.appendText("$it\n")
                kotlinFile.appendText("$it\n")
            }
            else {
                cppHeaderFile.appendText("const int $it;\n")
                kotlinFile.appendText("const val $it\n")
            }
        }

        kotlinFile.appendText("""
            
            }
        """.trimIndent())

        cppHeaderFile.appendText("""
            
            #endif // GENERATED_HEADER_H
        """.trimIndent())

        //println("Generated C++ header at: ${cppHeaderFile.absolutePath}")
        //println("Generated Kotlin file at: ${kotlinFile.absolutePath}")
    }
}
