package com.myapplication3.parichay.ads;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.myapplication3.parichay.R;
import com.myapplication3.parichay.myad;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class publish_ad extends AppCompatActivity {

    private static final String TAG = "";
    ImageView adimg;
    TextView adtit,addur,amt;
    Button publish;
    Uri mainImgURI = null;

    ProgressDialog dialog;

    String uid;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_ad);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Advertise");

        adimg = findViewById(R.id.adimg);
        adtit = findViewById(R.id.adtit);
        addur = findViewById(R.id.addur);
        publish = findViewById(R.id.publish);

        dialog = new ProgressDialog(publish_ad.this);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        adimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(publish_ad.this);
            }
        });

        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String ti = adtit.getText().toString();
                final String du = addur.getText().toString();


                if (mainImgURI == null || ti.isEmpty() || du.isEmpty()){
                    Toast.makeText(publish_ad.this, "ALL FIELDS SHOULD BE FILLED", Toast.LENGTH_SHORT).show();
                    return;

                }

                dialog.setMessage("Please wait");
                dialog.show();

                final StorageReference image_path = mStorageRef.child("ad").child(ti + ".jpg");
                image_path.putFile(mainImgURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Uri dnuri = uri;

                                storedata(dnuri,ti,du);
                            }
                        });
                    }
                });
            }
        });



    }

    private void storedata(Uri dnuri, String ti, final String du) {

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        String cu = sdf.format(cal.getTime());
        cal.add(Calendar.DATE, Integer.parseInt(du));
        String ex = sdf.format(cal.getTime());

        myad ad1 = new myad(dnuri.toString(),ti,cu,ex,du,"pending");


        FirebaseFirestore.getInstance().collection("advertises").document(uid).collection("myads")
                .add(ad1).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }

                int val = Integer.parseInt(du) * 20;

                Intent in = new Intent(publish_ad.this,pay_ad.class);
                in.putExtra("amount",Integer. toString(val));
                in.putExtra("documentid",task.getResult().getId());
                startActivity(in);
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
                adimg.setImageURI(mainImgURI);

            }
        }
    }


}
