package com.myapplication3.parichay;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.myapplication3.parichay.ads.new_ads;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
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

public class home_matrimony extends Fragment {

    FloatingActionButton admin;
    private String userId;
    private String imgf;
    RecyclerView recyclerViewmat;

    private adapter_matrimony_recycler adapter;
    private SwipeRefreshLayout refresh;

    SearchView searchView;
    MenuItem mat_pro;

    FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    private List<matrimony_user> matrimonyList;
    private List<new_ads> adsList1;
    private CollectionReference query;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.home_matrimony,container,false);

        admin = view.findViewById(R.id.admin);
        recyclerViewmat = view.findViewById(R.id.recyclermat);
        refresh = view.findViewById(R.id.refreshmat);

        firestore = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseMessaging.getInstance().subscribeToTopic("news");

        matrimonyList = new ArrayList<>();
        adsList1 = new ArrayList<>();


        adapter = new adapter_matrimony_recycler(matrimonyList,adsList1);
        recyclerViewmat.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewmat.setAdapter(adapter);

        callmat();

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        searchView.clearFocus();
                        searchView.setIconified(true);
                            callmat();
                        refresh.setRefreshing(false);
                    }
                },3000);

            }
        });

        return view;
    }


    private void callmat() {
        matrimonyList.clear();
        adsList1.clear();

        firestore.collection("matrimonyProfiles").orderBy("first", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                for (DocumentChange doc:queryDocumentSnapshots.getDocumentChanges()){
                    if(doc.getType() == DocumentChange.Type.ADDED){


                        matrimony_user newspost = doc.getDocument().toObject(matrimony_user.class);

                       if (!userId.equals(doc.getDocument().getId()))
                            matrimonyList.add(newspost);
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

                for (int i = 0;i < matrimonyList.size();i += 6){
                    matrimonyList.add(i,matrimonyList.get(i));
                }

                for (int i = 0;i < matrimonyList.size();i++){
                    Random rand = new Random();
                    int rand_int = rand.nextInt(listads.size());
                    new_ads newadsitem2 = listads.get(rand_int);
                    adsList1.add(newadsitem2);
                    adapter.notifyDataSetChanged();
                }


            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("Please wait");
        dialog.show();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance().collection("users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String mat1 = task.getResult().getString("matrimony");
                String user = task.getResult().getString("type");

                String yes = "yes";
                if (mat1.equals(yes) || user.equals("admin")){
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }

                }
                else{
                    Intent in = new Intent(getContext(),sign_matrimony.class);
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    startActivity(in);
                }
            }
        });
    }

    private void finish() {
        Intent i = new Intent(getActivity(),MainActivity.class);
        finish();
    }

    public home_matrimony() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main2,menu);

        MenuItem search_item = menu.findItem(R.id.Search);
        mat_pro = menu.findItem(R.id.Search);


        searchView = (SearchView) search_item.getActionView();
        searchView.setFocusable(false);
        searchView.setQueryHint("Search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {


            @Override
            public boolean onQueryTextSubmit(String keyword) {


                Toast.makeText(getContext(), "key = " + keyword.trim(), Toast.LENGTH_SHORT).show();

                if (keyword != ""){
                    String[] name = keyword.split("\\s+");

                    switch (name.length){
                        case 1:
                            search(FirebaseFirestore.getInstance().collection("matrimonyProfiles").whereEqualTo("first",name[0]));
                            break;
                        case 2:
                            query.whereEqualTo("first",name[0]).whereEqualTo("third",name[1]);
                            search(query);
                            break;
                        case 3:
                            Toast.makeText(getContext(), "Search only First and Last name.", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.mat_pro:
                startActivity(new Intent(getContext(),sign_matrimony.class));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    private void search(Query query) {
        matrimonyList.clear();
        adsList1.clear();

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    System.err.println("Listen failed:" + e);
                    return;
                }
                for (DocumentChange doc:queryDocumentSnapshots.getDocumentChanges()){
                    if(doc.getType() == DocumentChange.Type.ADDED){


                        matrimony_user newspost = doc.getDocument().toObject(matrimony_user.class);

                        /*if (!imgf.equals(doc.getDocument().getId()))*/
                            matrimonyList.add(newspost);
                    }
                }

                if (matrimonyList.size() == 0)
                    Toast.makeText(getContext(), "USER NOT FOUND", Toast.LENGTH_LONG).show();

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

                for (int i = 0;i < matrimonyList.size();i += 6){
                    matrimonyList.add(i,matrimonyList.get(i));
                }

                for (int i = 0;i < matrimonyList.size();i++){
                    Random rand = new Random();
                    int rand_int = rand.nextInt(listads.size());
                    new_ads newadsitem2 = listads.get(rand_int);
                    adsList1.add(newadsitem2);
                    adapter.notifyDataSetChanged();
                }


            }
        });
    }
}
