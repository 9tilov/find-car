package com.moggot.findmycarlocation.common

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<T : ViewBinding> : AppCompatActivity() {

    protected lateinit var viewBinding: T

    abstract fun performDataBinding(): T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = performDataBinding()
        setContentView(viewBinding.root)
    }
}
