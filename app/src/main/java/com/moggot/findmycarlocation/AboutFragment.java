package com.moggot.findmycarlocation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.TextView;

import com.moggot.findmycarlocation.presentation.common.BaseFragment;

import java.util.Calendar;

import butterknife.BindView;

public class AboutFragment extends BaseFragment<AboutViewModel> {

    private static final String TAG = "AboutFragment";

    @BindView(R.id.about_tv_version)
    TextView tvVersion;
    @BindView(R.id.about_tv_copyright)
    TextView tvCopyright;
    @BindView(R.id.about_cl_privacy_policy)
    ConstraintLayout clPrivacyPolicy;

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState, AboutViewModel viewModel) {

    }

    @Override
    protected Class<AboutViewModel> getViewModel() {
        return AboutViewModel.class;
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_about;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvVersion.setText(getString(R.string.version, BuildConfig.VERSION_NAME));
        tvCopyright.setText(getString(R.string.copyright, Calendar.getInstance().get(Calendar.YEAR)));
        clPrivacyPolicy.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), PrivacyPolicyActivity.class));
        });
    }
}
