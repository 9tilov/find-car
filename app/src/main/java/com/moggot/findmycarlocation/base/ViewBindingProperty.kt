package com.moggot.findmycarlocation.base

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

abstract class ViewBindingProperty<in R : Any, T : ViewBinding>(
    private val viewBinder: (R) -> T
) : ReadOnlyProperty<R, T> {

    private var viewBinding: T? = null
    private val lifecycleObserver = ClearOnDestroyLifecycleObserver()
    private var thisRef: R? = null

    protected abstract fun getLifecycleOwner(thisRef: R): LifecycleOwner

    @MainThread
    override fun getValue(thisRef: R, property: KProperty<*>): T {
        checkIsMainThread()
        viewBinding?.let { vb ->
            check(this.thisRef != null && thisRef === this.thisRef) {
                "Instance of ViewBindingProperty can't be shared between classes"
            }
            return vb
        }

        this.thisRef = thisRef
        getLifecycleOwner(thisRef).lifecycle.addObserver(lifecycleObserver)
        return viewBinder(thisRef).also { viewBinding = it }
    }

    @MainThread
    fun clear() {
        checkIsMainThread()

        val thisRef = thisRef ?: return
        this.thisRef = null
        getLifecycleOwner(thisRef).lifecycle.removeObserver(lifecycleObserver)
        mainHandler.post {
            viewBinding = null
        }
    }

    private inner class ClearOnDestroyLifecycleObserver : DefaultLifecycleObserver {

        @MainThread
        override fun onDestroy(owner: LifecycleOwner): Unit = clear()
    }

    private companion object {

        private val mainHandler = Handler(Looper.getMainLooper())
    }
}
