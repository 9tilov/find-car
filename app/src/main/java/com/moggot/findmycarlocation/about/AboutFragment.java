package com.moggot.findmycarlocation.about;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.view.View;
import android.widget.TextView;

import com.moggot.findmycarlocation.BuildConfig;
import com.moggot.findmycarlocation.MainActivity;
import com.moggot.findmycarlocation.R;
import com.moggot.findmycarlocation.base.BaseFragment;
import com.moggot.findmycarlocation.di.component.MainComponent;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

public class AboutFragment extends BaseFragment {

    private static final String TAG = "AboutFragment";

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tvVersion = view.findViewById(R.id.about_tv_version);
        TextView tvCopyright = view.findViewById(R.id.about_tv_copyright);
        ConstraintLayout clPrivacyPolicy = view.findViewById(R.id.about_cl_privacy_policy);
        ConstraintLayout clRemoveAds = view.findViewById(R.id.about_cl_purchase_premium);

        tvVersion.setText(getString(R.string.version, BuildConfig.VERSION_NAME));
        tvCopyright.setText(getString(R.string.copyright, Calendar.getInstance().get(Calendar.YEAR)));
        MainActivity activity = ((MainActivity) getActivity());
        if (activity == null) {
            return;
        }
        if (activity.isPremiumPurchased()) {
            clRemoveAds.setVisibility(View.GONE);
        }
        clPrivacyPolicy.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), PrivacyPolicyActivity.class));
        });
        clRemoveAds.setOnClickListener(v -> activity.getBillingManager().requestSubscription());
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_about;
    }

    @NotNull
    @Override
    public String getComponentName() {
        return MainComponent.class.getName();
    }

    @Override
    public boolean isComponentDestroyable() {
        return false;
    }
}
