package com.moggot.findmycarlocation.base

abstract class BaseMvpFragment<V : BaseView, out P : BasePresenter<V>> : BaseFragment(), BaseView {

    abstract val presenter: P

    @Suppress("UNCHECKED_CAST")
    override fun onStart() {
        super.onStart()
        presenter.attachView(this as V)
    }

    override fun onStop() {
        super.onStop()
        presenter.detachView()
    }
}