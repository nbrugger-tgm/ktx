package eu.niton.ktx.annotation

import kotlin.reflect.KClass
import kotlin.reflect.KType

@Target(AnnotationTarget.FILE)
annotation class GenerateKtx(
  val eventType: KClass<*>
){}
