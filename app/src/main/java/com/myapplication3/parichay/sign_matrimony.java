package com.myapplication3.parichay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.myapplication3.parichay.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class sign_matrimony extends AppCompatActivity {

    private ImageView img11;
    TextView first,sec,third,con1,place,dist,state,birth,qualify,work,income,hei,wei;
    RadioButton male,female,unmar,mar,dev,wid;
    Button mat_set;

    public ProgressDialog dialog;
    String uid;
    private Uri mainImgURI1 = null;
    private boolean ischanged = false;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private Bitmap compressedImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_matrimony);

        img11 = findViewById(R.id.image1);
        first = findViewById(R.id.firstName);
        sec = findViewById(R.id.secName);
        third = findViewById(R.id.thirdName);
        con1 = findViewById(R.id.con);
        place = findViewById(R.id.place);
        dist = findViewById(R.id.dist);
        state = findViewById(R.id.state);
        birth = findViewById(R.id.age);
        hei = findViewById(R.id.height);
        wei = findViewById(R.id.weight);
        qualify = findViewById(R.id.qualify);
        work = findViewById(R.id.work);
        income = findViewById(R.id.income);
        unmar = findViewById(R.id.unmarried);
        mar = findViewById(R.id.married);
        wid = findViewById(R.id.widowed);
        dev = findViewById(R.id.devorced);
        mat_set = findViewById(R.id.createProfile);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Matrimony Profile");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#C50000")));
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#C50000"));


        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        dialog = new ProgressDialog(sign_matrimony.this);
        this.dialog.setMessage("Please wait");
        this.dialog.show();


        //set data if exist in fire base
        FirebaseFirestore.getInstance().collection("matrimonyProfiles").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        String img1 = task.getResult().getString("img1");

                        String f = task.getResult().getString("first");
                        String s = task.getResult().getString("sec");
                        String t = task.getResult().getString("third");
                        String con = task.getResult().getString("contact");
                        String p = task.getResult().getString("place");
                        String d = task.getResult().getString("dist");
                        String st  = task.getResult().getString("state");
                        String br  = task.getResult().getString("age");
                        String he  = task.getResult().getString("height");
                        String we  = task.getResult().getString("weight");
                        String qu  = task.getResult().getString("qualify");
                        String w  = task.getResult().getString("work");
                        String in  = task.getResult().getString("income");
                        String ma  = task.getResult().getString("marital");
                        String ge = task.getResult().getString("gender");;

                        first.setText(f);
                        sec.setText(s);
                        third.setText(t);
                        con1.setText(con);
                        place.setText(p);
                        dist.setText(d);
                        state.setText(st);
                        birth.setText(br);
                        hei.setText(he);
                        wei.setText(we);
                        qualify.setText(qu);
                        work.setText(w);
                        income.setText(in);

                        if (ge == "Male")
                            male.setChecked(true);
                        else
                            female.setChecked(true);

                        switch(ma){
                            case "Unmarried":
                                unmar.setChecked(true);
                                break;
                            case "Married":
                                mar.setChecked(true);
                                break;
                            case "Widowed":
                                wid.setChecked(true);
                                break;
                            case "Divorced":
                                dev.setChecked(true);
                                break;
                        }

                        mainImgURI1 = Uri.parse(img1);

                        RequestOptions temp = new RequestOptions();
                        temp.placeholder(R.drawable.ic_account_circle_black_24dp);

                        Glide.with(sign_matrimony.this).setDefaultRequestOptions(temp).load(img1).into(img11);

                    } else {
                        Toast.makeText(sign_matrimony.this, "Profile Data not Exist", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(sign_matrimony.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });


        img11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setMinCropWindowSize(700,700)
                                .start(sign_matrimony.this);
            }
        });




        mat_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new ProgressDialog(sign_matrimony.this);
                dialog.setMessage("Please wait");
                dialog.show();

                final String fi = first.getText().toString();
                final String se = sec.getText().toString();
                final String th = third.getText().toString();
                final String con = con1.getText().toString();
                final String pl = place.getText().toString();
                final String di = dist.getText().toString();
                final String sta = state.getText().toString();
                final String bi = birth.getText().toString();
                final String he = hei.getText().toString();
                final String we = wei.getText().toString();
                final String qua = qualify.getText().toString();
                final String wo = work.getText().toString();
                final String inc = income.getText().toString();
                final String gen,mar_st;

                if (male.isChecked())
                    gen = "Male";
                else if (female.isChecked())
                    gen= "Female";
                else
                    gen = "";

                if (unmar.isChecked())
                    mar_st = "Unmarried";
                else if (mar.isChecked())
                    mar_st = "Married";
                else if (wid.isChecked())
                    mar_st = "Widowed";
                else
                    mar_st = "Divorced";


                if(ischanged){

                    if (mainImgURI1 == null  || fi.isEmpty() || se.isEmpty() || th.isEmpty() || con.isEmpty() || pl.isEmpty() ||di.isEmpty() ||sta.isEmpty() ||bi.isEmpty()||he.isEmpty() ||we.isEmpty()||qua.isEmpty() ||wo.isEmpty() ||inc.isEmpty()){
                        Toast.makeText(sign_matrimony.this, "ALL FIELDS SHOULD BE FILLED", Toast.LENGTH_LONG).show();
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        return;

                    }

                    File imgpath = new File(mainImgURI1.getPath());
                    try {
                        compressedImageBitmap = new Compressor(sign_matrimony.this)
                                .setQuality(75)
                                .setMaxHeight(400)
                                .setMaxWidth(400)
                                .compressToBitmap(imgpath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    compressedImageBitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                    byte[] data = baos.toByteArray();

                    final StorageReference image_path1 = mStorageRef.child("matrimony_profiles").child(uid + ".jpg");

                    image_path1.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            image_path1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri dnuri = uri;

                                    storedata(dnuri,fi,se,th,con,pl,di,sta,bi,he,we,qua,wo,inc,gen,mar_st);
                                }
                            });
                        }
                    });

                } else {
                    storedata(null,fi,se,th,con,pl,di,sta,bi,he,we,qua,wo,inc,gen,mar_st);
                }
            }
        });



    }

    private void storedata(Uri dnuri ,String fi,String se,String th,String con,String pl,String di,String sta,String bi,String he,String we,String qua,String wo,String inc,String gen,String mar_st) {

        if (mainImgURI1 == null || fi.isEmpty() || se.isEmpty() || th.isEmpty() ||con.isEmpty() || pl.isEmpty() ||di.isEmpty() ||sta.isEmpty() ||bi.isEmpty()||he.isEmpty() ||we.isEmpty() ||qua.isEmpty() ||wo.isEmpty() ||inc.isEmpty()){
            Toast.makeText(sign_matrimony.this, "ALL FIELDS SHOULD BE FILLED", Toast.LENGTH_LONG).show();
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            return;

        }

        if(dnuri == null){
            dnuri = mainImgURI1;
        }

        Map<String,String> user = new HashMap<>();
        user.put("img1",dnuri.toString());
        user.put("first",fi);
        user.put("sec",se);
        user.put("third",th);
        user.put("contact",con);
        user.put("place",pl);
        user.put("dist",di);
        user.put("state",sta);
        user.put("age",bi);
        user.put("height",he);
        user.put("weight",we);
        user.put("qualify",qua);
        user.put("work",wo);
        user.put("income",inc);
        user.put("gender",gen);
        user.put("marital",mar_st);

        FirebaseFirestore.getInstance().collection("matrimonyProfiles").document(uid).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    Toast.makeText(sign_matrimony.this, "Profile is updated", Toast.LENGTH_SHORT).show();

                    Map<String,Object> user1 = new HashMap<>();
                    user1.put("matrimony","yes");
                    FirebaseFirestore.getInstance().collection("users").document(uid).update(user1);

                    Intent in =  new Intent(sign_matrimony.this, MainActivity.class);
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    startActivity(in);
                    finish();

                } else {
                    Toast.makeText(sign_matrimony.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImgURI1 = result.getUri();
                img11.setImageURI(mainImgURI1);
                ischanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}
