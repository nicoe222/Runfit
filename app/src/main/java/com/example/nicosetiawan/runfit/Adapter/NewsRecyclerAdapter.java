package com.example.nicosetiawan.runfit.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.nicosetiawan.runfit.Models.News;
import com.example.nicosetiawan.runfit.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class NewsRecyclerAdapter extends RecyclerView.Adapter<NewsRecyclerAdapter.ViewHolder> {

    public List<News> news_list;
    public Context mContext;

    private static final String TAG = "NewsRecyclerAdapter";

    public NewsRecyclerAdapter(List<News> news_list){
            this.news_list = news_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout_main, parent, false);
        mContext = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String content_data = news_list.get(position).getContent();
        if (content_data.length() < 15 ){
            holder.setContentView(content_data);
        } else {
            String content_sub = content_data.substring(0,170).toString();
            holder.setContentView(content_sub);
        }

        String title_data = news_list.get(position).getTitle();
        holder.setTitleView(title_data);

        String image_url = news_list.get(position).getImage_url();
        holder.setImageView(image_url);

        long milliseconds = news_list.get(position).getTimestamp().getTime();
        String dateString = new SimpleDateFormat("dd/MM/yyy").format(new Date(milliseconds)).toString();
        holder.settTimeView(dateString);


    }

    @Override
    public int getItemCount() {
        return news_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // DECALRE VARIABLE
        private View mView;
        private TextView contentView,titleView,timeView;
        private ImageView image_url;
        private ProgressBar mProgressBar;


        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setContentView(String content){
            contentView = mView.findViewById(R.id.content);
            contentView.setText(content);
        }

        public void setTitleView(String title){
            titleView = mView.findViewById(R.id.cardTitle);
            titleView.setText(title);
        }

        public void setImageView(String download_url){
            image_url = mView.findViewById(R.id.cardImage);
            mProgressBar = mView.findViewById(R.id.cardProgressDialog);
            //create the imageloader object
            ImageLoader imageLoader = ImageLoader.getInstance();

            int defaultImage = mContext.getResources().getIdentifier("@drawable/image_failed",null,mContext.getPackageName());

            //create display options
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisc(true).resetViewBeforeLoading(true)
                    .showImageForEmptyUri(defaultImage)
                    .showImageOnFail(defaultImage)
                    .showImageOnLoading(defaultImage).build();

            imageLoader.displayImage(download_url, image_url, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                        mProgressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    mProgressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    mProgressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
        }

        public void settTimeView(String time){
            timeView = mView.findViewById(R.id.datepost);
            timeView.setText(time);
        }
    }

}
