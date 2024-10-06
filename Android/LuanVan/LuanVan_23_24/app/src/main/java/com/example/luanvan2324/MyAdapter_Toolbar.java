package com.example.luanvan2324;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MyAdapter_Toolbar extends BaseAdapter {

    private Context context;
    private int layout;
    private List<Items_Toolbar> list;

    public MyAdapter_Toolbar(Context context, int layout, List<Items_Toolbar> list) {
        this.context = context;
        this.layout = layout;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder{
        TextView tv;
        ImageView img;
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {

        ViewHolder viewHolder;
        if(converView == null ) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            converView = inflater.inflate(layout, null);
            viewHolder = new ViewHolder();

            viewHolder.tv = (TextView) converView.findViewById(R.id.tvItem);
            viewHolder.img = (ImageView) converView.findViewById(R.id.imgIcon);

            converView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) converView.getTag();
        }

        viewHolder.tv.setText(list.get(position).tenItem);
        viewHolder.img.setImageResource(list.get(position).icon);

        return converView;
    }
}
