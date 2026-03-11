package top.monkeyfans

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile

class KoalaGeneratorPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create("generator", GeneratorExtension::class.java)
        extension.entityPackageName.convention("top.monkeyfans.entity")
        extension.dtoPackageName.convention("top.monkeyfans.dto")
        extension.dtoClassSuffixName.convention("Dto")
        extension.generatorType.convention(GeneratorType.NORMAL_CLASS)

        project.tasks.register("generator", KoalaGeneratorTask::class.java) { task ->
            task.description = "Generator dto classes from entity classes"
            task.entityPackageName.set(extension.entityPackageName)
            task.dtoPackageName.set(extension.dtoPackageName)
            task.dtoClassSuffixName.set(extension.dtoClassSuffixName)
            task.generatorType.set(extension.generatorType)

            project.tasks.findByName("compileJava")?.let { compileJavaTask ->
                if (compileJavaTask is JavaCompile) {
                    task.classesDir.set(compileJavaTask.destinationDirectory)
                }
            }
        }

    }
}
