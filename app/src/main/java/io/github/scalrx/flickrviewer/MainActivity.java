package io.github.scalrx.flickrviewer;

import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
    private FlickrImageAdapter adapter;
    private ArrayList<FlickrImage> list;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.viewpager);
        list = new ArrayList<>();
        // Need an AsyncTask to download JSON and parse
        DownloadAsync getFlickrData = new DownloadAsync();
        getFlickrData.execute();
    }

    private class DownloadAsync extends AsyncTask<Void, Integer, IOException>
    {
        private final String JSON_URL =
                "https://api.flickr.com/services/feeds/photos_public.gne?format=json&nojsoncallback=1";

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

            }
            else
            {
                adapter = new FlickrImageAdapter(getSupportFragmentManager(), list);
                viewPager.setAdapter(adapter);
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
                    System.out.println(title);
                    System.out.println(link);                                       // TEMPORARY

                    // Add new FlickrImages to the list
                    FlickrImage flickrImage = new FlickrImage(title, link);
                    list.add(flickrImage);
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
