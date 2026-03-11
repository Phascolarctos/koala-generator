package top.monkeyfans

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import java.lang.classfile.ClassFile
import java.lang.classfile.FieldModel
import java.lang.constant.ClassDesc
import java.lang.constant.MethodTypeDesc
import java.lang.reflect.AccessFlag
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

abstract class KoalaGeneratorTask : DefaultTask() {
    @get:Input
    abstract val entityPackageName: Property<String>

    @get:Input
    abstract val dtoPackageName: Property<String>

    @get:Input
    abstract val dtoClassSuffixName: Property<String>

    @get:Input
    abstract val generatorType: Property<GeneratorType>

    @get:InputDirectory
    @get:Optional
    abstract val classesDir: DirectoryProperty

    @TaskAction
    fun action() {
        if (classesDir.isPresent) {
            val mainPath = classesDir.get().toString()
            classesDir.get().asFileTree.forEach {

                val model = ClassFile.of().parse(Paths.get(it.path))
                val symbol = model.thisClass().asSymbol()
                val dtoClassName = spliceClassName(symbol.displayName())

                if (entityPackageName.get() == model.thisClass().asSymbol().packageName()) {
                    generateClassFile(dtoClassName, model.fields(), mainPath)
                }
                logger.lifecycle(model.thisClass().asSymbol().packageName() + "-> package name")


                logger.lifecycle(it.path + "-> class")
            }
        }
    }

    private fun spliceClassName(className: String): String {
        return className + dtoClassSuffixName.get()
    }

    /**
     * fields name type mapping
     */
    private fun generateClassFile(classFileName: String, fieldModels: List<FieldModel>, mainPath: String) {
        val classDesc = ClassDesc.of(dtoPackageName.get(), classFileName)
        val classBytes = ClassFile.of().build(classDesc) { builder ->
            builder.withFlags(AccessFlag.PUBLIC)
            builder.withMethod("<init>", MethodTypeDesc.ofDescriptor("()V"), ClassFile.ACC_PUBLIC) { methodBuilder ->
                methodBuilder.withCode { codeBuilder ->
                    codeBuilder.aload(0)
                    codeBuilder.invokespecial(
                        ClassDesc.of("java.lang.Object"),
                        "<init>",
                        MethodTypeDesc.ofDescriptor("()V")
                    )
                    codeBuilder.return_()
                }
            }
            fieldModels.forEach { fieldModel ->
                builder.withField(fieldModel.fieldName(), fieldModel.fieldType(), fieldModel.flags().flagsMask())
            }
        }
        val dtoPath = dtoPackageName.map { it.replace('.', '/') }.get()
        val path = Path.of(mainPath).resolve(dtoPath).resolve("$classFileName.class")
        Files.createDirectories(path.parent)
        Files.write(path, classBytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
    }
}
