package com.ezpass.smopaye_mobile.NotifFragmentAdapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.ezpass.smopaye_mobile.DBLocale_Notifications.DbHandler;
import com.ezpass.smopaye_mobile.NotifFragmentModel.Item;
import com.ezpass.smopaye_mobile.R;
import java.util.ArrayList;

public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.MyViewHolder> implements Filterable {


    private Context context;
    private ArrayList<Item> list;
    private ArrayList<Item> listFilter;

    public CardListAdapter(Context context, ArrayList<Item> list, ArrayList<Item> listFilter) {
        this.context = context;
        this.list = list;
        this.listFilter = listFilter;
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

        Item listFilter1 = listFilter.get(position);
        //creation d'une animation
        holder.thumbnail.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation));
        holder.container.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale_animation));


        holder.name.setText(listFilter1.getName());
        holder.description.setText(listFilter1.getDescription());
        holder.price.setText(listFilter1.getPrice());
        holder.thumbnail.setImageResource(listFilter1.getThumbnail());
    }

    @Override
    public int getItemCount() {
        return listFilter.size();
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

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                String key = constraint.toString();
                if(key.isEmpty()){
                    listFilter = list;
                } else {
                    ArrayList<Item> firstFilter = new ArrayList<>();
                    for(Item row : list){
                        if(row.getName().toLowerCase().contains(key.toLowerCase())){
                            firstFilter.add(row);
                        }
                    }
                    listFilter = firstFilter;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = listFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                listFilter = (ArrayList<Item>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView name;
        private TextView description;
        private TextView price;
        private ImageView thumbnail;
        public RelativeLayout viewBackground;
        public RelativeLayout viewForeground;
        private RelativeLayout container;

        private MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
            price = itemView.findViewById(R.id.price);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            viewBackground = itemView.findViewById(R.id.view_background);
            viewForeground = itemView.findViewById(R.id.view_foreground);
            container = itemView.findViewById(R.id.container);
        }
    }
}
