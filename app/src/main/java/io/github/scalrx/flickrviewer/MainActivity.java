package io.github.scalrx.flickrviewer;

import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import io.github.scalrx.flickrviewer.util.FlickrImage;
import io.github.scalrx.flickrviewer.util.FlickrImageAdapter;

public class MainActivity extends AppCompatActivity
{
    // Members
    private ViewPager viewPager;
    private SwipeRefreshLayout srl;
    private FlickrImageAdapter adapter;
    private ArrayList<FlickrImage> list;
    private PageIndicator mIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.viewpager);
        srl = findViewById(R.id.mainSwipeContainer);
        list = new ArrayList<>();
        // Need an AsyncTask to download JSON and parse
        DownloadAsync getFlickrData = new DownloadAsync();
        getFlickrData.execute();

        // For if the user wants to refresh the feed
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srl.setRefreshing(true);
                DownloadAsync getFlickrData = new DownloadAsync();
                getFlickrData.execute();
            }
        });
    }

    private class DownloadAsync extends AsyncTask<Void, Integer, IOException>
    {
        private final String JSON_URL =
                "https://api.flickr.com/services/feeds/photos_public.gne?format=json&nojsoncallback=1";

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            list.clear();
        }

        @Override
        protected void onPostExecute(IOException e)
        {
            super.onPostExecute(e);
            if(e != null)
            {

            }
            else
            {
                if(srl.isRefreshing())
                    srl.setRefreshing(false);
                adapter = new FlickrImageAdapter(getSupportFragmentManager(), list);
                viewPager.setAdapter(adapter);
                mIndicator = (CirclePageIndicator)findViewById(R.id.view_pager_indicator);
                mIndicator.setViewPager(viewPager);
                mIndicator.setCurrentItem(0);
            }
        }

        @Override
        protected IOException doInBackground(Void... voids)
        {
            JSONParser parser = new JSONParser();

            try
            {
                URL flickr = new URL(JSON_URL); // URL to Parse
                URLConnection uc = flickr.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));

                // Parse the JSON
                JSONObject jsonObject = (JSONObject)parser.parse(in);
                JSONArray images = (JSONArray)jsonObject.get("items");

                for(Object image : images)
                {
                    JSONObject imageObject = (JSONObject)image;
                    String title = String.valueOf(imageObject.get("title"));
                    JSONObject media = (JSONObject)((JSONObject) image).get("media");
                    String link = String.valueOf(media.get("m"));
                    String linkCut = link.substring(0, link.lastIndexOf("_")) + link.substring(link.lastIndexOf("."));

                    // Add new FlickrImages to the list
                    FlickrImage flickrImage = new FlickrImage(title, linkCut);
                    list.add(flickrImage);
                    System.out.println(flickrImage.getUrl());
                }
                in.close();
                return null;
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
            return new IOException();
        }
    }
}
