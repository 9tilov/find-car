package com.moggot.findmycarlocation.di

import com.moggot.findmycarlocation.core.exception.ComponentNotInitializedException
import timber.log.Timber
import java.util.*

object ComponentHolder {
    private val components = HashMap<String, Any>()

    fun provideApplicationProvider(): ApplicationProvider =
        components[ApplicationProvider::class.java.name] as ApplicationProvider

    @Suppress("UNCHECKED_CAST")
    fun <C : Any> provideComponent(tag: String, provider: (() -> C)? = null): C {
        if (components.containsKey(tag)) {
            return components[tag] as C
        }

        if (provider == null) {
            throw ComponentNotInitializedException(tag)
        }

        val component = provider.invoke()

        components[tag] = component

        Timber.d("provideComponent $component")
        return component
    }

    fun releaseComponent(tag: String) {
        components.remove(tag)
    }
}
