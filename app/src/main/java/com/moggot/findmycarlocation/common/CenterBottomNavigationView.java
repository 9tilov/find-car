package com.moggot.findmycarlocation.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.Gravity;

import java.lang.reflect.Field;

import timber.log.Timber;

public class CenterBottomNavigationView extends BottomNavigationView {

    public CenterBottomNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        centerMenuIcon();
    }

    @SuppressLint("RestrictedApi")
    private void centerMenuIcon() {
        BottomNavigationMenuView menuView = getBottomMenuView();
        if (menuView != null) {
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView menuItemView = (BottomNavigationItemView) menuView.getChildAt(i);
                AppCompatImageView icon = (AppCompatImageView) menuItemView.getChildAt(0);
                LayoutParams params = (LayoutParams) icon.getLayoutParams();
                params.gravity = Gravity.CENTER;
                menuItemView.setShiftingMode(true);
            }
        }
    }

    private BottomNavigationMenuView getBottomMenuView() {
        Object menuView = null;
        try {
            Field field = BottomNavigationView.class.getDeclaredField("mMenuView");
            field.setAccessible(true);
            menuView = field.get(this);
        } catch (Exception e) {
            Timber.d("getBottomMenuView exception = %s", e.getMessage());
        }
        return (BottomNavigationMenuView) menuView;
    }
}
