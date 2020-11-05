package com.boardinglabs.mireta.selada.component.util;

import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

/**
 * Created by Dhimas on 1/9/18.
 */

public class CustomWebChromeClient extends WebChromeClient {
    private final ProgressBar mWebLoading;

    private static final int FINISHED = 100;

    public CustomWebChromeClient(ProgressBar webLoading) {
        mWebLoading = webLoading;
    }

    @Override
    public void onProgressChanged(WebView view, int progress) {
        mWebLoading.setVisibility(View.VISIBLE);
        mWebLoading.setProgress(progress);
        if (progress == FINISHED) {
            mWebLoading.setVisibility(View.INVISIBLE);
        }
    }
}
