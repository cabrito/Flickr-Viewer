package io.github.scalrx.flickrviewer.util;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FlickrImageAdapter extends FragmentStatePagerAdapter
{
    // Members
    private ArrayList<FlickrImage> list;

    // Constructor
    public FlickrImageAdapter(FragmentManager fm, ArrayList<FlickrImage> list)
    {
        super(fm);
        this.list = list;
    }

    @Override
    public Fragment getItem(int i)
    {
        return ImageFragment.newInstance(list.get(i));
    }

    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object)
    {
        super.destroyItem(container, position, object);
    }
}
