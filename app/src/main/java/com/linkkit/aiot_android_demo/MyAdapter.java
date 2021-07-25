package com.linkkit.aiot_android_demo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    ArrayList lists;//用于显示的数据

    public MyAdapter(ArrayList lists) {

        this.lists = lists;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        //给view设定自定义的布局

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout,parent,false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }
    //<br>　　//绑定viewHolder的操作
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ViewGroup.LayoutParams params =  holder.itemView.getLayoutParams();//得到item的LayoutParams布局参数

        params.height = (int)(200+Math.random()*400) ;//把随机的高度赋予itemView布局

        holder.itemView.setLayoutParams(params);//把params设置给itemView布局
        holder.textView.setText(lists.get(position).toString());//绑定数据

    }



    @Override
    public int getItemCount() {
        return lists.size();
    }


    //自定义的ViewHolder，持有每个Item的的所有界面元素,其实是优化
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;

        public ViewHolder(View view){
            super(view);
            textView = (TextView)view.findViewById(R.id.textview);//实例化
        }
    }

}