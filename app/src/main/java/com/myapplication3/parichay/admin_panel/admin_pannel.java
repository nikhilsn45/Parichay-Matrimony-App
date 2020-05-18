package com.myapplication3.parichay.admin_panel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.myapplication3.parichay.MainActivity;
import com.myapplication3.parichay.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class admin_pannel extends AppCompatActivity {

    EditText mob,news_title,dec;
    ImageView new_img;
    Button reg,publish;

    private Uri mainImgURI = null;

    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private FirebaseFirestore fistore;
    private Bitmap compressedImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_pannel);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Admin Pannel");

        mob = findViewById(R.id.reg_mob);
        reg = findViewById(R.id.new_register);
        new_img = findViewById(R.id.new_img);
        news_title = findViewById(R.id.newtitle);
        dec = findViewById(R.id.desc);
        publish = findViewById(R.id.publish);

        fistore = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobile = mob.getText().toString();
                if (mobile.isEmpty()){
                    mob.setError("Enter mobile Number");
                    mob.requestFocus();
                    return;
                }

                Map<String,String> no = new HashMap<>();
                no.put("mobile",mobile);
                no.put("used","no");



                fistore.collection("mobilenos").document(mobile).set(no).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(admin_pannel.this, "mobile is registered", Toast.LENGTH_SHORT).show();
                            mob.setText("");

                        } else {
                            Toast.makeText(admin_pannel.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        new_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(admin_pannel.this);
            }
        });

        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String news_tit = news_title.getText().toString();
                final String news_des = dec.getText().toString();


                if (mainImgURI == null || news_tit.isEmpty() || news_des.isEmpty()){
                    Toast.makeText(admin_pannel.this, "ALL FIELDS SHOULD BE FILLED", Toast.LENGTH_SHORT).show();
                    return;

                }

                File imgpath = new File(mainImgURI.getPath());
                try {
                    compressedImageBitmap = new Compressor(admin_pannel.this)
                            .setQuality(75)
                            .setMaxHeight(200)
                            .setMaxWidth(200)
                            .compressToBitmap(imgpath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                compressedImageBitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                byte[] data = baos.toByteArray();

                final StorageReference image_path1 = mStorageRef.child("news").child(news_tit + ".jpg");

                image_path1.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        image_path1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Uri dnuri = uri;

                                storedata(dnuri,news_tit,news_des);
                            }
                        });
                    }
                });

                /*final StorageReference image_path = mStorageRef.child("news").child(news_tit + ".jpg");
                image_path.putFile(mainImgURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Uri dnuri = uri;

                                storedata(dnuri,news_tit,news_des);
                            }
                        });
                    }
                });*/
            }
        });


    }

    private void storedata(Uri dnuri, String news_tit, String news_des) {

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdft = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        Map<String,String> user = new HashMap<>();
        user.put("image",dnuri.toString());
        user.put("title",news_tit);
        user.put("desc",news_des);
        user.put("date",sdf.format(cal.getTime()));
        user.put("time",sdft.format(cal.getTime()));

        fistore.collection("news").document(news_tit).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    Toast.makeText(admin_pannel.this, "News Successfully published", Toast.LENGTH_SHORT).show();
                    Intent in =  new Intent(admin_pannel.this, MainActivity.class);
                    startActivity(in);
                    finish();

                } else {
                    Toast.makeText(admin_pannel.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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

                mainImgURI = result.getUri();
                new_img.setImageURI(mainImgURI);

            }
        }
    }

    public boolean onNavigateUp(){
        finish();
        return true;
    }
}
