package eu.niton.ktx.processor

import com.google.devtools.ksp.KSTypeNotPresentException
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.visitor.KSEmptyVisitor
import eu.niton.ktx.annotation.GenerateKtx
import eu.niton.ktx.codegen.GenerationTarget


val SCHEME_URL = "html_5.xsd".asResourceUrl()

class Processor(private val env: SymbolProcessorEnvironment) : SymbolProcessor {
    @OptIn(KspExperimental::class)
    private val visitor: KSEmptyVisitor<Unit, Unit> = object : KSEmptyVisitor<Unit, Unit>() {
        override fun defaultHandler(node: KSNode, data: Unit) {
            if (node !is KSFile) return;
            val annotation = node.getAnnotationsByType(GenerateKtx::class).firstOrNull()
            if (annotation != null) {
                Generator(
                    pkg = node.packageName.asString(),
                    target = KSPTarget(env.codeGenerator, node, env.logger),
                    eventType = try {
                        annotation.eventType
                        throw RuntimeException()
                    } catch (e: KSTypeNotPresentException) {
                        e.ksType.declaration as KSClassDeclaration
                    },
                ) {
                    parseXmlSchema(SCHEME_URL)
                }.generate()
            }
        }
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getNewFiles().forEach { it.accept(visitor, Unit) }
        return emptyList()
    }

    class KSPTarget(val filer: CodeGenerator, val rootFile: KSFile, val logger: KSPLogger) : GenerationTarget {
        override fun writeFile(filename: String, packageName: String, fileContent: Appendable.() -> Unit) {
            val file = filer.createNewFile(Dependencies(false, rootFile), packageName, filename)
            file.bufferedWriter(Charsets.UTF_8).use {
                try {
                    it.fileContent()
                    it.flush()
                } catch (e: Exception) {
                    logger.error("Failed to write ${packageName}.${filename}", rootFile)
                    throw e
                }
            }
        }
    }
}