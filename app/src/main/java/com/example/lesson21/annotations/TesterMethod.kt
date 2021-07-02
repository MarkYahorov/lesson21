package com.example.lesson21.annotations

@Target(AnnotationTarget.FUNCTION)
@Retention(value = AnnotationRetention.RUNTIME)
annotation class TesterMethod(val description: String, val isInner: Boolean = false)
