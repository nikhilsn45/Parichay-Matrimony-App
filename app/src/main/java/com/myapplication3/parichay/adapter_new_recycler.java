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
import com.bumptech.glide.request.RequestOptions;
import com.myapplication3.parichay.ads.new_ads;
import com.myapplication3.parichay.news_feed.full_newsFeed;

import java.util.List;

public class adapter_new_recycler extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public List<news_post> newsList;
    public List<new_ads> adsList;
    public Context context;

    private static final int item_news = 0;
    private static final int item_ads = 1;

    public adapter_new_recycler(List<news_post> newsList,List<new_ads> adsList) {
        this.newsList = newsList;
        this.adsList = adsList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType)
        {
            case item_news:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item,parent,false);
                context = parent.getContext();
                return new newsviewholder(view);

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
            case item_news:
                newsviewholder newsholder = (newsviewholder)holder;
                final String img_data = newsList.get(position).getImgUrl();
                final String date_data = newsList.get(position).getDate();
                final String title_data = newsList.get(position).getTitle();
                final String desc_data = newsList.get(position).getDesc();

                newsholder.settext(desc_data,date_data,title_data,img_data);
                newsholder.item_container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent in = new Intent(context, full_newsFeed.class);
                        in.putExtra("imgurl",img_data);
                        in.putExtra("desc",desc_data);
                        in.putExtra("date",date_data);
                        in.putExtra("title",title_data);
                        context.startActivity(in);
                    }
                });
                break;

            case item_ads:
                adsviewholder adholder = (adsviewholder)holder;
                String adtitle = adsList.get(position).getAdtitle();
                String adimg = adsList.get(position).getAdimg();

                adholder.settext(adtitle,adimg);
                break;
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
            return item_news;
    }

    /*public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView desc;
        private TextView time;
        private TextView title;
        private ImageView img;
        ConstraintLayout item_container;
        private View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        public void settext (String descstr,String datestr,String titlestr,String imgstr) {
            desc = view.findViewById(R.id.newsdesc);
            time = view.findViewById(R.id.newsdate);
            title = view.findViewById(R.id.newstitle);
            img = view.findViewById(R.id.newsimg);
            item_container = view.findViewById(R.id.item_container);

            desc.setText(descstr);
            time.setText(datestr);
            title.setText(titlestr);

            RequestOptions temp = new RequestOptions();

            Glide.with(itemView.getContext())
                    .load(imgstr)
                    .into(img);
        }
    }*/

    public static class newsviewholder extends RecyclerView.ViewHolder
    {
        private TextView desc;
        private TextView time;
        private TextView title;
        private ImageView img;
        ConstraintLayout item_container;
        private View view;

        public newsviewholder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        public void settext (String descstr,String datestr,String titlestr,String imgstr) {
            desc = view.findViewById(R.id.newsdesc);
            time = view.findViewById(R.id.newsdate);
            title = view.findViewById(R.id.newstitle);
            img = view.findViewById(R.id.newsimg);
            item_container = view.findViewById(R.id.item_container);

            desc.setText(descstr);
            time.setText(datestr);
            title.setText(titlestr);

            RequestOptions temp = new RequestOptions();

            Glide.with(itemView.getContext())
                    .load(imgstr)
                    .into(img);
        }
    }

    public static class adsviewholder extends RecyclerView.ViewHolder
    {
        View view;
        TextView title_ad;
        ImageView img;

        public adsviewholder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        public void settext(String adtitle,String adimg) {
            title_ad = view.findViewById(R.id.adtitle);
            img = view.findViewById(R.id.adi);

            title_ad.setText(adtitle);
            Glide.with(itemView.getContext())
                    .load(adimg)
                    .into(img);
        }
    }

}
