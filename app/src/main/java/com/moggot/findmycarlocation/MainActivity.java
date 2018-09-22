package com.moggot.findmycarlocation;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.IdRes;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.moggot.findmycarlocation.presentation.common.BaseFragment;
import com.moggot.findmycarlocation.presentation.main.HomeFragment;
import com.moggot.findmycarlocation.presentation.map.GoogleMapFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;

public class MainActivity extends BaseActivity {

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    private int navigationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> showFragment(item.getItemId()));

        if (savedInstanceState != null) {
            bottomNavigationView.setSelectedItemId(navigationId);
        } else {
            showFragment(bottomNavigationView.getSelectedItemId());
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> showFragment(item.getItemId()));
    }

    @Override
    protected void configureDagger() {
        AndroidInjection.inject(this);
    }

    private boolean showFragment(@IdRes int itemId) {
        switch (itemId) {
            case R.id.navigation_home:
                loadFragment(HomeFragment.newInstance());
                return true;
            case R.id.navigation_map:
                loadFragment(GoogleMapFragment.newInstance());
                return true;
            case R.id.navigation_about:
                loadFragment(AboutFragment.newInstance());
                return true;
            default:
                loadFragment(HomeFragment.newInstance());
                return true;
        }
    }

    private void loadFragment(BaseFragment fragment) {
        Fragment cachedFragment = getSupportFragmentManager().findFragmentByTag(fragment.getFragmentTag());
        if (cachedFragment == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, fragment, fragment.getFragmentTag())
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServicesAvailable();
        bottomNavigationView.setSelectedItemId(navigationId);
    }

    private void checkPlayServicesAvailable() {
        final GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int status = apiAvailability.isGooglePlayServicesAvailable(this);

        if (status != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(status)) {
                apiAvailability.getErrorDialog(this, status, 1).show();
            } else {
                Snackbar.make(bottomNavigationView, "Google Play Services unavailable. This app will not work", Snackbar.LENGTH_INDEFINITE).show();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        navigationId = bottomNavigationView.getSelectedItemId();
    }

}
