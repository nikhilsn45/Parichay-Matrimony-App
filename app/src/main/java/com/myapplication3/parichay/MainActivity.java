package com.myapplication3.parichay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


import com.myapplication3.parichay.login.login_act;
import com.myapplication3.parichay.profile.profile_setup;
import com.myapplication3.parichay.subscription.subscribe;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    Toolbar toolbar;
    TextView logout;
    TextView navname,city_state;
    NavigationView navigationView;
    View headerview;
    FloatingActionButton admi;

    private String userId;
    private FirebaseFirestore fistore;

    String n,p,s,i,c,e;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer);
        logout = findViewById(R.id.Logout);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        headerview = navigationView.getHeaderView(0);

        navname = (TextView) headerview.findViewById(R.id.nav_name);
        city_state = (TextView) headerview.findViewById(R.id.city_state);
        admi = findViewById(R.id.admin);


        fistore = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        fistore.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        String n = task.getResult().getString("name");
                        String p = task.getResult().getString("place");
                        String s = task.getResult().getString("state");

                        navname.setText(n);
                        city_state.setText(p+","+s);

                    } else {
                        Toast.makeText(MainActivity.this, "data doesnt exists", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });


        headerview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivity.this, profile_setup.class);
                startActivity(in);
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent in = new Intent(MainActivity.this, login_act.class);
                startActivity(in); 
                finish();
            }
        });

        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();
        toolbar.setTitle("Home");

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new home_news()).commit();
            navigationView.setCheckedItem(R.id.news);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        fistore = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        fistore.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        n = task.getResult().getString("name");
                        p = task.getResult().getString("place");
                        s = task.getResult().getString("state");
                        i = task.getResult().getString("image");
                        c = task.getResult().getString("city");

                        if (n == null){
                            startActivity(new Intent(MainActivity.this,profile_setup.class));
                        }

                        navname.setText(n);
                        city_state.setText(p+","+s);

                        fistore.collection("expiry").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().exists()) {
                                        e = task.getResult().getString("expiry");

                                        Calendar cal = Calendar.getInstance();
                                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                        String current_date = sdf.format(cal.getTime());

                                        try{
                                            Date date1 = sdf.parse(current_date);
                                            Date date2 = sdf.parse(e);

                                            if (date2.compareTo(date1)<0)
                                            {
                                                Toast.makeText(MainActivity.this, "OOPS..You are Out of Subscription.Please Subscribe!!", Toast.LENGTH_SHORT).show();
                                                Intent in = new Intent(MainActivity.this, subscribe.class);
                                                in.putExtra("name",n);
                                                in.putExtra("place",p);
                                                in.putExtra("state",s);
                                                in.putExtra("image",i);
                                                in.putExtra("city",c);
                                                in.putExtra("expiry",e);
                                                startActivity(in);
                                            }

                                        }catch (ParseException e1){
                                            e1.printStackTrace();
                                        }

                                    }
                                }
                            }
                        });

                    } else {
                        startActivity(new Intent(MainActivity.this,profile_setup.class));
                    }

                } else {
                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.news:
                toolbar.setTitle("Home");
                toolbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FA6139")));
                Window window1 = getWindow();
                window1.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window1.setStatusBarColor(Color.parseColor("#FA6139"));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new home_news()).commit();
                break;

            case R.id.matrimony:
                toolbar.setTitle("Matrimony");
                toolbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#C50000")));
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.parseColor("#C50000"));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new home_matrimony()).commit();
                break;

            case R.id.Advertise:
                toolbar.setTitle("Advertise");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new advertise_fragment()).commit();
                break;

//            case R.id.notification:
//                toolbar.setTitle("Notifications");
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        new home_notification()).commit();
//                break;
//
//            case R.id.Settings:
//                toolbar.setTitle("Settings");
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        new setting_fragment()).commit();
//                break;

            case R.id.Subscribe:
                Intent in = new Intent(MainActivity.this,subscribe.class);
                in.putExtra("name",n);
                in.putExtra("place",p);
                in.putExtra("state",s);
                in.putExtra("image",i);
                in.putExtra("city",c);
                in.putExtra("expiry",e);
                startActivity(in);
                break;

            case R.id.Contact:
                startActivity(new Intent(MainActivity.this,issue.class));
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if(toolbar.getTitle() != "Home") {
            toolbar.setTitle("Home");
            toolbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FA6139")));
            Window window1 = getWindow();
            window1.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window1.setStatusBarColor(Color.parseColor("#FA6139"));
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new home_news()).commit();
        }
        else{
                super.onBackPressed();
            }
        }
}
