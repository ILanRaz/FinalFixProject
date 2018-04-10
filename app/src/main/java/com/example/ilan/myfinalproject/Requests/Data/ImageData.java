package com.example.ilan.myfinalproject.Requests.Data;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.example.ilan.myfinalproject.Requests.Loader.ImageLoaderManager;
import com.example.ilan.myfinalproject.Requests.Req.ImageRequest;
import com.example.ilan.myfinalproject.Requests.Listener.OnLoadListener;



public class ImageData
{
private OnLoadListener loadListener;
private ImageRequest imageRequest;
private ImageView imageView;
private boolean isLoaded;
private Bitmap image;
private String url;


    public ImageData(String url)
    {
    this.url=url;

    image=null;
    imageRequest=null;

    imageView=null;

    isLoaded=false;
    }


    public String getUrl()
    {
    return url;
    }


    public void load(int width, int height)
    {
    if(imageRequest==null)
        {
        imageRequest=ImageLoaderManager.loadImage(this, width, height);
        }
    }


    public void setOnLoadListener(OnLoadListener loadListener)
    {
    this.loadListener=loadListener;
    }


    public Bitmap getImage()
    {
    return image;
    }


    public void setImageView(ImageView imageView)
    {
    this.imageView=imageView;

    if(imageView !=null)
        {
        if(image!=null)
            {
            if(!image.isRecycled())
                {
                imageView.setImageBitmap(image);
                }else
                    {
                    imageView.setImageBitmap(null);
                    Log.e("ImageData","Error: can't set ImageView when the image is removed");
                    }
            }else
                {
                imageView.setImageBitmap(null);
                }
        }

    }


    public boolean isLoaded()
    {
    return isLoaded;
    }


    public void remove()
    {

    if(imageView != null)
        {
        imageView.setImageBitmap(null);
        }

    if(image != null)
        {
        image.recycle();

        //image=null;
        }
    }


    public void onPreLoad()
    {

    if(imageView != null)
        {
        imageView.setImageBitmap(null);
        }

    if(loadListener!=null)
        {
        loadListener.onPreLoad();
        }
    }


    public void onLoad(Bitmap bitmap)
    {
    image=bitmap;
    isLoaded=true;

    setImageView(imageView);

    if(loadListener!=null)
        {
        loadListener.onLoad();
        }
    }

}
