package com.linkkit.aiot_android_demo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDateTime

class Scene : AppCompatActivity() {
    lateinit var timeChangeReceiver:TimeChangeReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scene)
        supportActionBar!!.hide()
//        val yanshi:TextView = findViewById(R.id.YanShi)
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.intent.action.TIME_TICK")
        timeChangeReceiver = TimeChangeReceiver()
        registerReceiver(timeChangeReceiver,intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(timeChangeReceiver)
    }

    inner class TimeChangeReceiver:BroadcastReceiver(){
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onReceive(context: Context?, intent: Intent?) {
            val current = LocalDateTime.now().toString()
            Toast.makeText(context,current,Toast.LENGTH_SHORT).show()
            val yanshi:TextView = findViewById(R.id.YanShi)
            yanshi.setText(current)
        }
    }
}