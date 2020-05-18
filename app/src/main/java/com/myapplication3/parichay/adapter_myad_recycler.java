package com.myapplication3.parichay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static java.lang.Integer.parseInt;


public class adapter_myad_recycler extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public List<myad> newsList;
    public Context context;

    public adapter_myad_recycler(List<myad> newsList) {
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.myaditem,parent,false);
                context = parent.getContext();
                return new myadholder(view2);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

        myadholder myholder = (myadholder) holder;
        final String img_data = newsList.get(position).getAdimg();
        final String at = newsList.get(position).getAdtitle();
        final String ac = newsList.get(position).getC_date();
        final String ae = newsList.get(position).getExpiry();
        final String ad = newsList.get(position).getDuration();
        String as = "Active";


        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String c_date = sdf.format(cal.getTime());

        try {
            if (sdf.parse(ae).compareTo(sdf.parse(c_date))<0){
                as = "Inactive";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        myholder.settext(at,ad,as,ac,ae,img_data);
        myholder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }



    public static class myadholder extends RecyclerView.ViewHolder
    {
        private TextView adtitle,dur,stat1,stat2,pub,exp;
        private ImageView adimg;
        private View view;
        ConstraintLayout container;

        public myadholder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        public void settext (String at,String ad,String as,String ap,String ae,String ai) {
            adtitle = view.findViewById(R.id.adtitle);
            dur = view.findViewById(R.id.dur);
            stat1 = view.findViewById(R.id.stat1);
            stat2 = view.findViewById(R.id.stat2);
            pub = view.findViewById(R.id.pub);
            exp = view.findViewById(R.id.exp);
            adimg = view.findViewById(R.id.newsimg);
            container = view.findViewById(R.id.item_container);

            adtitle.setText(at);
            dur.setText(ad);
            if (as == "Active"){
                stat2.setVisibility(View.VISIBLE);
                stat1.setVisibility(View.GONE);
            } else {
                stat2.setVisibility(View.GONE);
                stat1.setVisibility(View.VISIBLE);
            }
            pub.setText(ap);
            exp.setText(ae);

            Glide.with(itemView.getContext())
                    .load(ai)
                    .into(adimg);
        }
    }

}