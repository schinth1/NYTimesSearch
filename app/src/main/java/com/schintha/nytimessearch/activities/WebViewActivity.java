package com.schintha.nytimessearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.schintha.nytimessearch.R;
import com.schintha.nytimessearch.models.ArticleModel;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WebViewActivity extends AppCompatActivity {

    @Bind(R.id.wvArticle)
    WebView wvArticle;
    @Bind(R.id.btnShare) FloatingActionButton btnShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);

        final ArticleModel article = (ArticleModel) Parcels.unwrap(getIntent().getParcelableExtra("article"));
        wvArticle.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        wvArticle.loadUrl(article.getWebUrl());
        wvArticle.getSettings().setJavaScriptEnabled(true);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/email");
                shareIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"palemgangireddy@gmail.com"});
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, article.getHeadline());
                shareIntent.putExtra(Intent.EXTRA_TEXT, article.getWebUrl());
                WebViewActivity.this.startActivity(shareIntent.createChooser(shareIntent, "Send Email..."));
            }
        });
    }
}
