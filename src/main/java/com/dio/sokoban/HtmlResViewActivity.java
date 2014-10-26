/*
 *  sokoban - a Sokoban game for android devices
 *  Copyright (C) 2010 Dedi Hirschfeld
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package com.dio.sokoban;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * A viewer for HTML resources. This viewer supports the scheme
 * 'android.resource:' in addition to regular schemes. using this scheme will
 * load a resource from the 'raw' directory, which can be localized.
 *
 * @author dedi
 */
public class HtmlResViewActivity extends Activity
{
    //
    // Members.
    //

    /**
     * The actual WebView.
     */
    private WebView m_webView;


    //
    // Operations.
    //

    /**
     * Create an HtmlResViewActivity, associated with the given document.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        m_webView = new WebView(this);

        // Set a client  that can handle resource URIs.
        // This is needed because the default WebView doesn't handle resource
        // URIs - and those are needed so that the help pages can (at some
        // point) be localized.
        m_webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TBD: This will be done synchronously. Is this good enough?
                return HtmlResViewActivity.this.loadResource(url);
            }
        });

        Intent ourIntent = getIntent();
        Uri resourceUri = ourIntent.getData();

        if (!loadResource(ourIntent.getDataString()))
        {
            String resourceUriStr = resourceUri.toString();
            m_webView.loadUrl(resourceUriStr);
        }
        setContentView(m_webView);
    }

    /**
     * Load a resource URI into the webview.
     *
     * @param resourceUri
     *            The resource URI.
     */
    private void loadResourceData(Uri resourceUri)
    {
        ContentResolver resolver = getContentResolver();
        String content;
        try
        {
            InputStream resourceStream = resolver.openInputStream(resourceUri);
            InputStreamReader resourceReader =
                new InputStreamReader(resourceStream);
            content=readFully(resourceReader);
        } catch (IOException e) {
            content = getString(R.string.ERR_URL_LOAD, resourceUri);
        }

        Log.d("Dedi", "Setting content to " + content);
        m_webView.loadDataWithBaseURL(resourceUri.toString(), content,
                "text/html", "UTF-8", null);
    }

    /**
     * Read the content of the given reader, and return it as a sting.
     */
    private String readFully(Reader reader) throws IOException
    {
        char[] charBuffer = new char[1024];
        StringBuffer strBuffer = new StringBuffer();
        int readCount;
        while ((readCount = reader.read(charBuffer)) > 0)
            strBuffer.append(charBuffer, 0, readCount);
        return strBuffer.toString();
    }

    /**
     * Launch a browser activity to view the given URI.
     * @param uri
     */
    private void launchBrowserActivity(Uri uri)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        startActivity(intent);
    }

    /**
     * If the given URL is a resource URL, load it into the webview. Otherwise
     * try to launch an external activity to view the URL. This way, external
     * URLs will be opened in an external browser activity, while local files
     * will be opened in the same window.
     */
    private boolean loadResource(String url) {
        Uri resourceUri = Uri.parse(url);
        String scheme = resourceUri.getScheme();

        if (scheme.equals(ContentResolver.SCHEME_ANDROID_RESOURCE))
        {
            Log.d("Dedi", "Handling " + url);
            loadResourceData(resourceUri);
        }
        else
        {
            Log.d("Dedi", "Launching browser for " + url);
            launchBrowserActivity(resourceUri);
        }
        return true;
    }
}
