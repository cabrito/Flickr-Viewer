package io.github.scalrx.flickrviewer.util;

import android.graphics.Bitmap;

public class FlickrImage
{
    // Members
    private final String title;
    private final String url;

    public FlickrImage(String title, String url)
    {
        this.title = title;
        this.url = url;
    }

    public String getTitle()
    {
        return title;
    }

    public String getUrl()
    {
        return url;
    }
}
