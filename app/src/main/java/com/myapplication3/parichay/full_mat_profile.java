package com.myapplication3.parichay;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class full_mat_profile extends AppCompatActivity {

    TextView name,age,contact,ad,he,we,stat,qual,work,sal;
    ImageView img;

    String mimg,mname,mage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_mat_profile);

        name = findViewById(R.id.name);
        img = findViewById(R.id.matimg);
        age = findViewById(R.id.age);
        contact = findViewById(R.id.contact);
        ad = findViewById(R.id.ad);
        he = findViewById(R.id.height);
        we = findViewById(R.id.weight);
        stat = findViewById(R.id.stat);
        qual = findViewById(R.id.qual);
        work = findViewById(R.id.work);
        sal = findViewById(R.id.salary);

        mimg = getIntent().getStringExtra("image");
        mname = getIntent().getStringExtra("name");
        mage = getIntent().getStringExtra("age");

        name.setText(mname);
        age.setText(mage + "Yrs");

        Glide.with(this)
                .load(mimg)
                .into(img);

        getSupportActionBar().setTitle(mname);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#C50000")));
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#C50000"));


        FirebaseFirestore.getInstance().collection("matrimonyProfiles").whereEqualTo("img1",mimg).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    System.err.println("Listen failed:" + e);
                    return;
                }
                for (DocumentChange doc:queryDocumentSnapshots.getDocumentChanges()){
                    if(doc.getType() == DocumentChange.Type.ADDED){


                        matrimony_user newspost = doc.getDocument().toObject(matrimony_user.class);

                        contact.setText(newspost.getContact());
                        ad.setText(newspost.getPlace()+ "," + newspost.getDist() + "," + newspost.getState());
                        he.setText(newspost.getHeight());
                        we.setText(newspost.getweight());
                        stat.setText(newspost.getMarital());
                        qual.setText(newspost.getQualify());
                        work.setText(newspost.getWork());
                        sal.setText(newspost.getIncome());

                    }
                }

            }
        });


    }
}
