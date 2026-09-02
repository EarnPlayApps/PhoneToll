package com.phonetoll.app;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "PhoneTollAdMob";

    private static final String PHONE_TOLL_URL =
            "https://phonetoll.earnplayapps.workers.dev/";

    private static final String BANNER_AD_UNIT =
            "ca-app-pub-1349855536683235/9698485448";

    private WebView webView;
    private AdView adView;
    private TextView statusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);

        setupWebView();
        setupAdMob();
    }

    private void setupWebView() {

        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(false);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);

        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(PHONE_TOLL_URL);
    }

    private void setupAdMob() {

        statusView = new TextView(this);
        statusView.setText("AdMob: initializing...");
        statusView.setTextSize(12);
        statusView.setTextColor(Color.DKGRAY);
        statusView.setPadding(8, 4, 8, 4);

        FrameLayout adContainer = findViewById(R.id.adContainer);

        adContainer.addView(
                statusView,
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );

        MobileAds.initialize(this, initializationStatus -> {

            Log.d(TAG, "AdMob initialized");

            runOnUiThread(() ->
                    statusView.setText("AdMob initialized - loading banner...")
            );

            loadBanner();
        });
    }

    private void loadBanner() {

        FrameLayout adContainer = findViewById(R.id.adContainer);

        adView = new AdView(this);

        adView.setAdUnitId(BANNER_AD_UNIT);
        adView.setAdSize(getAdSize());

        adView.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                Log.d(TAG, "BANNER LOADED");

                runOnUiThread(() ->
                        statusView.setText("AdMob: BANNER LOADED")
                );
            }

            @Override
            public void onAdFailedToLoad(LoadAdError error) {

                Log.e(
                        TAG,
                        "BANNER FAILED: code="
                                + error.getCode()
                                + " message="
                                + error.getMessage()
                                + " domain="
                                + error.getDomain()
                );

                runOnUiThread(() ->
                        statusView.setText(
                                "AdMob ERROR " +
                                error.getCode() +
                                ": " +
                                error.getMessage()
                        )
                );
            }

            @Override
            public void onAdOpened() {
                Log.d(TAG, "BANNER OPENED");
            }

            @Override
            public void onAdClosed() {
                Log.d(TAG, "BANNER CLOSED");
            }

            @Override
            public void onAdClicked() {
                Log.d(TAG, "BANNER CLICKED");
            }

            @Override
            public void onAdImpression() {
                Log.d(TAG, "BANNER IMPRESSION");
            }
        });

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        adContainer.addView(adView, params);

        AdRequest request = new AdRequest.Builder().build();

        Log.d(TAG, "Requesting banner: " + BANNER_AD_UNIT);

        adView.loadAd(request);
    }

    private AdSize getAdSize() {

        float density =
                getResources().getDisplayMetrics().density;

        float widthPixels =
                getResources().getDisplayMetrics().widthPixels;

        int adWidth =
                (int) (widthPixels / density);

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                this,
                adWidth
        );
    }

    @Override
    protected void onPause() {

        if (webView != null) {
            webView.onPause();
        }

        super.onPause();
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (webView != null) {
            webView.onResume();
        }
    }

    @Override
    protected void onDestroy() {

        if (adView != null) {
            adView.destroy();
        }

        if (webView != null) {
            webView.destroy();
        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
