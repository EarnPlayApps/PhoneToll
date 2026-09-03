package com.phonetoll.app;

import android.os.Bundle;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity {

    private static final String PHONE_TOLL_URL =
            "https://phonetool.earnplayapps.workers.dev/";

    private static final String BANNER_AD_UNIT =
            "ca-app-pub-1349855536683235/4188806760";

    private WebView webView;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setupWebView();
        setupAdMob();
    }

    private void setupWebView() {

        webView = findViewById(R.id.webView);

        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(false);

        CookieManager cookies = CookieManager.getInstance();
        cookies.setAcceptCookie(true);
        cookies.setAcceptThirdPartyCookies(webView, true);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean onRenderProcessGone(
                    WebView view,
                    RenderProcessGoneDetail detail) {

                if (webView != null) {
                    ViewGroup parent = (ViewGroup) webView.getParent();

                    if (parent != null) {
                        parent.removeView(webView);
                    }

                    webView.destroy();
                    webView = null;
                }

                return true;
            }
        });

        webView.loadUrl(PHONE_TOLL_URL);
    }

    private void setupAdMob() {

        MobileAds.initialize(this, initializationStatus -> {
            loadBanner();
        });
    }

    private void loadBanner() {

        FrameLayout adContainer =
                findViewById(R.id.adContainer);

        adView = new AdView(this);

        adView.setAdUnitId(BANNER_AD_UNIT);
        adView.setAdSize(getAdSize());

        adContainer.addView(
                adView,
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );

        AdRequest request =
                new AdRequest.Builder().build();

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
            adView = null;
        }

        if (webView != null) {
            webView.destroy();
            webView = null;
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
