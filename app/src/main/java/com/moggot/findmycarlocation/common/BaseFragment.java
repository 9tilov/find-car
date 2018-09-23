package com.moggot.findmycarlocation.common;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moggot.findmycarlocation.AppAnalytics;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.AndroidSupportInjection;

public abstract class BaseFragment<M extends BaseViewModel> extends Fragment {

    @Inject
    protected ViewModelProvider.Factory viewModelFactory;
    protected AppAnalytics analytics;
    private M viewModel;
    private Unbinder unbinder;

    @Override
    public void onAttach(Context context) {
        configureDagger();
        super.onAttach(context);
        analytics = new AppAnalytics(getActivity());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.viewModel = ViewModelProviders.of(this, viewModelFactory).get(getViewModel());
        onCreate(savedInstanceState, viewModel);
    }

    protected abstract void onCreate(@Nullable Bundle savedInstanceState, M viewModel);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutResId(), container, false);
        unbinder = ButterKnife.bind(this, view);
        analytics.setCurrentScreen(getFragmentTag());
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        this.viewModel.unsubscribeFromDestroy(this);
    }

    private void configureDagger() {
        AndroidSupportInjection.inject(this);
    }

    protected abstract Class<M> getViewModel();

    public abstract String getFragmentTag();

    @LayoutRes
    protected abstract int getLayoutResId();
}
