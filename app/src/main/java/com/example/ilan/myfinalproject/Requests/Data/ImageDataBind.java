package com.example.ilan.myfinalproject.Requests.Data;

import android.widget.ImageView;

import com.example.ilan.myfinalproject.Requests.Listener.OnLoadListener;
import com.example.ilan.myfinalproject.Requests.Listener.OnUnbindListener;



public class ImageDataBind extends ImageData
{
private OnUnbindListener bindListener;


    public ImageDataBind(String url)
    {
    super(url);
    }


    public void bind(ImageView imageView, OnLoadListener loadListener, OnUnbindListener bindListener)
    {
    unbind();

    this.bindListener=bindListener;

    setOnLoadListener(loadListener);
    setImageView(imageView);
    }


     public void unbind()
     {

     if(bindListener!=null)
         {
         bindListener.onUnbind();
         bindListener=null;
         }
     }
}
