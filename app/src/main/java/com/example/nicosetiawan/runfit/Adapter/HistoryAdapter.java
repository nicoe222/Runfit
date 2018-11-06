package com.example.nicosetiawan.runfit.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nicosetiawan.runfit.Database.Runit;
import com.example.nicosetiawan.runfit.R;

import java.util.List;

/**
 * Created by rusmana on 10/09/2018.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder>  {
    private Context mContext;
    private HistoryAdapterListener listener;
    private List<Runit> runitArrayList;
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /*View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_history, parent, false);
        return new MyViewHolder(itemView);*/

        View view = LayoutInflater.from(mContext).inflate(R.layout.list_row_history,parent,false);
        MyViewHolder myHoder = new MyViewHolder(view);


        return myHoder;


    }


    public HistoryAdapter(Context mContext, List<Runit> runits) {
        this.mContext = mContext;
        this.runitArrayList = runits;
    }

    public HistoryAdapter(Context mContext, List<Runit> runits, HistoryAdapterListener listener) {
        this.mContext = mContext;
        this.runitArrayList = runits;
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.MyViewHolder holder, int position) {

        Runit runit = runitArrayList.get(position);
        Log.d("Res Title", "" + runit.getRunTitle() );
        // displaying text view data
        String title = "<u>" + runit.getRunTitle() + "</u>";
        holder.txtTitle.setText( Html.fromHtml(title) );

        String subtitle = "<b>Date :</b> " + runit.getRunDate() + ", <b>Time :</b> " + runit.getRunStartTime()  + " - " + runit.getRunStopTime() ;
        //subtitle+= "<b>Stop Time</b> <i> "+ runit.getRunStopTime()  + "</i>";
        holder.txtSubtitle.setText( Html.fromHtml(subtitle) );
        applyClickEvents(holder,position);
    }

    @Override
    public int getItemCount() {
        return runitArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtTitle, txtSubtitle;
        public LinearLayout layout;

        public MyViewHolder(View itemView) {
            super(itemView);
            txtTitle  = (TextView) itemView.findViewById(R.id.txtHead);
            txtSubtitle = (TextView) itemView.findViewById(R.id.txtSecondary);
            layout      = (LinearLayout) itemView.findViewById(R.id.rowContainer);
        }
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onRowClicked(position);
            }
        });


        holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onRowLongClicked(position);
                return false;
            }
        });

    }
    public interface HistoryAdapterListener {
        void onRowClicked(int position);
        void onRowLongClicked(int position);

    }
}
