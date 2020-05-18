package com.myapplication3.parichay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.myapplication3.parichay.admin_panel.admin_pannel;
import com.myapplication3.parichay.ads.new_ads;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class home_news extends Fragment {

    FloatingActionButton admin;
    private String userId;
    private FirebaseFirestore firestore;
    RecyclerView recyclerView;

    List<news_post> newsList;
    List<new_ads> adsList1;

    private adapter_new_recycler adapter;
    private SwipeRefreshLayout refresh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.home_news,container,false);

        admin = view.findViewById(R.id.admin);
        recyclerView = view.findViewById(R.id.recycler);
        refresh = view.findViewById(R.id.refresh);

        firestore = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseMessaging.getInstance().subscribeToTopic("news");

        newsList = new ArrayList<>();
        adsList1 = new ArrayList<>();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                    callnews();
            }
        },100);


        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getActivity(), admin_pannel.class);
                startActivity(in);
            }
        });

        adapter = new adapter_new_recycler(newsList,adsList1);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (onoffint()) {
                            callnews();
                        }
                        refresh.setRefreshing(false);
                    }
                },3000);

            }
        });

        return view;
    }

    private void callnews() {

            newsList.clear();
            adsList1.clear();

            firestore.collection("news").orderBy("time", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                    for (DocumentChange doc:queryDocumentSnapshots.getDocumentChanges()){
                        if(doc.getType() == DocumentChange.Type.ADDED){

                            news_post newspost = doc.getDocument().toObject(news_post.class);
                            newsList.add(newspost);
                        }
                    }

                }
            });

        firestore.collection("ads").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                List<new_ads> listads = new ArrayList<>();

                for (DocumentChange doc:queryDocumentSnapshots.getDocumentChanges()){
                    if(doc.getType() == DocumentChange.Type.ADDED){

                        new_ads newadsitem = doc.getDocument().toObject(new_ads.class);
                        listads.add(newadsitem);
                    }
                }

                for (int i = 0;i < newsList.size();i += 6){
                    newsList.add(i,newsList.get(i));
                }

                for (int i = 0;i < newsList.size();i++){
                    Random rand = new Random();
                    if (listads.size() != 0){
                        int rand_int = rand.nextInt(listads.size());
                        new_ads newadsitem2 = listads.get(rand_int);
                        adsList1.add(newadsitem2);
                        adapter.notifyDataSetChanged();

                    }
                }


            }
        });
    }

    private boolean onoffint(){

        ConnectivityManager connectivityManager = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }
        else {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        refresh.setRefreshing(true);

        onoffint();

        firestore.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.getResult().exists()){
                    String s = task.getResult().getString("type");

                    if (s != null) {
                        if (s.equals("admin")) {
                            admin.show();
                        }
                    }
                    refresh.setRefreshing(false);
                }
            }
        });
    }
}
