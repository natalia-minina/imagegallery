package com.example.imagegallery;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

public class GalleryActivity extends Activity implements NextImageListener {

    private AppImageViewTouch gallery;

    private ImageView loadingGalleryIndicator;

    private int xImageIndex;

    private int yImageIndex;

    private Animation rotation;

    private static final float MAX_ZOOM = 4.0f;

    private volatile String loadingImageUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        initImageLoader();

        gallery = (AppImageViewTouch) findViewById(R.id.gallery);
        loadingGalleryIndicator = (ImageView) findViewById(R.id.loadingGalleryIndicator);

        rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_rotation);

        showImage();
    }

    @Override
    public void nextImage(int xDirection, int yDirection) {
        int xImageIndexPrev = xImageIndex;
        int yImageIndexPrev = yImageIndex;
        xImageIndex += xDirection;

        if (xImageIndex >= Constants.Urls.imagesArray.length) {
            xImageIndex = 0;
        } else if (xImageIndex < 0) {
            xImageIndex = Constants.Urls.imagesArray.length - 1;
        }
        if (xImageIndex != xImageIndexPrev) {
            yImageIndex = 0;
            showImage();
        } else {
            yImageIndex += yDirection;
            if (yImageIndex >= Constants.Urls.imagesArray[xImageIndex].length) {
                yImageIndex = 0;
            } else if (yImageIndex < 0) {
                yImageIndex = Constants.Urls.imagesArray[xImageIndex].length - 1;
            }
            if (yImageIndex != yImageIndexPrev) {
                showImage();
            }
        }
    }

    private void showImage() {
        ImageLoader.getInstance().loadImage(Constants.Urls.imagesArray[xImageIndex][yImageIndex], new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                loadingImageUri = imageUri;
                gallery.setVisibility(View.GONE);
                loadingGalleryIndicator.setVisibility(View.VISIBLE);
                loadingGalleryIndicator.startAnimation(rotation);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if (loadingImageUri == imageUri) {
                    gallery.setVisibility(View.VISIBLE);
                    gallery.setImageBitmap(null);
                    loadingGalleryIndicator.clearAnimation();
                    loadingGalleryIndicator.setVisibility(View.GONE);
                    Toast.makeText(GalleryActivity.this, R.string.error_has_occurred, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (loadingImageUri == imageUri) {
                    gallery.setVisibility(View.VISIBLE);
                    loadingGalleryIndicator.clearAnimation();
                    loadingGalleryIndicator.setVisibility(View.GONE);
                    gallery.setImageBitmap(loadedImage, true, null, MAX_ZOOM);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
            }
        });

    }

    private void initImageLoader() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration imageLoaderConfiguration = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(options)
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .build();
        ImageLoader.getInstance().init(imageLoaderConfiguration);
    }


}