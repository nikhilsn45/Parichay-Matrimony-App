package com.myapplication3.parichay.subscription;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.myapplication3.parichay.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class subscribe extends AppCompatActivity {

    CircleImageView subimg;
    TextView subname,place,city,state,subexp;
    Button get_sub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Subscription");

        subimg = findViewById(R.id.subprofilePic);
        subname = findViewById(R.id.subname);
        place = findViewById(R.id.subplace);
        city = findViewById(R.id.subcity);
        state = findViewById(R.id.substate);
        subexp = findViewById(R.id.subexp);
        get_sub = findViewById(R.id.get_subscribe);


        String img = getIntent().getStringExtra("image");
        subname.setText(getIntent().getStringExtra("name"));
        place.setText(getIntent().getStringExtra("place") + ",");
        city.setText(getIntent().getStringExtra("city") + ",");
        state.setText(getIntent().getStringExtra("state") + ".");
        subexp.setText(getIntent().getStringExtra("expiry"));

        RequestOptions temp = new RequestOptions();
        temp.placeholder(R.drawable.bottom_shadow);

        Glide.with(subscribe.this).setDefaultRequestOptions(temp).load(img).into(subimg);

        get_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(subscribe.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(subscribe.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
                }

                Intent in = new Intent(subscribe.this,PaymentActivity.class);
                in.putExtra("amount","50");
                startActivity(in);
            }
        });
    }

    public boolean onNavigateUp(){
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
