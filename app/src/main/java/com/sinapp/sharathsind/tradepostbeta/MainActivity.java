package com.sinapp.sharathsind.tradepostbeta;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by HenryChiang on 2015-12-28.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Welcome Back Sample User!");
        setSupportActionBar(toolbar);

        TextView addItem = (TextView)findViewById(R.id.add_item_addBtn);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ListingProcessActivity.class));
            }
        });

        TextView findCommunity = (TextView)findViewById(R.id.community_setBtn);
        findCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLocationDialog();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuItem item;

        item = menu.add("Settings");
        item.setIcon(R.mipmap.ic_send);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        return super.onCreateOptionsMenu(menu);
    }


    private void showLocationDialog(){
        int dialogLayout = R.layout.layout_location_dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);

        final View dialogView = getLayoutInflater().inflate(dialogLayout, null, false);
        final RadioButton rBtnLocScr = (RadioButton) dialogView.findViewById(R.id.radioButton_locService);
        final RadioButton rBtnPosCode = (RadioButton) dialogView.findViewById(R.id.radioButton_postalCode);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (rBtnLocScr.isChecked()) {
                    Toast.makeText(getApplicationContext(), "Location Service", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }else if (rBtnPosCode.isChecked()) {
                    Toast.makeText(getApplicationContext(), "Postal Code", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.setView(getLayoutInflater().inflate(dialogLayout, null, false));

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
//      dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        rBtnLocScr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rBtnPosCode.setChecked(!isChecked);
            }
        });

        rBtnPosCode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rBtnLocScr.setChecked(!isChecked);
            }
        });



    }




}
