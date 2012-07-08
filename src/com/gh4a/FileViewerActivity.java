/*
 * Copyright 2011 Azwan Adli Abdullah
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gh4a;

import org.eclipse.egit.github.core.Content;
import org.eclipse.egit.github.core.util.EncodingUtils;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.gh4a.loader.ContentLoader;
import com.gh4a.utils.StringUtils;

public class FileViewerActivity extends BaseSherlockFragmentActivity 
    implements LoaderManager.LoaderCallbacks<Content> {

    protected String mRepoOwner;
    protected String mRepoName;
    private String mPath;
    private String mRef;
    private String mSha;
    private String mName;
    private Content mContent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.web_viewer);
        setUpActionBar();

        mRepoOwner = getIntent().getStringExtra(Constants.Repository.REPO_OWNER);
        mRepoName = getIntent().getStringExtra(Constants.Repository.REPO_NAME);
        mPath = getIntent().getStringExtra(Constants.Object.PATH);
        mRef = getIntent().getStringExtra(Constants.Object.REF);
        mSha = getIntent().getStringExtra(Constants.Object.OBJECT_SHA);
        mName = getIntent().getStringExtra(Constants.Object.NAME);
        
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(mName);
        actionBar.setSubtitle(mRepoOwner + "/" + mRepoName);
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        getSupportLoaderManager().initLoader(0, null, this);
        getSupportLoaderManager().getLoader(0).forceLoad();
    }

    private void fillData(Content content, boolean highlight) {
        String data = new String(EncodingUtils.fromBase64(content.getContent()));
        WebView webView = (WebView) findViewById(R.id.web_view);

        WebSettings s = webView.getSettings();
        s.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        s.setUseWideViewPort(false);
        s.setAllowFileAccess(true);
        s.setBuiltInZoomControls(true);
        s.setLightTouchEnabled(true);
        s.setLoadsImagesAutomatically(true);
        s.setPluginsEnabled(false);
        s.setSupportZoom(true);
        s.setSupportMultipleWindows(true);
        s.setJavaScriptEnabled(true);

        webView.getSettings().setUseWideViewPort(true);

        String highlighted = StringUtils.highlightSyntax(data, highlight, mName);
        webView.setWebViewClient(webViewClient);
        webView.loadDataWithBaseURL("file:///android_asset/", highlighted, "text/html", "utf-8", "");
    }

    private WebViewClient webViewClient = new WebViewClient() {

        @Override
        public void onPageFinished(WebView webView, String url) {
        }
        
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
    };

    @Override
    public Loader<Content> onCreateLoader(int arg0, Bundle arg1) {
        return new ContentLoader(this, mRepoOwner, mRepoName, mPath, mRef);
    }

    @Override
    public void onLoadFinished(Loader<Content> loader, Content content) {
        if (content != null) {
            fillData(content, true);
        }
    }

    @Override
    public void onLoaderReset(Loader<Content> arg0) {
        // TODO Auto-generated method stub
        
    }

}
