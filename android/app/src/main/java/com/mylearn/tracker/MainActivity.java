package com.mylearn.tracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

/** Thin wrapper: shows the tracker web UI served by the Spring Boot server. */
public class MainActivity extends Activity {
    private WebView web;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        prefs = getSharedPreferences("app", MODE_PRIVATE);
        web = new WebView(this);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView v, WebResourceRequest req, WebResourceError err) {
                if (req.isForMainFrame()) askUrl("Can't reach server: " + err.getDescription());
            }
        });
        setContentView(web);

        String url = prefs.getString("url", null);
        if (state != null) web.restoreState(state);
        else if (url == null) askUrl(null);
        else web.loadUrl(url);
    }

    private void askUrl(String error) {
        EditText input = new EditText(this);
        input.setText(prefs.getString("url", "http://192.168.1.10:8080"));
        new AlertDialog.Builder(this)
                .setTitle("Server URL")
                .setMessage(error)
                .setView(input)
                .setCancelable(false)
                .setPositiveButton("Open", (d, w) -> {
                    String url = input.getText().toString().trim();
                    prefs.edit().putString("url", url).apply();
                    web.loadUrl(url);
                })
                .show();
    }

    @Override
    protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        web.saveState(out);
    }

    @Override
    public void onBackPressed() {
        if (web.canGoBack()) web.goBack();
        else super.onBackPressed();
    }
}
