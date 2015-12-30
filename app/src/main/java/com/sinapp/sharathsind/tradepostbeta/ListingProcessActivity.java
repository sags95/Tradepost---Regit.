package com.sinapp.sharathsind.tradepostbeta;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apmem.tools.layouts.FlowLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import CustomWidget.LimitedEditText;

/**
 * Created by HenryChiang on 2015-12-28.
 */
public class ListingProcessActivity extends AppCompatActivity {

    /**
     * Hold a reference to the current animator, so that it can be canceled mid-way.
     */
    private Animator mCurrentAnimator;

    /**
     * The system "short" animation time duration, in milliseconds. This duration is ideal for
     * subtle animations or animations that occur very frequently.
     */
    private int mShortAnimationDuration;



    private ImageView camera, folder, itemImg1, itemImg2, itemImg3, itemImg4;
    private ColorStateList oldColors;

    //section 2
    private final int REQUEST_CODE_CAM = 0;
    private final int REQUEST_CODE_GAL = 1;
    private Uri mImageUri;
    private int currentImgPos = 0;
    private ArrayList<Bitmap> tempBitmap = new ArrayList<>();
    private ArrayList<ImageView> imageViewArrayList = new ArrayList<>();
    final String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE","android.permission.READ_EXTERNAL_STORAGE"};
    final int permsRequestCode = 200;


    //section 5
    private EditText tagInput;
    private FlowLayout tagFlowLayout;
    private LinearLayout singleTagLayout;
    private final int MAX_NUM_TAGS = 5;
    private int TAGS_COUNT = 0;
    private TextView tagsCount;

    //section 6
    private Spinner spinner;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_process);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add Your Item");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if(Build.VERSION.SDK_INT> Build.VERSION_CODES.LOLLIPOP_MR1) {
            requestPermissions(perms, permsRequestCode);
        }

        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        //section 1
        LimitedEditText itemName = (LimitedEditText) findViewById(R.id.section1_edit);
        itemName.setMaxLines(1);
        itemName.setMaxCharacters(70);
        final TextView itemNameCharCount = (TextView)findViewById(R.id.section1_char_count);
        itemName.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //This sets a textview to the current length
                itemNameCharCount.setText(String.valueOf(s.length()));
            }
        });

        //section 2
        itemImg1 = (ImageView) findViewById(R.id.section2_item_img_1);
        itemImg2 = (ImageView) findViewById(R.id.section2_item_img_2);
        itemImg3 = (ImageView) findViewById(R.id.section2_item_img_3);
        itemImg4 = (ImageView) findViewById(R.id.section2_item_img_4);

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
        SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar1);
        final LinearLayout seekBarLi = (LinearLayout)findViewById(R.id.seekBar_layout);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                for (int i = 0; i < seekBarLi.getChildCount(); i++) {
                    if (i != progress) {
                        TextView temp = (TextView) seekBarLi.getChildAt(i);
                        temp.setTextColor(oldColors);
                    }
                }
                TextView temp2 = (TextView) seekBarLi.getChildAt(progress);
                temp2.setTextColor(getResources().getColor(R.color.colorAccent));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //section 4
        LimitedEditText desEditText = (LimitedEditText) findViewById(R.id.section4_edit);
        desEditText.setMaxLines(5);
        desEditText.setMaxCharacters(250);
        final TextView itemDesCharCount = (TextView)findViewById(R.id.section4_char_count);
        desEditText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //This sets a textview to the current length
                itemDesCharCount.setText(String.valueOf(s.length()));
            }
        });

        //section 5
        tagInput = (EditText) findViewById(R.id.section5_edit);
        tagFlowLayout = (FlowLayout) findViewById(R.id.section5_tags);
        tagInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == 6) {
                    if (tagInput.getText().length() > 0 && tagFlowLayout.getChildCount() < MAX_NUM_TAGS) {
                        singleTagLayout = (LinearLayout) View.inflate(getApplicationContext(), R.layout.layout_tag, null);
                        singleTagLayout.setId(TAGS_COUNT);
                        //Button for removing Tag
                        ImageView cancelTag = (ImageView) singleTagLayout.findViewById(R.id.tag_cancel_btn);
                        cancelTag.setId(TAGS_COUNT);
                        cancelTag.setOnClickListener(tagCancelButtonListener);

                        //Tag Name
                        TextView tagName = (TextView) singleTagLayout.findViewById(R.id.tag_name);
                        tagName.setText(tagInput.getText().toString().trim());
                        tagName.setTag(tagInput.getText().toString());

                        tagInput.setText("");
                        TAGS_COUNT++;
                        tagFlowLayout.addView(singleTagLayout);
                        tagsCount.setText(String.valueOf(tagFlowLayout.getChildCount()));
                        Log.d("Child Added", "Add 1, total: " + String.valueOf(tagFlowLayout.getChildCount()));

                    }        //  sendMessage();
                    handled = true;
                }
                return handled;
            }
        });

        tagInput.setOnKeyListener(tagOnKeyListener);
        ImageView addTags = (ImageView) findViewById(R.id.section5_plus);
        addTags.setOnClickListener(addTagButtonListener);
        tagsCount = (TextView)findViewById(R.id.section5_tag_count);
        oldColors =  tagsCount.getTextColors(); //save original colors

        //using section 6 (Choose a category)
        String[] categories = getResources().getStringArray(R.array.category_array);
        spinner = (Spinner) findViewById(R.id.section6_spinner);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, Arrays.asList(getResources().getStringArray(R.array.category_array)));
        spinner.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuItem item;

        item = menu.add("Post");
        item.setIcon(R.mipmap.ic_send);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        return super.onCreateOptionsMenu(menu);
    }

    private View.OnClickListener camBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File photo=null;
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
                startActivityForResult(takePictureIntent, REQUEST_CODE_CAM);
            }

        }
    };

    private File createTemporaryFile(String part, String ext) throws Exception {
        File tempDir= Environment.getExternalStorageDirectory();
        tempDir=new File(tempDir.getAbsolutePath()+"/.temp/");
        if(!tempDir.exists())
        {
            tempDir.mkdir();
        }
        return File.createTempFile(part, ext, tempDir);
    }

    private View.OnClickListener galleryBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        /*
        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , 0);
        */

            Intent intent = new Intent( Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_CODE_GAL);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (resultCode == RESULT_OK) {
            if(requestCode == 0){
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
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);
                //setImage(bm);
                tempBitmap.add(bm);
                cursor.close();
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
            zoomImageFromThumb(v, ((BitmapDrawable) ((ImageView)v).getDrawable()).getBitmap());

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

    public Bitmap grabImage() {
        this.getContentResolver().notifyChange(mImageUri, null);
        ContentResolver cr = this.getContentResolver();
        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //  android.provider.MediaStore.Images.Media.getBitmap(cr, mImageUri);
        BitmapFactory.decodeFile(mImageUri.getPath(), options);
        final int REQUIRED_SIZE = 400;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
            options.inSampleSize = scale;
            options.inJustDecodeBounds = false;
            bm = BitmapFactory.decodeFile(mImageUri.getPath(), options);
            ExifInterface ei = null;
            try {
                ei = new ExifInterface(mImageUri.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch(orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                bm = RotateBitmap(bm, 90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                bm = RotateBitmap(bm, 180);
                break;
        }
        //  setImage(bm);

        return bm;
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


    public void setImage(Bitmap imageBitmap){
        if(currentImgPos<5) {
            switch (currentImgPos) {
                case 0:
                    itemImg1.setImageBitmap(getResizedBitmap(imageBitmap,itemImg1.getWidth(),itemImg1.getHeight()));
                    break;
                case 1:
                    itemImg2.setImageBitmap(getResizedBitmap(imageBitmap,itemImg1.getWidth(),itemImg1.getHeight()));
                    break;
                case 2:
                    itemImg3.setImageBitmap(getResizedBitmap(imageBitmap,itemImg1.getWidth(),itemImg1.getHeight()));
                    break;
                case 3:
                    itemImg4.setImageBitmap(getResizedBitmap(imageBitmap,itemImg1.getWidth(),itemImg1.getHeight()));
                    break;
            }
            currentImgPos++;

        }
    }

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

    private View.OnClickListener addTagButtonListener = new View.OnClickListener() {

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
                TextView tagName = (TextView) singleTagLayout.findViewById(R.id.tag_name);
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

    private View.OnClickListener tagCancelButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            tagFlowLayout.removeView((View) v.getParent());
            //tags.add(((TextView) v).getText().toString().trim());
            tagsCount.setText(String.valueOf(tagFlowLayout.getChildCount()));

            Log.d("Child Removed", "Remove 1, total: " + String.valueOf(tagFlowLayout.getChildCount()));


        }

    };

    private View.OnKeyListener tagOnKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                if (tagInput.getText().length() > 0 && tagFlowLayout.getChildCount() < MAX_NUM_TAGS) {
                    singleTagLayout = (LinearLayout) View.inflate(getApplicationContext(), R.layout.layout_tag, null);
                    singleTagLayout.setId(TAGS_COUNT);
                    //Button for removing Tag
                    ImageView cancelTag = (ImageView)singleTagLayout.findViewById(R.id.tag_cancel_btn);
                    cancelTag.setId(TAGS_COUNT);
                    cancelTag.setOnClickListener(tagCancelButtonListener);

                    //Tag Name
                    TextView tagName = (TextView) singleTagLayout.findViewById(R.id.tag_name);
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

                return false;
            }

            return false;
        }
    };

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {

        switch (grantResults[0]) {

            case 0:
                Toast.makeText(getApplicationContext(),"granted",Toast.LENGTH_SHORT).show();
                break;
            case -1:
                Toast.makeText(getApplicationContext(),"denied and finish()",Toast.LENGTH_SHORT).show();
                finish();
                break;

        }
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
    private void zoomImageFromThumb(final View thumbView, Bitmap image) {
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
