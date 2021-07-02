package com.example.lesson21

import android.util.Log
import com.example.lesson21.annotations.TesterAttribute
import com.example.lesson21.annotations.TesterMethod




open class Tester(
    @field:TesterAttribute("kilka")
    private val param: String,
) {

    private var protParam = 42

    @TesterMethod(description = "Some public method")
    fun doPublic() {
        Log.e("key", "protected: $param")
    }

    protected fun doProtected() {
        Log.e("key", "protected: $param (${protParam})")
    }

    @TesterMethod(description = "Some private method", isInner = true)
    private fun doPrivate() {
        Log.e("key", "private: $param")
    }

    @TesterMethod(description = "Some private method", isInner = true)
    private fun doPrivateSecond() {
        Log.e("key", "private: $param")
    }
}