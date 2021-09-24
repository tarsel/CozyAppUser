package com.fandataxiuser.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fandataxiuser.R;
import com.fandataxiuser.data.network.model.DeliveryData;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {

    private Context context;
   ArrayList<DeliveryData> arrayList;

    public ItemAdapter(Context context, ArrayList<DeliveryData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
       DeliveryData  promoList = arrayList.get(position);
        if (promoList != null) {
              holder.txt_productName.setText(promoList.getItemToDeliver()+"");
                holder.txt_phoneNumber.setText(promoList.getReceiverMobile()+"");
                holder.txt_address.setText(promoList.getDeliveryAddress()+"");
                holder.txt_personName.setText(promoList.getReceiverName()+"");


        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView txt_productName;
        private TextView txt_personName;
        private TextView txt_address;
        private TextView txt_phoneNumber;

        MyViewHolder(View view) {
            super(view);

            txt_productName = view.findViewById(R.id.tv_product_name);
            txt_personName = view.findViewById(R.id.tv_customer_name);
            txt_phoneNumber = view.findViewById(R.id.tv_phone);
            txt_address = view.findViewById(R.id.tv_address);
        }
    }
}
