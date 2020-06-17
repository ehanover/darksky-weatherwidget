package com.github.ehanover.weatherwidget_darksky;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class Widget1Service extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        // Log.d("ASDF", "onGetViewFactory()");

        return new Widget1ServiceFactory(this.getApplicationContext(), intent);
    }


}
