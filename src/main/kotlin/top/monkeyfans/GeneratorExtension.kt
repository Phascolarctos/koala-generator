package top.monkeyfans

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property

interface GeneratorExtension {
    val entityPackageName: Property<String>
    val dtoPackageName: Property<String>
    val dtoClassSuffixName: Property<String>
    val generatorType: Property<GeneratorType>
    val classesDir: DirectoryProperty
}