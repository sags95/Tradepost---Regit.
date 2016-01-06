package com.sinapp.sharathsind.tradepost;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import CustomWidget.CustomButton;
import CustomWidget.CustomTextView;

public class MyItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        View customToolBarTitle = getLayoutInflater().inflate(R.layout.layout_toolbar_custom_title, null);
        getSupportActionBar().setCustomView(customToolBarTitle);
        CustomTextView title1 = (CustomTextView)customToolBarTitle.findViewById(R.id.toolbar_title1);
        CustomTextView title2 = (CustomTextView)customToolBarTitle.findViewById(R.id.toolbar_title2);
        title1.setText("My Items");
        title2.setVisibility(View.GONE);

        View mainLayout = (View)findViewById(R.id.myItem_fragment_layout);
        CustomButton postItem = (CustomButton)mainLayout.findViewById(R.id.myItems_emptyView_postBtn);
        postItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(),ListingProcessActivity.class));

            }
        });



    }

}
