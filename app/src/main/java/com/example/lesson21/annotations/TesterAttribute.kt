package com.example.lesson21.annotations

import android.util.Log

@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD)
@Retention(value = AnnotationRetention.SOURCE)
annotation class TesterAttribute(val info: String)
