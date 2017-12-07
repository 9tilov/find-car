package com.moggot.findmycarlocation.presentation.common;

import android.support.annotation.NonNull;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class BasePresenter<T extends BaseView> {

    private final CompositeDisposable disposable = new CompositeDisposable();
    private T view;

    protected void unSubscribeOnDetach(Disposable... disposables) {
        disposable.addAll(disposables);
    }

    public void onAttach(@NonNull T view) {
        this.view = view;
    }

    public void onDetach() {
        disposable.clear();
        this.view = null;
    }

    protected T getView() {
        return view;
    }
}
