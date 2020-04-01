package com.example.ethan.darkskywidget;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

public class Widget1Service extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        // Log.d("ASDF", "onGetViewFactory()");

        return new Widget1ServiceFactory(this.getApplicationContext(), intent);
    }


}
