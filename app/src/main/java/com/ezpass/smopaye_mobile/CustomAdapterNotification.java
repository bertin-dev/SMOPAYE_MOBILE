package com.ezpass.smopaye_mobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdapterNotification extends BaseAdapter {

    Context context;
    String Item[];
    String SubItem[];
    int flags[];
    String ItemDate[];
    LayoutInflater inflter;

    public CustomAdapterNotification(Context applicationContext, String[] Item, String[] SubItem , int[] flags, String[] ItemDate) {
        this.context = context;
        this.Item = Item;
        this.SubItem = SubItem;
        this.flags = flags;
        this.ItemDate = ItemDate;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return Item.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.listitem_notification, null);
        /*TextView item = (TextView) view.findViewById(R.id.item);
        TextView subitem = (TextView) view.findViewById(R.id.subitem);
        ImageView image = (ImageView) view.findViewById(R.id.image);
        TextView itemDate = (TextView) view.findViewById(R.id.itemDate);
        item.setText(Item[i]);
        subitem.setText(SubItem[i]);
        image.setImageResource(flags[i]);
        itemDate.setText(ItemDate[i]);*/
        return view;
    }
}
