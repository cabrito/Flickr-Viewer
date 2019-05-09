package io.github.scalrx.flickrviewer.util;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import io.github.scalrx.flickrviewer.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageFragment extends Fragment
{
    // Members
    private FlickrImage flickrImage;
    private TextView title;
    private ImageView image;

    public ImageFragment()
    {
        // Required empty public constructor
    }

    public static ImageFragment newInstance(FlickrImage flickrImage)
    {
        ImageFragment imageFragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString("title", flickrImage.getTitle());
        args.putString("url", flickrImage.getUrl());
        imageFragment.setArguments(args);
        return imageFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        String title = getArguments().getString("title");
        String url = getArguments().getString("url");
        flickrImage = new FlickrImage(title, url);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        title = (TextView) view.findViewById(R.id.flickrTitle);
        image = (ImageView) view.findViewById(R.id.flickrImage);

        // Download the image as we need it to help save memory
        DownloadAsync task = new DownloadAsync();
        task.execute(flickrImage);
        return view;
    }

    // Logic for downloading a particular file
    private class DownloadAsync extends AsyncTask<FlickrImage, Integer, IOException>
    {
        // Downloads a bitmap from an internet source
        private Bitmap getBitmapFromUrl(String address)
        {
            try
            {
                URL url = new URL(address);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();

                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);

                return bitmap;
            }
            catch(IOException e)
            {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(IOException e)
        {
            super.onPostExecute(e);
            if(e != null)
            {
                title.setText(R.string.bitmap_download_failed);
            }
        }

        @Override
        protected IOException doInBackground(FlickrImage... flickrImages)
        {
            Bitmap bitmap = getBitmapFromUrl(flickrImage.getUrl());
            if(bitmap != null)
            {
                image.setImageBitmap(bitmap);
                return null;
            }
            else
            {
                return new IOException();
            }
        }
    }
}
