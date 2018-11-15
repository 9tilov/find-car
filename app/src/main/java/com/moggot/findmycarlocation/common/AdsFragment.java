package com.moggot.findmycarlocation.common;

import android.os.Bundle;
import android.support.annotation.Nullable;

public abstract class AdsFragment<M extends BaseViewModel> extends BaseFragment<M> {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBilling();
    }

    public abstract void initBilling();
}
