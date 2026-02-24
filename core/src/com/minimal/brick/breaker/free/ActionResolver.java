package com.minimal.brick.breaker.free;

public interface ActionResolver {
    void preloadInterstitial();
    void onNaturalBreak();
    void showBanner();
    void hideBanner();
    int getBannerHeightPx();
}
