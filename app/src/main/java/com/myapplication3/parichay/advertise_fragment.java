package com.myapplication3.parichay;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.myapplication3.parichay.ads.publish_ad;
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

public class advertise_fragment extends Fragment {

    FloatingActionButton admin;
    RecyclerView recyclerView;
    SwipeRefreshLayout refresh;
    TextView noad;

    FirebaseFirestore firestore;
    String userId;
    private List<myad> myadList;
    private adapter_myad_recycler adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_advertise,container,false);

        admin = view.findViewById(R.id.ad_min);
        recyclerView = view.findViewById(R.id.recyclerad);
        refresh = view.findViewById(R.id.refreshad);
        noad = view.findViewById(R.id.noad);

        firestore = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseMessaging.getInstance().subscribeToTopic("news");

        myadList = new ArrayList<>();


        refresh.setRefreshing(true);
                FirebaseFirestore.getInstance().collection("advertises").document(userId).collection("myads")
                        .whereEqualTo("status","pending").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                FirebaseFirestore.getInstance().collection("advertises").document(userId).collection("myads")
                                        .document(document.getId()).delete();
                            }

                            refresh.setRefreshing(false);
                            callad();
                        } else {
                            refresh.setRefreshing(false);
                            callad();
                        }
                    }
                });

        adapter = new adapter_myad_recycler(myadList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);


        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getActivity(), publish_ad.class);
                startActivity(in);
            }
        });


        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                            callad();
                        refresh.setRefreshing(false);
                    }
                },3000);

            }
        });

        return view;
    }

    private void callad() {

        myadList.clear();

        firestore.collection("advertises").document(userId).collection("myads").orderBy("expiry", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                for (DocumentChange doc:queryDocumentSnapshots.getDocumentChanges()){
                    if(doc.getType() == DocumentChange.Type.ADDED){

                        myad newspost = doc.getDocument().toObject(myad.class);
                        myadList.add(newspost);
                        adapter.notifyDataSetChanged();
                    }
                }

                if (myadList.size() == 0){
                    noad.setVisibility(View.VISIBLE);
                }

            }
        });

        /*firestore.collection("ads").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                    int rand_int = rand.nextInt(listads.size());
                    new_ads newadsitem2 = listads.get(rand_int);
                    adsList1.add(newadsitem2);
                    adapter.notifyDataSetChanged();
                }


            }
        });*/
    }
}
