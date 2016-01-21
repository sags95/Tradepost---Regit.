package com.sinapp.sharathsind.tradepost;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.ExceptionReporter;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

import org.apmem.tools.layouts.FlowLayout;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import CustomWidget.CustomPagerAdapter;
import CustomWidget.CustomTextView;
import Model.MarketPlaceData;
import Model.Variables;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by HenryChiang on 15-06-06.
 */
public class SingleListingActivity extends AppCompatActivity {

    private CustomPagerAdapter mCustomPagerAdapter;
    private ViewPager mViewPager;
    private List<ImageView> dots;
    private Animator mCurrentAnimator;
    private FloatingActionButton offerFab;
    private CustomTextView itemTitle,itemDescription,itemCondition,itemDateAdded,itemUsername,itemDistance;
    private FlowLayout tagsLayout;
    private CircleImageView proPic;
    private ImageView favouriteItemStatus;
    private int mShortAnimationDuration;


    private RelativeLayout singleListingHeader;

    private boolean isSelfItem = false;
    private String[] itemTagsToEdit=null;
    private String itemCatToEdit = "";
    private String[] imageResources;
    private String itemId="";
    private String[] images;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_single_listing_view);
        View includeView = (View)findViewById(R.id.single_listing_main_layout);
        //GCMService.b=true;
        Tracker t =
                ((TradePost) getApplication()).getDefaultTracker();
        Thread.UncaughtExceptionHandler myHandler = new ExceptionReporter(
                t,                                        // Currently used Tracker.
                Thread.getDefaultUncaughtExceptionHandler(),      // Current default uncaught exception handler.
                this);
        //single listing header
        singleListingHeader= (RelativeLayout)findViewById(R.id.single_listing_header);
//        singleListingHeader.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
//                ArrayList<String> itemProfileClicked = new ArrayList<>();
//                itemProfileClicked.add(0, String.valueOf(m.item.item.getUserid()));
//                itemProfileClicked.add(1, m.proUsername);
//                i.putStringArrayListExtra("itemProfileClicked", itemProfileClicked);
//
//                //
//                i.putExtra("caller","SingleListingActivity");
//                Bitmap b = ((BitmapDrawable)proPic.getDrawable()).getBitmap();
//                i.putExtra("profilePic", b);
//
//                startActivity(i);
//            }
//        });

        //item information
        proPic = (CircleImageView)singleListingHeader.findViewById(R.id.single_listing_header_userImg);
        itemTitle = (CustomTextView)singleListingHeader.findViewById(R.id.single_listing_header_itemTitle);
        itemCondition = (CustomTextView)includeView.findViewById(R.id.single_listing_item_condition);
        itemDescription = (CustomTextView)includeView.findViewById(R.id.single_listing_des_input);
        itemDateAdded = (CustomTextView)singleListingHeader.findViewById(R.id.single_listing_header_time);
        tagsLayout = (FlowLayout)includeView.findViewById(R.id.single_listing_tags);
        itemUsername = (CustomTextView)singleListingHeader.findViewById(R.id.single_listing_header_username);
        itemDistance = (CustomTextView)singleListingHeader.findViewById(R.id.single_listing_header_distance);

        //favouriteItemStatus.setOnClickListener(addedTofavourite);
        //floating action button
        //offerFab = (FloatingActionButton)findViewById(R.id.offer_fab2);
        offerFab = (FloatingActionButton)includeView.findViewById(R.id.fab);
        offerFab.setVisibility(View.GONE);
        //offerFab.setOnClickListener(offerFabOnClickListener);


        Intent i = getIntent();

        if(getIntent().getStringExtra("caller").equals("MyItem")){
            ArrayList<String> itemInfo = getIntent().getStringArrayListExtra("myItemClicked");
            Bitmap proPicReceived = i.getParcelableExtra("profilePic");

            //item id
            itemId=itemInfo.get(0);
            //item userPic
            Picasso.with(this).load(Uri.parse("http://services.tradepost.me:8084/TDserverWeb/images/" + userdata.userid + "/profile.png")).into(proPic);
            //proPic.setImageBitmap(proPicReceived);
            //item title
            itemTitle.setText(itemInfo.get(1));
            //item description
            itemDescription.setText(itemInfo.get(3));
            //item condition
            itemCondition.setText(setCondition(Integer.parseInt(itemInfo.get(5))));
            //item dateAdded
            itemDateAdded.setText(itemInfo.get(4));
            //item username
            itemUsername.setText(Variables.username);
//            itemUsername.setText(itemInfo.get(2));
            //item distance
//            itemDistance.setText(String.valueOf(roundedDistance(distance(userdata.mylocation.latitude,userdata.mylocation.Longitude, userdata.mylocation.latitude, userdata.mylocation.Longitude, 'K'))));

            String[] itemImages = getIntent().getStringArrayExtra("itemImages");
            images = new String[itemImages.length];

            for(int j=0;j<itemImages.length;j++){
                images[j]="http://services.tradepost.me:8084/TDserverWeb/images/items/" + itemInfo.get(0) +"/"+ itemImages[j];

            }
            String[] itemTags = getIntent().getStringArrayExtra("itemTags");

            //For EditActivity
            itemTagsToEdit = new String[itemTags.length];
            itemTagsToEdit=itemTags;
            itemCatToEdit = itemInfo.get(6);

            imageResources = new String[itemImages.length];
            mCustomPagerAdapter = new CustomPagerAdapter(this,images,expandImgOnClick);
            mViewPager = (ViewPager) findViewById(R.id.pager);
            mViewPager.setAdapter(mCustomPagerAdapter);
if(itemTags==null)
    itemTags=new String[]{"notag"};
            for (String tempTag : itemTags) {
                tagsLayout.addView(addTagsSingleListing(tempTag));
            }

            offerFab.setVisibility(View.GONE);
            isSelfItem=true;



        }

        /*
        imageResources =new Bitmap[m.image.length];
        for(int j=0;j<m.image.length;j++){
            try {
                String s= m.image[j];
                URL url = new URL(s);
                URLConnection con = url.openConnection();
                con.setRequestProperty("connection","close");
                InputStream is = con.getInputStream();
                imageResources[j] = BitmapFactory.decodeStream(is);
                is.close();
                ((HttpURLConnection)con).disconnect();

            }catch (Exception e){
            }
        }
        */
        //image slider viewer
        //mCustomPagerAdapter = new CustomPagerAdapter(this,m.image);

        //setup actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Item");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        addDots();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item;

        //put logic here
        if(isSelfItem){
            item = menu.add("Edit");
            item.setIcon(R.mipmap.ic_action_edit);
            MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        }


        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getTitle().toString().equals("Edit")){
            Toast.makeText(getApplicationContext(), "Edit", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(),EditListingActivity.class);
            //General Information\

            ArrayList<String> editListing = new ArrayList<>();
            editListing.add(0,itemId);
            editListing.add(1,itemTitle.getText().toString());
            editListing.add(2,itemDescription.getText().toString());
            editListing.add(3,itemCondition.getText().toString());
            editListing.add(4, itemCatToEdit);

            i.putStringArrayListExtra("itemToEdit", editListing);

            //Tags
            i.putExtra("tagsToEdit", itemTagsToEdit);

            //Images
            i.putExtra("imageUrls", images);

            startActivity(i);
            finish();



        }

        return super.onOptionsItemSelected(item);
    }

    public void addDots() {
        dots = new ArrayList<>();
        LinearLayout dotsLayout = (LinearLayout)findViewById(R.id.dots);

        if(imageResources != null) {
            for (int i = 0; i < imageResources.length; i++) {
                ImageView dot = new ImageView(this);
                if (i == 0) {
                    dot.setImageDrawable(getResources().getDrawable(R.mipmap.pager_dot_selected));
                } else {
                    dot.setImageDrawable(getResources().getDrawable(R.mipmap.pager_dot_not_selected));
                }
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(3, 0, 3, 0);
                dotsLayout.addView(dot, params);
                dots.add(dot);
            }

            mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    selectDot(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        }
    }

    public void selectDot(int idx) {
        Resources res = getResources();

        if(imageResources != null) {
            for (int i = 0; i < imageResources.length; i++) {
                int drawableId = (i == idx) ? (R.mipmap.pager_dot_selected) : (R.mipmap.pager_dot_not_selected);
                Drawable drawable = res.getDrawable(drawableId);
                dots.get(i).setImageDrawable(drawable);
            }
        }
    }

//    public View.OnClickListener offerFabOnClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            Intent intent = new Intent(getApplicationContext(), OfferProcessActivity.class);
//            Intent i = getIntent();
//            ArrayList<String> itemDetails = i.getStringArrayListExtra("itemClicked");
//            ArrayList<String> items = new ArrayList<String>();
//            items.add(0,String.valueOf(m.item.item.getItemid()));
//            items.add(1,String.valueOf(m.item.item.getItemname()));
//            items.add(2,String.valueOf(m.item.item.getUserid()));
//            intent.putStringArrayListExtra("itemToOfferProcess", items);
//            startActivity(intent);
//
//        }
//    };

//    public View.OnClickListener addedTofavourite = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            if(m.isFav) {
//                //   view.setEnabled(false);
//                new AsyncTask<Void,Void,Void>()
//                {
//                    @Override
//                    protected void onPostExecute(Void aVoid) {
//
//                        super.onPostExecute(aVoid);
//                        m.isFav=false;
//                        favouriteItemStatus.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_not_selected));
//
//                    }
//
//                    @Override
//                    protected Void doInBackground(Void... params) {
//                        Cursor c= Constants.db.rawQuery("select * from fav where itemid="+m.item.item.getItemid(),null);
//                        c.moveToFirst();
//                        FavouriteWebService.removefavouInts(c.getInt(c.getColumnIndex("id")));
//                        c.close();
//                        return null;
//                    }
//                }.execute(null,null);
//            }
//            else {
//                //     view.setEnabled(false);
//
//                new AsyncTask<Void,Void,Void>()
//                {
//                    @Override
//                    protected void onPostExecute(Void aVoid) {
//                        super.onPostExecute(aVoid);
//                        m.isFav=true;
//                        favouriteItemStatus.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_selected));
//                    }
//                    @Override
//                    protected Void doInBackground(Void... params) {
//                        FavouriteWebService.add(m.item.item.getItemid());
//                        return null;
//                    }
//                }.execute(null, null);
//            }
//
//        }
//    };


    public String setCondition(int condition){
        String temp ="";
        switch (condition){
            case 0:
                temp = "POOR";
                break;
            case 1:
                temp = "FAIR";
                break;
            case 2:
                temp = "GREAT";
                break;
            case 3:
                temp = "MINT";
                break;
            case 4:
                temp = "NEW";
                break;
        }

        return temp;
    }

    public CustomTextView addTagsSingleListing(String tag) {

        CustomTextView newTag = new CustomTextView(getApplicationContext());
        newTag.setText(tag.toUpperCase());
        newTag.setTextColor(getResources().getColor(android.R.color.white));
        newTag.setClickable(true);
        newTag.settingOpenSansLight();
        newTag.setBackgroundResource(R.drawable.tag_btn_shape);
        FlowLayout.LayoutParams lp = new FlowLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0,DpToPx(8), DpToPx(8),0);

        newTag.setLayoutParams(lp);



        return newTag;

    }


    public int DpToPx(int requireDp ){
        float d = getResources().getDisplayMetrics().density;
        return (int)(requireDp * d); // margin in pixels

    }

    private double distance(double lat1, double lon1, double lat2, double lon2, char unit) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);

        dist = rad2deg(dist);

        dist = dist * 60 * 1.1515;

        if (unit == 'K') {

            dist = dist * 1.609344;

        } else if (unit == 'N') {

            dist = dist * 0.8684;

        }

        return (dist);

    }

    private double deg2rad(double deg) {

        return (deg * Math.PI / 180.0);

    }

    private double rad2deg(double rad) {

        return (rad * 180 / Math.PI);

    }

    private double roundedDistance(double distance){
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(distance));
    }

    public void zoomImageFromThumb(final View thumbView, Bitmap image) {
        // If there's an animation in progress, cancel it immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) findViewById(R.id.expanded_image);
        expandedImageView.setImageBitmap(image);

        final ScrollView scrollView = (ScrollView) findViewById(R.id.single_listing_scroll);
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
        findViewById(R.id.single_view).getGlobalVisibleRect(finalBounds, globalOffset);
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

    private View.OnClickListener expandImgOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            zoomImageFromThumb(v, ((BitmapDrawable) ((ImageView)v).getDrawable()).getBitmap());

        }
    };
}
