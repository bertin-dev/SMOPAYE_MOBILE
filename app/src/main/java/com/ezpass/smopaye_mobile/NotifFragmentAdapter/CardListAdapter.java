package com.ezpass.smopaye_mobile.NotifFragmentAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.ezpass.smopaye_mobile.DBLocale_Notifications.DbHandler;
import com.ezpass.smopaye_mobile.NotifFragmentModel.Item;
import com.ezpass.smopaye_mobile.R;
import java.util.ArrayList;

public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.MyViewHolder> {


    private Context context;
    private ArrayList<Item> list;

    public CardListAdapter(Context context, ArrayList<Item> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_notification, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Item item = list.get(position);
        holder.name.setText(item.getName());
        holder.description.setText(item.getDescription());
        holder.price.setText(item.getPrice());
        holder.thumbnail.setImageResource(item.getThumbnail());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void removeItem(int position, int id, Context context){
        list.remove(position);

        DbHandler db1 = new DbHandler(context);
         db1.DeleteOneNotification(id);

        notifyItemRemoved(position);
    }

    public void restoreItem(Item item, int position){
        list.add(position, item);
        notifyItemInserted(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView name;
        public TextView description;
        public TextView price;
        public ImageView thumbnail;
        public RelativeLayout viewBackground;
        public RelativeLayout viewForeground;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
            price = itemView.findViewById(R.id.price);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            viewBackground = itemView.findViewById(R.id.view_background);
            viewForeground = itemView.findViewById(R.id.view_foreground);
        }
    }
}
