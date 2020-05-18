package com.myapplication3.parichay.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.myapplication3.parichay.MainActivity;
import com.myapplication3.parichay.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login_act extends AppCompatActivity {

    Button login;
    EditText email,pass;
    ProgressBar progress;
    FirebaseAuth mAuth;
    TextView forgot;

    TextView sign,help;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_act);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#000000"));

        email =findViewById(R.id.email);
        pass =findViewById(R.id.pass);
        progress = findViewById(R.id.progressBar1);
        login = findViewById(R.id.login);
        sign = findViewById(R.id.signup);
        forgot = findViewById(R.id.forgot);
        help = findViewById(R.id.help);

        mAuth = FirebaseAuth.getInstance();

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(login_act.this,reset_pass.class));

            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String contact = "+91 8605598802"; // use country code with your phone number
                String url = "https://api.whatsapp.com/send?phone=" + contact;
                try {
                    PackageManager pm = getPackageManager();
                    pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(login_act.this, "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String em = email.getText().toString().trim();
                String pas = pass.getText().toString().trim();

                if (em.isEmpty()){

                    email.setError("Enter Valid Email");
                    email.requestFocus();
                    return;

                }else if (pas.isEmpty() || pas.length() < 6){

                    pass.setError("Enter 6 Digit Password");
                    pass.requestFocus();
                    return;

                }

                progress.setVisibility(View.VISIBLE);
                login.setEnabled(false);

                mAuth.signInWithEmailAndPassword(em,pas)
                        .addOnCompleteListener(login_act.this,new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Intent in = new Intent(login_act.this, MainActivity.class);
                            startActivity(in);
                            finish();
                        }else {
                            Toast.makeText(login_act.this, "message " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        login.setEnabled(true);
                        progress.setVisibility(View.INVISIBLE);
                    }
                });

            }
        });

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(login_act.this, signup.class);
                startActivity(in);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            Intent in = new Intent(login_act.this, MainActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(in);
        }
    }
}
