package com.example.mycoupons.event;

//used to show and hide couponFragments in Tablets
public class FabVisibilityChangeEvent {

    public boolean showFab;

    public FabVisibilityChangeEvent(boolean showFab){
        this.showFab = showFab;
    }

}
