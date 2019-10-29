package com.moggot.findmycarlocation.base

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BasePresenter<V : BaseView> {

    protected var view: V? = null
    protected val disposable = CompositeDisposable()

    open fun unSubscribeOnDetach(vararg disposables: Disposable) {
        disposable.addAll(*disposables)
    }

    fun attachView(view: V) {
        this.view = view
        onViewAttached(view)
    }

    open fun onViewAttached(view: V) {}

    fun view(action: V.() -> Unit) {
        view?.run(action)
    }

    fun detachView() {
        disposable.clear()
        view = null
    }
}