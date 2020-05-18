package com.myapplication3.parichay.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.myapplication3.parichay.MainActivity;
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

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class profile_setup extends AppCompatActivity {

    CircleImageView dp;
    EditText name,mob,place,city,state,dob;
    Button setup;
    ProgressBar progresss;

    private Uri mainImgURI = null;
    private String userId,acc_type;

    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private FirebaseFirestore fistore;
    private boolean ischanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");

        dp = findViewById(R.id.profilePic);
        name = findViewById(R.id.name);
        mob = findViewById(R.id.mob);
        place = findViewById(R.id.place);
        city = findViewById(R.id.city);
        state = findViewById(R.id.state);
        dob = findViewById(R.id.dob);
        setup = findViewById(R.id.setup);
        progresss =findViewById(R.id.progressBar3);

        fistore = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        try {
            userId = mAuth.getCurrentUser().getUid();
        } catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        fistore.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                    if (task.getResult().exists()){

                        progresss.setVisibility(View.VISIBLE);
                        String img = task.getResult().getString("image");
                        String n = task.getResult().getString("name");
                        String m = task.getResult().getString("mobile");
                        String p = task.getResult().getString("place");
                        String c = task.getResult().getString("city");
                        String s = task.getResult().getString("state");
                        String d = task.getResult().getString("dob");
                        acc_type  = task.getResult().getString("type");

                        name.setText(n);
                        mob.setText(m);
                        place.setText(p);
                        city.setText(c);
                        state.setText(s);
                        dob.setText(d);

                        mainImgURI = Uri.parse(img);


                        RequestOptions temp = new RequestOptions();
                        temp.placeholder(R.drawable.ic_account_circle_black_24dp);

                        Glide.with(profile_setup.this).setDefaultRequestOptions(temp).load(img).into(dp);

                        progresss.setVisibility(View.INVISIBLE);

                    } else {
                        acc_type = null;
                        Toast.makeText(profile_setup.this, "data doesnt exists", Toast.LENGTH_SHORT).show();
                        progresss.setVisibility(View.INVISIBLE);
                    }

                } else {
                    Toast.makeText(profile_setup.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progresss.setVisibility(View.INVISIBLE);
                }
            }
        });



        setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progresss.setVisibility(View.VISIBLE);

                final String nam = name.getText().toString();
                final String mo = mob.getText().toString();
                final String pla = place.getText().toString();
                final String cit = city.getText().toString();
                final String stat = state.getText().toString();
                final String birth = dob.getText().toString();


                if(ischanged){

                    if (mainImgURI == null || nam.isEmpty() || mo.isEmpty() || pla.isEmpty() || cit.isEmpty() || stat.isEmpty() ||birth.isEmpty()){
                        Toast.makeText(profile_setup.this, "ALL FIELDS SHOULD BE FILLED", Toast.LENGTH_SHORT).show();
                        progresss.setVisibility(View.INVISIBLE);
                        return;

                    }


                    setup.setEnabled(false);

                        final StorageReference image_path = mStorageRef.child("profiles").child(userId + ".jpg");
                        image_path.putFile(mainImgURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Uri dnuri = uri;

                                        storedata(dnuri,nam,mo,pla,cit,stat,birth);
                                        progresss.setVisibility(View.INVISIBLE);
                                    }
                                });
                            }
                        });

                } else {
                    storedata(null,nam,mo,pla,cit,stat,birth);
                }
            }
        });

        dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if(ContextCompat.checkSelfPermission(profile_setup.this, Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED){

                        ActivityCompat.requestPermissions(profile_setup.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

                    } else {

                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1,1)
                                .start(profile_setup.this);
                    }
                }

            }
        });

    }

    private void storedata(Uri dnuri ,String nam,String mo,String pla,String cit,String stat,String birth) {

        progresss.setVisibility(View.VISIBLE);

        if (mainImgURI == null || nam.isEmpty() || mo.isEmpty() || pla.isEmpty() || cit.isEmpty() || stat.isEmpty() ||birth.isEmpty()){
            Toast.makeText(profile_setup.this, "ALL FIELDS SHOULD BE FILLED", Toast.LENGTH_SHORT).show();
            progresss.setVisibility(View.INVISIBLE);
            return;

        }

        if(dnuri == null){
            dnuri = mainImgURI;
        }
        if (acc_type == null){
            acc_type = "user";
        }

        Map<String,String> user = new HashMap<>();
        user.put("image",dnuri.toString());
        user.put("name",nam);
        user.put("mobile",mo);
        user.put("place",pla);
        user.put("city",cit);
        user.put("state",stat);
        user.put("dob",birth);
        user.put("matrimony","no");
        user.put("type",acc_type);

        fistore.collection("users").document(userId).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    Toast.makeText(profile_setup.this, "Profile is updated", Toast.LENGTH_SHORT).show();
                    Intent in =  new Intent(profile_setup.this, MainActivity.class);
                    startActivity(in);
                    progresss.setVisibility(View.INVISIBLE);
                    finish();

                } else {
                    Toast.makeText(profile_setup.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                    setup.setEnabled(true);
                    progresss.setVisibility(View.INVISIBLE);
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
                dp.setImageURI(mainImgURI);
                ischanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
