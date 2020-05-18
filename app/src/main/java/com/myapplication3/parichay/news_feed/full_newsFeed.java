package com.myapplication3.parichay.news_feed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.myapplication3.parichay.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

public class full_newsFeed extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    Toolbar toolbar;
    ImageView imageView;
    TextView appbar_title,appbar_sub,date,full_desc,title;
    LinearLayout titleappbar;
    AppBarLayout appBarLayout;
    private boolean ishidetoolbarview = false;
    FrameLayout date_behaviour;
    String mimg,mtitle,mdate,mdesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_news_feed);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);

        appBarLayout = findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(this);

        date_behaviour = findViewById(R.id.date_behavior);
        titleappbar = findViewById(R.id.title_appbar);
        imageView = findViewById(R.id.backdrop);
        appbar_title = findViewById(R.id.title_on_appbar);
        appbar_sub = findViewById(R.id.subtitle_on_appbar);
        date = findViewById(R.id.date);
        title = findViewById(R.id.title);
        full_desc = findViewById(R.id.full_desc);

        mimg = getIntent().getStringExtra("imgurl");
        mtitle = getIntent().getStringExtra("title");
        mdate = getIntent().getStringExtra("date");
        mdesc = getIntent().getStringExtra("desc");

        RequestOptions temp = new RequestOptions();
        temp.placeholder(R.drawable.ic_account_circle_black_24dp);

        Glide.with(this)
                .setDefaultRequestOptions(temp)
                .load(mimg)
                .into(imageView);
        appbar_title.setText(mtitle);
        appbar_sub.setText(mdate);
        title.setText(mtitle);
        date.setText(mdate);
        full_desc.setText(mdesc);

        date_behaviour.setVisibility(View.VISIBLE);
        titleappbar.setVisibility(View.GONE);

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        int maxscroll = appBarLayout.getTotalScrollRange();
        float percent = (float) Math.abs(i) / (float) maxscroll;

        if (percent == 1f && !ishidetoolbarview){
            date_behaviour.setVisibility(View.GONE);
            titleappbar.setVisibility(View.VISIBLE);
            ishidetoolbarview = !ishidetoolbarview;
        }else if (percent < 1f && ishidetoolbarview){
            date_behaviour.setVisibility(View.VISIBLE);
            titleappbar.setVisibility(View.GONE);
            ishidetoolbarview = !ishidetoolbarview;
        }
    }
}
