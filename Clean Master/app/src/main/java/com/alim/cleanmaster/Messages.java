package com.alim.cleanmaster;

import android.content.Context;
import android.widget.Toast;

public class Messages {
    public  static  void sendMessage(Context context,String message){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }
}
