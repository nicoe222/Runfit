package com.example.nicosetiawan.runfit.News;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nicosetiawan.runfit.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class NewsDetailActivity extends AppCompatActivity {

    private static final String TAG = "NewsDetailActivity";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        // ArrowBack
        ImageView backArrow = findViewById(R.id.BackMenu);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Close");
                finish();
            }
        });

        getIncomingIntent();
    }


    private void getIncomingIntent(){
        Log.d(TAG, "getIncomingIntent: Check Incoming Intent");

        if (getIntent().hasExtra("content") && getIntent().hasExtra("title") && getIntent().hasExtra("image_url")
                && getIntent().hasExtra("date")){
            Log.d(TAG, "getIncomingIntent: foound Intent Extra");

            String imageURL = getIntent().getStringExtra("image_url");
            String content = getIntent().getStringExtra("content");
            String title = getIntent().getStringExtra("title");
            String date = getIntent().getStringExtra("date");

            setNewsDetail(imageURL,content,title,date);
        }
    }

    private void setNewsDetail(String imageURL, String content, String title, String date){


        TextView mcontent = findViewById(R.id.contentdetail);
        mcontent.setText(content);

        TextView mtitle = findViewById(R.id.titledetail);
        mtitle.setText(title);

        TextView mdate = findViewById(R.id.datepost);
        mdate.setText(date);

        ImageView image_url = findViewById(R.id.image_news_detail);
        ImageLoader imageLoader = ImageLoader.getInstance();

        int defaultImage = this.getResources().getIdentifier("@drawable/image_failed",null,this.getPackageName());

        //create display options
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(defaultImage)
                .showImageOnFail(defaultImage)
                .showImageOnLoading(defaultImage).build();

        imageLoader.displayImage(imageURL, image_url, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
               // mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                //mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
               // mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }


}
