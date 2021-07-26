package com.moggot.findmycarlocation.base

import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    override fun onBackPressed() {
        // Передача нажатия в текущее окно
        val contentFragment = getContentFragment()
        if (contentFragment != null && contentFragment.processBackButton()) {
            // Обработка нажатия перехвачена во фрагменте текущего окна
            return
        }
        super.onBackPressed()
    }

    abstract fun getFragmentContainerId(): Int

    private fun getContentFragment(): BaseFragment? {
        val fragment = supportFragmentManager.findFragmentById(getFragmentContainerId())
        return if (fragment is BaseFragment) {
            fragment
        } else null
    }
}