package com.example.puneet2singla.chatapp;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.net.URL;

public class GlideImageLoader {
    private RequestManager requestManager;
    public GlideImageLoader(Context context){
        this.requestManager= Glide.with(context);
    }
    public void load(ImageView imageView, String url){
        requestManager.load(url).into(imageView);
    }
}
