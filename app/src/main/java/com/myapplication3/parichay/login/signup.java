package com.myapplication3.parichay.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
import com.myapplication3.parichay.profile.*;
import com.myapplication3.parichay.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class signup extends AppCompatActivity {

    EditText email;
    EditText pass,cnfpass,mob;
    ProgressBar progress;
    Button signup;
    TextView login;
    private FirebaseFirestore firestore;

    FirebaseAuth mAuth;
    private String  userId;
    String mo;
    private TextView help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#000000"));

        email = findViewById(R.id.email2);
        pass = findViewById(R.id.pass2);
        cnfpass = findViewById(R.id.cnfpass2);
        progress = findViewById(R.id.progressBar2);
        signup = findViewById(R.id.signup);
        login = findViewById(R.id.login);
        mob = findViewById(R.id.mob);
        help = findViewById(R.id.help);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

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
                    Toast.makeText(signup.this, "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final String em = email.getText().toString().trim();
                final String pas = pass.getText().toString().trim();
                String cnf = cnfpass.getText().toString().trim();
                mo = mob.getText().toString().trim();

                if (mo.isEmpty()){

                    mob.setError("Enter mobile number");
                    mob.requestFocus();
                    return;

                }else if (em.isEmpty()){

                    email.setError("Enter Email");
                    email.requestFocus();
                    return;

                }else if (pas.isEmpty() || pas.length() < 6){

                    pass.setError("Enter 6 Digit Password");
                    pass.requestFocus();
                    return;

                }else if(!cnf.equals(pas)){
                    cnfpass.setError("Password must be same");
                    cnfpass.requestFocus();
                    return;
                }
                progress.setVisibility(View.VISIBLE);

                signup.setEnabled(false);

                firestore.collection("mobilenos").document(mo).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()){
                            String used = task.getResult().getString("used");

                            if (!used.equals("yes")){
                                progress.setVisibility(View.INVISIBLE);
                                createuser(em,pas);
                            } else {
                                progress.setVisibility(View.INVISIBLE);
                                signup.setEnabled(true);
                                Toast.makeText(signup.this, "Mobile number is already used", Toast.LENGTH_LONG).show();
                            }

                        } else {
                            progress.setVisibility(View.INVISIBLE);
                            signup.setEnabled(true);
                            Toast.makeText(signup.this, "Register your mobile number to Admin", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(signup.this,login_act.class);
                startActivity(in);
                finish();
            }
        });


    }

    private void createuser(String em,String pas) {
        final ProgressDialog dialog = new ProgressDialog(signup.this);
        dialog.setMessage("Please wait");
        dialog.show();

        mAuth.createUserWithEmailAndPassword(em,pas)
                .addOnCompleteListener(signup.this,new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Calendar cal = Calendar.getInstance();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            cal.add(Calendar.MONTH, 2);

                            Map<String,String> user = new HashMap<>();
                            user.put("expiry",sdf.format(cal.getTime()));
                            user.put("service","free");

                            firestore.collection("mobilenos").document(mo).update("used","yes");

                            firestore.collection("expiry").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(user);
                            Intent in =  new Intent(signup.this, profile_setup.class);
                            startActivity(in);
                            finish();
                        }else {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            Toast.makeText(signup.this, "message " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        signup.setEnabled(true);

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            Intent in = new Intent(signup.this, MainActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(in);
        }
    }
}
