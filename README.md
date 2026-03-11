# koala-generator

gradle plugin to generate dto classes from entity package classes

## Points

> FieldModel and ClassFile API

## Usage
```groovy

// id("top.monkeyfans.generator") not support yet

// git clone and setting.gradle.kts include
includeBuild("../koala-generator")

// add config in build.gradle.kts
generator {
    entityPackageName = "top.monkeyfans.entity"
    dtoPackageName = "top.monkeyfans.dto"
}
```
