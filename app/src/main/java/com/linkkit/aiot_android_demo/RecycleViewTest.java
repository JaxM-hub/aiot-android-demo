package com.linkkit.aiot_android_demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

public class RecycleViewTest extends AppCompatActivity {
    public class spaceItemDecoration extends RecyclerView.ItemDecoration {

        int space;

        public spaceItemDecoration(int space){

            this.space = space;


        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

//这里就是每个item的边距
            outRect.top = space;

            outRect.left = space;

            outRect.right = space;

            outRect.bottom = space;





        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_view_test);


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycleview);

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));//指定只有4列

        recyclerView.addItemDecoration(new spaceItemDecoration(30));//设置边距，这个类要自己实现

        ArrayList lists = new ArrayList();
        lists.add("1");

        for (int i = 0; i < 100; i++) {

            lists.add("" + i);

        }



        //创建并设置Adapter
        MyAdapter myAdapter = new MyAdapter(lists);

        recyclerView.setAdapter(myAdapter);



    }
}