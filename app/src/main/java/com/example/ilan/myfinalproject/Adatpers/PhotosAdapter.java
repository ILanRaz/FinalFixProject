package com.example.ilan.myfinalproject.Adatpers;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.ilan.myfinalproject.R;
import com.example.ilan.myfinalproject.Requests.Data.ImageData;
import com.example.ilan.myfinalproject.Requests.Listener.OnLoadListener;



// This adapter loads the photos
public class PhotosAdapter extends PagerAdapter {
    private LayoutInflater inflater;
    private ImageData[] images;
    private View[] items;


    public PhotosAdapter(Context context) {
        inflater = LayoutInflater.from(context);

        images = null;
        items = null;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final ImageView imageView;
        final ProgressBar loading;

        if (items[position] == null) {
            items[position] = inflater.inflate(R.layout.image, null);

            imageView = (ImageView) items[position].findViewById(R.id.iv_photo);
            loading = (ProgressBar) items[position].findViewById(R.id.pb_loading);

            images[position].setOnLoadListener(new OnLoadListener() {
                @Override
                public void onPreLoad() {
                    loading.setVisibility(View.VISIBLE);
                }


                @Override
                public void onLoad() {
                    loading.setVisibility(View.GONE);
                }
            });

            images[position].setImageView(imageView);
            images[position].load(container.getWidth(), container.getHeight());
        }


        container.addView(items[position]);

        return items[position];
    }


    public void setImages(String[] photoUrls) {
        if (photoUrls != null) {
            images = new ImageData[photoUrls.length];
            items = new View[photoUrls.length];

            for (int i = 0; i < photoUrls.length; i++) {
                images[i] = new ImageData(photoUrls[i]);
            }
        }

        notifyDataSetChanged();
    }


    public void clearImages() {

        for (int i = 0; i < getCount(); i++) {
            images[i].remove();
        }

        images = null;

        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        int count = 0;

        if (images != null) {
            count = images.length;
        }

        return count;
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
