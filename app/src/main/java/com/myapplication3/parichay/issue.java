package com.myapplication3.parichay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class issue extends AppCompatActivity {

    TextView sub,desc,cont,name;
    Button send;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue);

        getSupportActionBar().setTitle("Issue Application");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sub = findViewById(R.id.isubj);
        desc = findViewById(R.id.idesc);
        name = findViewById(R.id.iname);
        cont = findViewById(R.id.cont);
        send = findViewById(R.id.sendi);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String n,c,s,d;
                n = name.getText().toString();
                s = sub.getText().toString();
                d = desc.getText().toString();
                c = cont.getText().toString();

                Map<String,String> issue = new HashMap<>();
                issue.put("name",n);
                issue.put("contact",c);
                issue.put("subject",s);
                issue.put("desc",d);

                FirebaseFirestore.getInstance().collection("issue").document(uid).set(issue).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(issue.this, "Successfully Submitted", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
