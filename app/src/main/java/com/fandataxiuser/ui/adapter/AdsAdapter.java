package com.fandataxiuser.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fandataxiuser.BuildConfig;
import com.fandataxiuser.R;
import com.fandataxiuser.data.network.model.ads.DataItem;

import java.util.ArrayList;

public class AdsAdapter extends RecyclerView.Adapter<AdsAdapter.MyViewHolder> {

    private Context context;
   ArrayList<DataItem> arrayList;

    public AdsAdapter(Context context, ArrayList<DataItem> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ads_item, parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DataItem  promoList = arrayList.get(position);
        if (promoList != null) {
            Glide.with(context)
                    .load(BuildConfig.BASE_IMAGE_URL+promoList.getImage())
                    .apply(RequestOptions
                            .placeholderOf(R.drawable.logo)
                            .dontAnimate()
                            .error(R.drawable.logo))
                    .into(holder.imageView);
         holder.cardView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 try {
                      String url = promoList.getLink();
                      Intent i = new Intent(Intent.ACTION_VIEW);
                     i.setData(Uri.parse(url));
                     i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                     context.startActivity(i);
                 }catch (Exception e){
                    e.printStackTrace();
                 }
             }
         });
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        CardView cardView;

        MyViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.iv_adds);
            cardView = view.findViewById(R.id.cv_container);
          }
    }
}
