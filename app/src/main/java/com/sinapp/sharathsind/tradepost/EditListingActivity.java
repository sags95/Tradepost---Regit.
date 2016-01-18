package com.sinapp.sharathsind.tradepost;

import android.*;
import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.analytics.ExceptionReporter;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

import org.apmem.tools.layouts.FlowLayout;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import CustomWidget.CustomEditText;
import CustomWidget.CustomPagerAdapter;
import CustomWidget.CustomSpinnerAdapter;
import CustomWidget.CustomTextView;
import CustomWidget.LimitedEditText;
import Model.RegisterWebService;
import Model.Variables;
import webservices.MainWebService;

/**
 * Created by HenryChiang on 15-09-02.
 */
public class EditListingActivity extends AppCompatActivity {

    /**
     * Hold a reference to the current animator, so that it can be canceled mid-way.
     */
    private Animator mCurrentAnimator;

    /**
     * The system "short" animation time duration, in milliseconds. This duration is ideal for
     * subtle animations or animations that occur very frequently.
     */
    private int mShortAnimationDuration;


    final int MAX_NUM_TAGS = 5;
    int TAGS_COUNT = 0;
    private FlowLayout tagFlowLayout;
    private LinearLayout singleTagLayout;
    private String[] categories;
    private ImageView camera, folder, itemImg1, itemImg2, itemImg3, itemImg4;
    private int requestCodeCam = 0;
    private int requestCodeGal = 1;
    private ArrayList<Bitmap> tempBitmap = new ArrayList<>();
    private ArrayList<ImageView> imageViewArrayList = new ArrayList<>();
    private Uri mImageUri;
    private CustomTextView tagsCount;
    private ColorStateList oldColors;

    //section1
    private LimitedEditText itemName;
    //section2

    //section3
    private SeekBar seekBar;
    //section4
    private LimitedEditText desEditText;
    //section5

    //section6
    private CustomSpinnerAdapter spinnerAdapter;
    public int itemid;
    public ArrayList<String> tags;
    public ArrayList<Bitmap>bits;
    private CustomEditText tagInput;
    Spinner spinner;
ArrayList<Integer>offers;
ArrayList<Integer>userid;
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Tracker t =
                ((TradePost) getApplication()).getDefaultTracker();
        Thread.UncaughtExceptionHandler myHandler = new ExceptionReporter(
                t,                                        // Currently used Tracker.
                Thread.getDefaultUncaughtExceptionHandler(),      // Current default uncaught exception handler.
                this);
        switch (requestCode)
        {

            case 1:
                if(grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED)
                {
                    camera();


                }
                break;
            case 2:
                if(grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED)
                {
                    gallery();
                }
        }

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_process);
        tagFlowLayout = (FlowLayout) findViewById(R.id.section5_tags);

        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);


        Permission permission = new Permission(this, null);
        if (permission.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || permission.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            permission.askPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},4);
        }

//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... params) {
//                offers=new ArrayList<Integer>();
//                userid=new ArrayList<Integer>();
//                ArrayList<String> itemInfoForEdit = getIntent().getStringArrayListExtra("itemToEdit");
//                 itemid=Integer.parseInt(itemInfoForEdit.get(0));
//               Cursor cursor= Constants.db.rawQuery("select * from offers where itemid="+itemid,null);
//
//                cursor.moveToFirst();
//                while (!cursor.isAfterLast())
//                {
//                    int status=cursor.getInt(cursor.getColumnIndex("status"));
//                    if (status==0||status==1) {
//                        offers.add(cursor.getInt(cursor.getColumnIndex("Offerid")));
//                        userid.add(cursor.getInt(cursor.getColumnIndex("userid")));
//                    }
//                    cursor.moveToNext();
//                }
//                SoapObject soapObject=new SoapObject("http://webser/","getOfferDelete");
//                soapObject.addProperty("i",itemid);
//                Vector res= MainWebService.getMsg1(soapObject, "http://services.tradepost.me:8084/TDserverWeb/EditdeleteItem?wsdl", "http://webser/EditdeleteItem/getOfferDeleteRequest");
//               if(res!=null) {
//                   for (Object i : res) {
//                       cursor = Constants.db.rawQuery("select * from offers where offerid=" + i, null);
//                       cursor.moveToFirst();
//                       int status = cursor.getInt(cursor.getColumnIndex("status"));
//                       if (status == 0 || status == 1) {
//                           offers.add(cursor.getInt(cursor.getColumnIndex("Offerid")));
//                           userid.add(cursor.getInt(cursor.getColumnIndex("userid")));
//                       }
//                   }
//
//               }
//
//                cursor.close();
//              //  int h = res.size();
//
//                return null;
//            }
//        }.execute(null,null,null);
        //toolbar
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
        title1.setText("Add Your Item");
        title2.setVisibility(View.GONE);

        tags=new ArrayList<String>();
        bits=new ArrayList<Bitmap>();

        //section 1
        itemName = (LimitedEditText) findViewById(R.id.section1_edit);
        itemName.setMaxLines(1);
        itemName.setMaxCharacters(70);
        final CustomTextView itemNameCharCount = (CustomTextView)findViewById(R.id.section1_char_count);
        itemName.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //This sets a textview to the current length
                itemNameCharCount.setText(String.valueOf(s.length()));
            }

            public void afterTextChanged(Editable s) {
            }
        });


        //section 2
        itemImg1 = (ImageView) findViewById(R.id.section2_item_img1);
        itemImg2 = (ImageView) findViewById(R.id.section2_item_img2);
        itemImg3 = (ImageView) findViewById(R.id.section2_item_img3);
        itemImg4 = (ImageView) findViewById(R.id.section2_item_img4);

        itemImg1.setTag(0);
        itemImg2.setTag(1);
        itemImg3.setTag(2);
        itemImg4.setTag(3);

        imageViewArrayList.add(0,itemImg1);
        imageViewArrayList.add(1,itemImg2);
        imageViewArrayList.add(2,itemImg3);
        imageViewArrayList.add(3,itemImg4);

        ///spinner=(Spinner)findViewById(R.id.sp)
        camera = (ImageView) findViewById(R.id.section2_img_camera);
        folder = (ImageView) findViewById(R.id.section2_img_folder);

        camera.setOnClickListener(camBtnListener);
        folder.setOnClickListener(galleryBtnListener);

        //section 3
        seekBar = (SeekBar)findViewById(R.id.seekBar1);
        final LinearLayout seekBarLi = (LinearLayout)findViewById(R.id.seekBar_layout);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                for(int i=0;i<seekBarLi.getChildCount();i++){
                    if(i!=progress){
                        CustomTextView temp = (CustomTextView)seekBarLi.getChildAt(i);
                        temp.setTextColor(oldColors);
                    }
                }
                CustomTextView temp2 =(CustomTextView)seekBarLi.getChildAt(progress);
                temp2.setTextColor(getResources().getColor(R.color.colorAccent));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });



        //section 4
        desEditText = (LimitedEditText) findViewById(R.id.section4_edit);
        desEditText.setMaxLines(5);
        desEditText.setMaxCharacters(250);
        final CustomTextView itemDesCharCount = (CustomTextView)findViewById(R.id.section4_char_count);
        desEditText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //This sets a textview to the current length
                itemDesCharCount.setText(String.valueOf(s.length()));
            }

            public void afterTextChanged(Editable s) {
            }
        });

        //section 5
        tagInput = (CustomEditText) findViewById(R.id.section5_edit);
        ImageView addTags = (ImageView) findViewById(R.id.section5_plus);
        addTags.setOnClickListener(addTagButtonListener);
        tagsCount = (CustomTextView)findViewById(R.id.section5_tag_count);
        oldColors =  tagsCount.getTextColors(); //save original colors

        //using section 6 (Choose a category)
        categories = getResources().getStringArray(R.array.category_array);
        spinner = (Spinner) findViewById(R.id.section6_spinner);
        spinnerAdapter = new CustomSpinnerAdapter(
                this, android.R.layout.simple_spinner_dropdown_item, Arrays.asList(getResources().getStringArray(R.array.category_array)));
        spinner.setAdapter(spinnerAdapter);


        //Filling item information for editing
        setItemInfoForEdit();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuItem item;

        item = menu.add("Save");
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        item = menu.add("Delete");
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);


        return super.onCreateOptionsMenu(menu);
    }
    String result;
    ProgressDialog pg;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getTitle().toString()){
            case "Save":

                final EditText tv=(EditText)findViewById(R.id.section1_edit);
                final    EditText desc=(EditText)findViewById(R.id.section4_edit);
                final     SeekBar s=(SeekBar)findViewById(R.id.seekBar1);
                final      int i=s.getProgress();
                final String title=tv.getText().toString();
                final String description=desc.getText().toString();
                final String cat=spinner.getSelectedItem().toString();

//pg.show();
                new AsyncTask<String,String,String>()
                {
                    boolean cancel;
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        if(title==null||title.trim().length()<=0)
                        {
                            new AlertDialog.Builder(EditListingActivity.this)
                                    .setTitle("Error")
                                    .setMessage("item title cannot be empty")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // continue with delete
                                        }
                                    })

                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                            cancel=true;
                            return;
                        }
                        if( getAllTagNames(tagFlowLayout).size()<=0)
                        {
                            new AlertDialog.Builder(EditListingActivity.this)
                                    .setTitle("Error")
                                    .setMessage("Please create atleast one tag")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // continue with delete
                                        }
                                    })

                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                            cancel=true;
                        }
                        else {
                            pg=ProgressDialog.show(EditListingActivity.this,"Please Wait","adding",false,false);
                            pg.setCancelable(false);
                            pg.setMessage("please wait..");
                        }
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        if(cancel)
                            return;
                        pg.dismiss();
                        Intent i = new Intent(getApplicationContext(),ListingProcessDoneActivity.class);
                        if(s.equals("success")){
                            i.putExtra("isSuccess", true);
                            startActivity(i);
                            finish();

                        }else{
                            i.putExtra("isSuccess", false);
                            startActivity(i);
                        }

                    }

                    @Override
                    protected String doInBackground(String... voids) {
                        if(cancel)
                            return null;
                        delete1(itemid) ;
                        try {
                            tags=getAllTagNames(tagFlowLayout);
                            String[] tagarray = new String[tags.size()];

                            for(int i=0;i<tags.size();i++)
                            {
                                tagarray[i]=tags.get(i);
                            }

                            //     int i=s.get;
                            SoapPrimitive r= RegisterWebService.sendDataEdit(title, description, tagarray, bits.toArray(), i, userdata.userid, cat, itemid);
                            result=r.getValue().toString();
                            int i=0;
                            if(bits.size()==0)
                            {
                                Drawable myDrawable = ContextCompat.getDrawable(EditListingActivity.this, R.drawable.sample_img);
                                Bitmap myLogo = ((BitmapDrawable) myDrawable).getBitmap();
                                bits.add(myLogo);
                            }

                            for(Bitmap b:bits)
                            {
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                b.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byte[] byteArray = stream.toByteArray();
                                sendDImage(Integer.parseInt(result),byteArray,i);

                                i++;
                            }
                            for(String t:tags)
                                sendtag(Integer.parseInt(result),t);
//
                            return "success";

                        } catch (Exception e) {
                            result=e.toString();
                            e.printStackTrace();
                            return "failed";

                        }

                    }
                }.execute(null, null);


                break;
            case "Delete":
new AsyncTask<String,String,String>(){
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(s.equals("success")){
            Intent i=new Intent(EditListingActivity.this,MyItemActivity.class);
            //i.putExtra("isSuccess", true);
            startActivity(i);
            finish();

        }else{
      //  Snackbar.make(android.)
        }
    }

    @Override
    protected String doInBackground(String... params) {
      SoapPrimitive res=  delete(itemid);
        if(res!=null)
        {
            return "success";
        }
        return "failure";
    }
}.execute();





                break;
        }

        return super.onOptionsItemSelected(item);

    }
    public SoapPrimitive sendDImage(int id,byte[] im,int pic)
    {
        SoapObject object = new SoapObject("http://webser/", "addimage");
        object.addProperty("itemid", id);
        object.addProperty("pic",pic);
        object.addProperty("image",im);
        return     MainWebService.getMsg(object, "http://services.tradepost.me:8084/TDserverWeb/AddItems?wsdl", "http://webser/AddItems/addimageRequest");
    }
    public SoapPrimitive sendtag(int id,String im)
    {
        SoapObject object = new SoapObject("http://webser/", "addtag");
        object.addProperty("itemid", id);

        object.addProperty("tag",im);
        return     MainWebService.getMsg(object, "http://services.tradepost.me:8084/TDserverWeb/AddItems?wsdl", "http://webser/AddItems/addtagRequest");
    }
    public SoapPrimitive delete(int itemid)
    {
        SoapObject soapObject=new SoapObject("http://webser/","delete");
        soapObject.addProperty("id",itemid);
        SoapPrimitive res= MainWebService.getMsg(soapObject, "http://services.tradepost.me:8084/TDserverWeb/EditdeleteItem?wsdl", "http://webser/EditdeleteItem/deleteRequest");

return res;
    }
    public void delete1(int itemid)
    {
        SoapObject soapObject=new SoapObject("http://webser/","deletetags");
        soapObject.addProperty("id",itemid);
        SoapPrimitive res= MainWebService.getMsg(soapObject, "http://services.tradepost.me:8084/TDserverWeb/EditdeleteItem?wsdl", "http://webser/EditdeleteItem/deletetagsRequest");


    }
    private void setItemInfoForEdit() {
        ArrayList<String> itemInfoForEdit = getIntent().getStringArrayListExtra("itemToEdit");

        //section1
        itemName.setText(itemInfoForEdit.get(1));
        itemid=Integer.parseInt(itemInfoForEdit.get(0));
        //section2
        final Thread setImageThread = new Thread() {

            @Override
            public void run() {
                try {
                    String[] imagesToEdit = getIntent().getStringArrayExtra("imageUrls");
                    for (String imageUrl : imagesToEdit) {
                        tempBitmap.add(Picasso.with(getApplicationContext()).load(Uri.parse(imageUrl)).get());
                    }
                } catch (IOException e) {
                    Log.d("thread", "is interrupted!" + e.toString());
                } finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < tempBitmap.size(); i++) {
                                imageViewArrayList.get(i).setImageBitmap(getResizedBitmap(tempBitmap.get(i), imageViewArrayList.get(i).getWidth(), imageViewArrayList.get(i).getHeight()));
                                imageViewArrayList.get(i).setOnClickListener(expandImgOnClick);
                                imageViewArrayList.get(i).setOnLongClickListener(deleteImgOnClick);
                            }
                        }
                    });
                }
            }
        };

        setImageThread.start();

        //section3
        seekBar.setProgress(convertCon(itemInfoForEdit.get(3)));
        //section4
        desEditText.setText(itemInfoForEdit.get(2));
        //section5
        String[] tagsToEdit = getIntent().getStringArrayExtra("tagsToEdit");
        for(String tag : tagsToEdit){
            addTagsEditListing(tag);
        }
        //section6
        spinner.setSelection(spinnerAdapter.getPosition(itemInfoForEdit.get(4)));


    }

    public int convertCon(String condition){
        int con=0;
        switch (condition){
            case "POOR":
                con=0;
                break;
            case "FAIR":
                con=1;
                break;
            case "GREAT":
                con=2;
                break;
            case "MINT":
                con=3;
                break;
            case "NEW":
                con=4;
                break;
        }

        return con;
    }

    public View.OnClickListener addTagButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (tagInput.getText().length() > 0 && tagFlowLayout.getChildCount() < MAX_NUM_TAGS) {
                singleTagLayout = (LinearLayout) View.inflate(getApplicationContext(), R.layout.layout_tag, null);
                singleTagLayout.setId(TAGS_COUNT);
                //Button for removing Tag
                ImageView cancelTag = (ImageView)singleTagLayout.findViewById(R.id.tag_cancel_btn);
                cancelTag.setId(TAGS_COUNT);
                cancelTag.setOnClickListener(tagCancelButtonListener);

                //Tag Name
                CustomTextView tagName = (CustomTextView) singleTagLayout.findViewById(R.id.tag_name);
                //tagName.setId(TAGS_COUNT);
                tagName.setText(tagInput.getText().toString().trim());
                //tags.add(tagInput.getText().toString().trim());
                tagName.setTag(tagInput.getText().toString());

                tagInput.setText("");
                TAGS_COUNT++;
                tagFlowLayout.addView(singleTagLayout);
                tagsCount.setText(String.valueOf(tagFlowLayout.getChildCount()));
                Log.d("Child Added", "Add 1, total: " + String.valueOf(tagFlowLayout.getChildCount()));

            }

        }
    };

    public View.OnClickListener tagCancelButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            tagFlowLayout.removeView((View) v.getParent());
            //tags.add(((TextView) v).getText().toString().trim());
            tagsCount.setText(String.valueOf(tagFlowLayout.getChildCount()));

            Log.d("Child Removed", "Remove 1, total: " + String.valueOf(tagFlowLayout.getChildCount()));


        }

    };

    public ArrayList<String> getAllTagNames(FlowLayout fl) {
        ArrayList<String> tagNames = new ArrayList<>();

        for (int i = 0; i < fl.getChildCount(); i++) {
            View child = fl.getChildAt(i);
            CustomTextView getTagName = (CustomTextView) (child.findViewById(R.id.tag_name));
            tagNames.add(getTagName.getText().toString().trim());
        }
        return tagNames;

    }

    public void addTagsEditListing(String tag) {

        LinearLayout singleTag = (LinearLayout) View.inflate(getApplicationContext(), R.layout.layout_tag, null);

        singleTag.setId(TAGS_COUNT);
        //Button for removing Tag
        ImageView cancelTag = (ImageView)singleTag.findViewById(R.id.tag_cancel_btn);
        cancelTag.setId(TAGS_COUNT);
        cancelTag.setOnClickListener(tagCancelButtonListener);

        //Tag Name
        CustomTextView tagName = (CustomTextView) singleTag.findViewById(R.id.tag_name);
        tagName.setText(tag.trim());
        tagName.setTag(tag);
        tagFlowLayout.addView(singleTag);
        tagsCount.setText(String.valueOf(tagFlowLayout.getChildCount()));

    }


    public int DpToPx(int requireDp ){
        int dpValue = requireDp; // margin in dips
        float d = getResources().getDisplayMetrics().density;
        int margin = (int)(dpValue * d); // margin in pixels
        return margin; // margin in pixels

    }

    public View.OnClickListener camBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Permission per = new Permission(EditListingActivity.this, null);
            if (per.isPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE) == 0 && per.isPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE) == 0) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File photo = null;
                try {
                    // place where to store camera taken picture
                    photo = createTemporaryFile("temp", ".jpg");
                    photo.delete();
                } catch (Exception e) {
                    Log.v("camera", "Can't create file to take picture!");
                    Toast.makeText(getApplicationContext(), "Please check SD card! Image shot is impossible!", Toast.LENGTH_SHORT);
                }
                mImageUri = Uri.fromFile(photo);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                //start camera intent

                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, requestCodeCam);
                }
            } else if (per.isPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE) == 1 && per.isPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE) == 1) {
                per.askPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

            }
            else{
                Snackbar.make(findViewById(android.R.id.content), "App needs Permission to access your external storage", Snackbar.LENGTH_LONG)
                        .setAction("Settings", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                            }
                        })
                        .setActionTextColor(Color.RED)
                        .show();

            }
        }


    };
    public void camera()
    {  Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = null;
        try {
            // place where to store camera taken picture
            photo = createTemporaryFile("temp", ".jpg");
            photo.delete();
        } catch (Exception e) {
            Log.v("camera", "Can't create file to take picture!");
            Toast.makeText(getApplicationContext(), "Please check SD card! Image shot is impossible!", Toast.LENGTH_SHORT);
        }
        mImageUri = Uri.fromFile(photo);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        //start camera intent

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, requestCodeCam);
        }

    }
    public void gallery()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select File"), requestCodeGal);

    }

    private File createTemporaryFile(String part, String ext) throws Exception
    {
        File tempDir= Environment.getExternalStorageDirectory();
        tempDir=new File(tempDir.getAbsolutePath()+"/.temp/");
        if(!tempDir.exists())
        {
            tempDir.mkdir();
        }
        return File.createTempFile(part, ext, tempDir);
    }

    public Bitmap grabImage() {
        this.getContentResolver().notifyChange(mImageUri, null);
        ContentResolver cr = this.getContentResolver();


        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //  android.provider.MediaStore.Images.Media.getBitmap(cr, mImageUri);
        BitmapFactory.decodeFile(mImageUri.getPath(), options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bm =          BitmapFactory.decodeFile(mImageUri.getPath(), options);;
        //  setImage(bm);

        return bm;
    }


    public View.OnClickListener galleryBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            /*

  gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg                                                                            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto , 0);
            */
            Permission per = new Permission(EditListingActivity.this, null);
            if (per.isPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE) == 0 && per.isPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE) == 0) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select File"), requestCodeGal);
            }
            else if (per.isPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE) == 1 && per.isPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE) == 1) {
                per.askPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},2);

            }
            else{
                Snackbar.make(findViewById(android.R.id.content), "App needs Permission to access your external storage", Snackbar.LENGTH_LONG)
                        .setAction("Settings", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        })
                        .setActionTextColor(Color.RED)
                        .show();

            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (resultCode == RESULT_OK) {
            if(requestCode ==0){
               /*
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                setImage(imageBitmap);
                bits.add(imageBitmap);
                */
                //setImage(grabImage());
                tempBitmap.add(grabImage());
                //       bits.add(grabImage());
                //... some code to inflate/create/find appropriate ImageView to place grabbed image

            }else if(requestCode == 1){
                Uri selectedImageUri = data.getData();
                String[] projection = { MediaStore.MediaColumns.DATA };
                Cursor cursor = getContentResolver().query(selectedImageUri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();

                String selectedImagePath = cursor.getString(column_index);

                Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                cursor.close();
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);
                //setImage(bm);
                tempBitmap.add(bm);

            }
        }

        for (int i = 0 ; i < tempBitmap.size() ; i++){
            imageViewArrayList.get(i).setImageBitmap(getResizedBitmap(tempBitmap.get(i),imageViewArrayList.get(i).getWidth(),imageViewArrayList.get(i).getHeight()));
            imageViewArrayList.get(i).setOnClickListener(expandImgOnClick);
            imageViewArrayList.get(i).setOnLongClickListener(deleteImgOnClick);
        }

    }

    private View.OnClickListener expandImgOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            zoomImageFromThumb(v, ((BitmapDrawable) ((ImageView) v).getDrawable()).getBitmap());

        }
    };

    private View.OnLongClickListener deleteImgOnClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {

            Toast.makeText(getApplicationContext(),"DELETED",Toast.LENGTH_SHORT).show();
            tempBitmap.remove(Integer.parseInt(v.getTag().toString()));

            for (int i = 0 ; i < imageViewArrayList.size(); i++){
                imageViewArrayList.get(i).setImageResource(R.mipmap.ic_image_thumbnail);
                imageViewArrayList.get(i).setOnClickListener(null);
                imageViewArrayList.get(i).setOnLongClickListener(null);
            }

            for (int i = 0; i < tempBitmap.size(); i++) {
                imageViewArrayList.get(i).setImageBitmap(getResizedBitmap(tempBitmap.get(i),imageViewArrayList.get(i).getWidth(),imageViewArrayList.get(i).getHeight()));
                imageViewArrayList.get(i).setOnClickListener(expandImgOnClick);
                imageViewArrayList.get(i).setOnLongClickListener(deleteImgOnClick);
            }



            return true;
        }
    };
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    /**
     * "Zooms" in a thumbnail view by assigning the high resolution image to a hidden "zoomed-in"
     * image view and animating its bounds to fit the entire activity content area. More
     * specifically:
     *
     * <ol>
     *   <li>Assign the high-res image to the hidden "zoomed-in" (expanded) image view.</li>
     *   <li>Calculate the starting and ending bounds for the expanded view.</li>
     *   <li>Animate each of four positioning/sizing properties (X, Y, SCALE_X, SCALE_Y)
     *       simultaneously, from the starting bounds to the ending bounds.</li>
     *   <li>Zoom back out by running the reverse animation on click.</li>
     * </ol>
     *
     * @param thumbView  The thumbnail view to zoom in.
     * @param image The high-resolution version of the image represented by the thumbnail.
     */
    public void zoomImageFromThumb(final View thumbView, Bitmap image) {
        // If there's an animation in progress, cancel it immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) findViewById(R.id.expanded_image);
        expandedImageView.setImageBitmap(image);

        final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        // Calculate the starting and ending bounds for the zoomed-in image. This step
        // involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail, and the
        // final bounds are the global visible rectangle of the container view. Also
        // set the container view's offset as the origin for the bounds, since that's
        // the origin for the positioning animation properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.listing_proecss_layout).getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final bounds using the
        // "center crop" technique. This prevents undesirable stretching during the animation.
        // Also calculate the start scaling factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation begins,
        // it will position the zoomed-in view in the place of the thumbnail.
        thumbView.setAlpha(0f);
        scrollView.setVisibility(View.GONE);
        toolbar.setVisibility(View.GONE);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations to the top-left corner of
        // the zoomed-in view (the default is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and scale properties
        // (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left,
                        finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top,
                        finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down to the original bounds
        // and show the thumbnail instead of the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel, back to their
                // original values.
                AnimatorSet set = new AnimatorSet();
                set
                        .play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView, View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView, View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        toolbar.setVisibility(View.VISIBLE);
                        expandedImageView.setVisibility(View.GONE);
                        scrollView.setVisibility(View.VISIBLE);


                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        toolbar.setVisibility(View.VISIBLE);
                        expandedImageView.setVisibility(View.GONE);
                        scrollView.setVisibility(View.VISIBLE);

                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }

}
