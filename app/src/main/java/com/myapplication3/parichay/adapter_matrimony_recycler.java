package com.myapplication3.parichay;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.myapplication3.parichay.ads.new_ads;

import java.util.List;

public class adapter_matrimony_recycler extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public List<matrimony_user> newsList;
    public List<new_ads> adsList;
    public Context context;

    private static final int item_mat = 0;
    private static final int item_ads = 1;

    public adapter_matrimony_recycler(List<matrimony_user> newsList,List<new_ads> adsList) {
        this.newsList = newsList;
        this.adsList = adsList;
        }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType)
        {
        case item_mat:
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.matrimony_item,parent,false);
            context = parent.getContext();
            return new matholder(view);

        case item_ads:

        default:
            View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.ads_item,parent,false);
            context = parent.getContext();
            return new adsviewholder(view2);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

             int viewtype = getItemViewType(position);

        switch (viewtype)
        {
            case item_mat:
                matholder matholder = (matholder) holder;
                final String img_data = newsList.get(position).getImg1();
                final String f = newsList.get(position).getFirst();
                final String s = newsList.get(position).getSec();
                final String t = newsList.get(position).getThird();
                final String h = newsList.get(position).getHeight();
                final String w = newsList.get(position).getweight();
                final String a = newsList.get(position).getAge();


                     matholder.settext(f + " " + s + " " + t ,img_data,h,w,a+" Yrs");
                     matholder.container.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent in = new Intent(context,full_mat_profile.class);
                                in.putExtra("image",img_data);
                                in.putExtra("name",f + " " + s + " " + t );
                                in.putExtra("age",a);

                                context.startActivity(in);
                            }
                     });
                break;

            case item_ads:
                adsviewholder adholder = (adsviewholder)holder;
                final String adtitle = adsList.get(position).getAdtitle();
                final String adim = adsList.get(position).getAdimg();

                adholder.settext(adtitle,adim);
        }

    }

@Override
public int getItemCount() {
        return newsList.size();
        }

@Override
public int getItemViewType(int position) {

        if (position % 6 == 0)
        return item_ads;
        else
        return item_mat;
        }


public static class matholder extends RecyclerView.ViewHolder
{
    private TextView name,he,we,ag;
    private ImageView img;
    private View view;
    ConstraintLayout container;

    public matholder(@NonNull View itemView) {
        super(itemView);
        view = itemView;
    }

    public void settext (String na,String imgstr,String h,String w,String a) {
        img = view.findViewById(R.id.matimg);
        name = view.findViewById(R.id.matName);
        container = view.findViewById(R.id.item_container);
        he = view.findViewById(R.id.height);
        we = view.findViewById(R.id.weight);
        ag = view.findViewById(R.id.ageyr);

        name.setText(na);
        he.setText(h);
        we.setText(w);
        ag.setText(a);

        Glide.with(itemView.getContext())
                .load(imgstr)
                .into(img);
    }
}

public static class adsviewholder extends RecyclerView.ViewHolder
{
    View view;
    TextView title_ad;
    ImageView adi;

    public adsviewholder(@NonNull View itemView) {
        super(itemView);
        view = itemView;
    }

    public void settext(String adtitle,String adim) {
        title_ad = view.findViewById(R.id.adtitle);
        adi = view.findViewById(R.id.adi);

        title_ad.setText(adtitle);
        Glide.with(itemView.getContext())
                .load(adim)
                .into(adi);
    }
}

}