package com.example.googlemap;

import android.graphics.Bitmap;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LoadImageTask {
    private final ExecutorService backgroundExecutor = Executors.newSingleThreadExecutor();

    public Future<Bitmap> loadImage(String iconUrl){
        return backgroundExecutor.submit(new Callable<Bitmap>() {
            @Override
            public Bitmap call() throws Exception {
                try {
                    return Picasso.get().load(iconUrl).get();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });
    }
}