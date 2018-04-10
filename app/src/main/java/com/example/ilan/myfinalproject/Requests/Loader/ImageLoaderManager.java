package com.example.ilan.myfinalproject.Requests.Loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

import com.example.ilan.myfinalproject.Requests.Data.ImageData;
import com.example.ilan.myfinalproject.Requests.Req.ImageRequest;
import com.example.ilan.myfinalproject.Extra.Http;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;



public class ImageLoaderManager
{
private static ArrayList<ImageRequest> requests;
private static boolean isRunning;
private static Handler handler;
private static Runnable action;


    private static void init()
    {
    requests=new ArrayList<>();
    isRunning=false;
    handler=new Handler();

    action=new Runnable()
        {
            @Override
            public void run()
            {
            onBackground();
            }
        };
    }


    public static ImageRequest loadImage(ImageData image, int width, int height)
    {
    ImageRequest request=new ImageRequest(image, width, height);

    addRequest(request);

    return request;
    }


    static synchronized void addRequest(ImageRequest request)
    {

    if(requests==null)
        {
        init();
        }

    if(!request.isRequested())
        {
        request.onRequested();
        requests.add(request);

        if(!isRunning)
            {
            isRunning=true;
            new Thread(action, "load images task").start();
            }
        }
    }


    private static void onBackground()
    {
    ImageRequest request;
    Bitmap bitmap;
    String url;

    while(!requests.isEmpty())
        {
        request=requests.remove(0);

        url=request.getUrl();
        bitmap=null;

        if(url!=null)
            {
            if(url.startsWith("http"))
                {
                bitmap=getBitmapFromWeb(url, request.getWidth(), request.getHeight());
                }
            }

        postResult(request, bitmap);

        }

    isRunning=false;
    }


    private static void postResult(final ImageRequest request, final Bitmap bitmap)
    {
    //Log.d("ImageLoaderManager","post image");
    handler.post(new Runnable()
        {

            @Override
            public void run()
            {
            request.onLoad(bitmap);
            }
        });
    }


    private static Bitmap getBitmapFromWeb(String url, int width, int height)
    {
    BitmapFactory.Options bitmapOptions;
    InputStream inputStream = null;
    Bitmap bitmap = null;

    try {
        inputStream= Http.getInputStream(url);

        bitmapOptions=null;

        if(width > 0 || height > 0)
            {
            bitmapOptions=getBitmapOptions(inputStream, width ,height);

            inputStream= Http.getInputStream(url);
            }

        bitmap= BitmapFactory.decodeStream(inputStream, null ,bitmapOptions);

        if(bitmap!=null && width > 0 && height > 0)
            {
            bitmap=Bitmap.createScaledBitmap(bitmap, width, height, true);
            }

        } catch (Exception e)
            {
            Log.e("ImageLoaderManager","Failed to load image - "+e.getMessage());
            }

    finally
        {
        if(inputStream!=null)
            {
            try {
                inputStream.close();
                } catch(Exception e) {}
            }
        }

    return bitmap;
    }


    private static BitmapFactory.Options getBitmapOptions(InputStream inputStream, int width, int height) throws IOException
    {
    BitmapFactory.Options options= createBitmapOptions();

    BitmapFactory.decodeStream(inputStream, null ,options);

    scaleBitmap(options, width, height);

    inputStream.close();

    return options;
    }


    private static BitmapFactory.Options createBitmapOptions()
    {
    BitmapFactory.Options options= new BitmapFactory.Options();

    options.inJustDecodeBounds = true;

    return options;
    }


    private static void scaleBitmap(BitmapFactory.Options options, int width, int height)
    {
    options.inJustDecodeBounds = false;

    width= (width > 0)? options.outWidth / width: options.outWidth;

    height=(height > 0)? options.outHeight / height: options.outHeight;

    options.inSampleSize = Math.min(width, height);
    }

}
