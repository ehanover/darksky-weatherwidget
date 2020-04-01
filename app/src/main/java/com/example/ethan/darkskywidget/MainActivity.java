package com.example.ethan.darkskywidget;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity { // TODO: add fields for timezone?
    static String LOG = "ASDF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.main_textview_attribution);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        SharedPreferences prefs = getSharedPreferences("com.example.ethan.darkskywidget", Context.MODE_PRIVATE);

        final EditText key = findViewById(R.id.main_edittext_key);
        String k = prefs.getString("key", "none");
        key.setText(k);

        final EditText location = findViewById(R.id.main_edittext_location);
        String l = prefs.getString("location", "none");
        location.setText(l);

        final EditText days = findViewById(R.id.main_edittext_days);
        String d = ""+prefs.getInt("days", -1);
        days.setText(d);

        final CheckBox info = findViewById(R.id.main_checkbox_showinfo);
        boolean i = prefs.getBoolean("info", true);
        info.setChecked(i);

        final CheckBox apparent = findViewById(R.id.main_checkbox_apparent);
        boolean a = prefs.getBoolean("apparent", true);
        info.setChecked(a);

        Button save = findViewById(R.id.main_button_save);
        save.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                SharedPreferences prefs = getSharedPreferences("com.example.ethan.darkskywidget", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("key", key.getText().toString());
                edit.putString("location", location.getText().toString());
                edit.putInt("days", Integer.parseInt(days.getText().toString()));
                edit.putBoolean("info", info.isChecked());
                edit.putBoolean("apparent", apparent.isChecked());
                edit.apply();

                // Log.d(LOG, "Saving input values to SharedPrefs. key is " + prefs.getString("key", "ERROR"));
            }
        });



    }
}
