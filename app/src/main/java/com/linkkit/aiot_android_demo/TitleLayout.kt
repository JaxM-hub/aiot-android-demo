package com.linkkit.aiot_android_demo

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.title.view.*
class TitleLayout (context: Context, attrs: AttributeSet) : LinearLayout(context,attrs) {
    init {
        LayoutInflater.from(context).inflate(R.layout.title,this)
        titleBack.setOnClickListener{
            val activity = context as Activity
            activity.finish()
        }
        titleEdit.setOnClickListener{
            Toast.makeText(context,"版本:1.0\n缪智强:1807020215", Toast.LENGTH_SHORT).show()
            //val intent = Intent(com.example.myapplication.TitleLayout,Scene::class.java)
        }
    }
}