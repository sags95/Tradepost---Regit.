package com.sinapp.sharathsind.tradepost;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import CustomWidget.CustomLinearLayoutManager;
import Model.DividerItemDecoration;
import Model.EmptyRecyclerView;

import Model.MyItems;
import Model.MyItemsAdapter;
import Model.RecyclerViewOnClickListener;
import Model.Variables;
import webservices.ItemWebService;

/**
 * Created by HenryChiang on 15-08-10.
 */
public class MyItemsFragment extends Fragment {

    private View rootView,emptyView;
    private EmptyRecyclerView mRecyclerView;
    private MyItemsAdapter myItemsAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CustomLinearLayoutManager mLayoutManager;
    private ArrayList<MyItems>  myItems  = new ArrayList<MyItems>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_items, container, false);
        emptyView = rootView.findViewById(R.id.myItems_emptyView);
        mRecyclerView = (EmptyRecyclerView)rootView.findViewById(R.id.myItems_recyclerview);

        userdata.i=new ArrayList<>();
        ArrayList<Integer> i1= ItemWebService.getItems(Constants.userid);
        for(Integer i:i1)
        userdata.i.add(ItemWebService.getItem(i));

        if(userdata.i!=null) {
            for (int i = 0; i < userdata.i.size(); i++) {
                MyItems myItem = new MyItems(userdata.i.get(i).item.getItemname(), userdata.i.get(i).item.getItemid(), userdata.userid);
                myItems.add(myItem);
            }
        }else{
            myItems = null;
        }

        //SwipeToRefresh
        mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.myItems_swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.ColorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("SwipeToRefresh", "Refreshing");
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (mRecyclerView == null || mRecyclerView.getChildCount() == 0) ?
                                0 : mRecyclerView.getChildAt(0).getTop();
                mSwipeRefreshLayout.setEnabled(mLayoutManager.findFirstVisibleItemPosition() == 0 && topRowVerticalPosition >= 0);
                super.onScrolled(recyclerView, dx, dy);
            }
        });



        myItemsAdapter = new MyItemsAdapter(getActivity().getApplicationContext(),myItems,myItemClickListener);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setSwipeRefreshLayout(mSwipeRefreshLayout);
        mRecyclerView.setEmptyView(emptyView);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerViewOnClickListener(getActivity(), new RecyclerViewOnClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                    }
                })
        );

        mRecyclerView.setAdapter(myItemsAdapter);
        applyLinearLayoutManager();


        return rootView;
    }

    private void applyLinearLayoutManager(){
        mLayoutManager = new CustomLinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    public View.OnClickListener myItemClickListener = new View.OnClickListener() {
        public  Calendar getDatePart(Date date){
            Calendar cal = Calendar.getInstance();       // get calendar instance
            cal.setTime(date);
            cal.set(Calendar.HOUR_OF_DAY, 0);            // set hour to midnight
            cal.set(Calendar.MINUTE, 0);                 // set minute in hour
            cal.set(Calendar.SECOND, 0);                 // set second in minute
            cal.set(Calendar.MILLISECOND, 0);            // set millisecond in second

            return cal;                                  // return the date part
        }

        public String daysBetween(Date startDate) {
            Calendar sDate = getDatePart(startDate);
            Calendar eDate = getDatePart(new Date());

            long daysBetween = 0;
            while (sDate.before(eDate)) {
                sDate.add(Calendar.DAY_OF_MONTH, 1);
                daysBetween++;
            }

            String daysOffset;
            if((int)daysBetween == 0){
                daysOffset = "Today";

            }else if((int)daysBetween == 1){
                daysOffset = (int)daysBetween + " day ago";

            }else{
                daysOffset = (int)daysBetween + " days ago";

            }
            return daysOffset;

        }

        @Override
        public void onClick(View v) {
            Log.d("child position", String.valueOf(mRecyclerView.getChildLayoutPosition(v)));


            Intent i = new Intent(getActivity(), SingleListingActivity.class);
            ArrayList<String> clickedItemDetails = new ArrayList<String>();

            i.putExtra("caller", "MyItem");

            clickedItemDetails.add(0, String.valueOf(userdata.i.get(mRecyclerView.getChildLayoutPosition(v)).item.getItemid()));
            clickedItemDetails.add(1, userdata.i.get(mRecyclerView.getChildLayoutPosition(v)).item.getItemname());
            clickedItemDetails.add(2, userdata.i.get(mRecyclerView.getChildLayoutPosition(v)).item.getUsername());
            clickedItemDetails.add(3, userdata.i.get(mRecyclerView.getChildLayoutPosition(v)).item.getDescription());
            clickedItemDetails.add(4, daysBetween(userdata.i.get(mRecyclerView.getChildLayoutPosition(v)).item.getDateadded()));
            clickedItemDetails.add(5, String.valueOf(userdata.i.get(mRecyclerView.getChildLayoutPosition(v)).item.getCon()));
            clickedItemDetails.add(6,userdata.i.get(mRecyclerView.getChildLayoutPosition(v)).item.getCategory());

            i.putStringArrayListExtra("myItemClicked", clickedItemDetails);

            i.putExtra("latitude", userdata.i.get(mRecyclerView.getChildLayoutPosition(v)).item.getLatitude());
            i.putExtra("longitude",userdata.i.get(mRecyclerView.getChildLayoutPosition(v)).item.getLongtitude());

            i.putExtra("itemImages", userdata.i.get(mRecyclerView.getChildLayoutPosition(v)).images);
            i.putExtra("itemTags",userdata.i.get(mRecyclerView.getChildLayoutPosition(v)).tags);

            //profile picture
            Bitmap b = Variables.getProfilepic();
            i.putExtra("profilePic",b);

            startActivity(i);
        }
    };
}
