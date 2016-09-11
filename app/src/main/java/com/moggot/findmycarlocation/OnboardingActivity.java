package com.moggot.findmycarlocation;

import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro2;

public class OnboardingActivity extends AppIntro2 {
    @Override
    public void init(Bundle savedInstanceState) {

// Здесь указываем количество слайдов, например нам нужно 3
        addSlide(OnboardingFragment.newInstance(R.layout.onboarding_screen1)); //
        addSlide(OnboardingFragment.newInstance(R.layout.onboarding_screen2));
        addSlide(OnboardingFragment.newInstance(R.layout.onboarding_screen3));

    }

        @Override
    public void onNextPressed() {
        // Do something here
    }

    @Override
    public void onDonePressed() {
        SharedPreference.SaveTutorialStatus(this, false);
        finish();
    }

    @Override
    public void onSlideChanged() {
        // Do something here
    }

}
