package eu.niton.ktx.codegen

import eu.niton.ktx.processor.FileContent

interface GenerationTarget {
    fun writeFile(filename: String, packageName: String, fileContent: Appendable.()->Unit)
}