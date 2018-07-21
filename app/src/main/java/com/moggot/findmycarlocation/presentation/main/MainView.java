package com.moggot.findmycarlocation.presentation.main;

import com.moggot.findmycarlocation.presentation.common.BaseView;

public interface MainView extends BaseView {

    void showCarIsParking();

    void showCarIsNotParking();

    void showCantSaveParking();

    void showAd();

    void openMap();

    void enableGear(boolean block);
}
