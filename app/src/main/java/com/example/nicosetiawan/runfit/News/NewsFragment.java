package com.example.nicosetiawan.runfit.News;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.nicosetiawan.runfit.Adapter.NewsRecyclerAdapter;
import com.example.nicosetiawan.runfit.Models.News;
import com.example.nicosetiawan.runfit.R;
import com.example.nicosetiawan.runfit.Utils.BottomNavigationViewHelper;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends Fragment {

    private static final String TAG = "NewsFragment";
    private BottomNavigationViewEx bottomNavigationViewEx;
    private static final int ACTIVITY_NUM = 0;

    private RecyclerView news_list_views;
    private List<News> news_list;
    private NewsRecyclerAdapter newsRecyclerAdapter;

    private FirebaseFirestore firebaseFirestore;
    private DocumentSnapshot lastVisible;

    private Context mContext;

    public NewsFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_news, container, false);

        news_list = new ArrayList<>();
        bottomNavigationViewEx = view.findViewById(R.id.bottomNavViewBar);
        news_list_views = view.findViewById(R.id.news_list_views);
        mContext = getActivity();
        newsRecyclerAdapter = new NewsRecyclerAdapter(news_list);
        news_list_views.setLayoutManager(new LinearLayoutManager(getActivity()));
        news_list_views.setAdapter(newsRecyclerAdapter);
        start_news();
        SetupBottomNavigationView();
        return view;
    }

    private void SetupBottomNavigationView(){
        Log.d(TAG, "SetupBottomNavigationView: Setting Up BottomNavigationView");
        BottomNavigationViewHelper.SetupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    private void start_news(){
        firebaseFirestore = FirebaseFirestore.getInstance();
        news_list_views.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                if (reachedBottom){
                    loadMoreNews();
                }
            }
        });

        Query firstQuery = firebaseFirestore.collection("News").orderBy("timestamp", Query.Direction.DESCENDING).limit(3);

        firstQuery.addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                lastVisible = documentSnapshots.getDocuments()
                        .get(documentSnapshots.size() -1);

                for (DocumentChange doc: documentSnapshots.getDocumentChanges()){
                    if (doc.getType() == DocumentChange.Type.ADDED || doc.getType() == DocumentChange.Type.MODIFIED){

                        News news = doc.getDocument().toObject(News.class);
                        news_list.add(news);
                        newsRecyclerAdapter.notifyDataSetChanged();

                    }
                }

            }
        });
    }

    public void loadMoreNews(){
        Query nextQuery = firebaseFirestore.collection("News")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(3);

        nextQuery.addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()){
                    lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() -1);

                    for (DocumentChange doc: documentSnapshots.getDocumentChanges()){
                        if (doc.getType() == DocumentChange.Type.ADDED || doc.getType() == DocumentChange.Type.MODIFIED){

                            News news = doc.getDocument().toObject(News.class);
                            news_list.add(news);

                            newsRecyclerAdapter.notifyDataSetChanged();

                        }
                    }

                }

            }
        });

    }

}
