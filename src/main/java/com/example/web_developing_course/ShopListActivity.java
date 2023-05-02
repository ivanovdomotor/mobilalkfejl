package com.example.web_developing_course;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class ShopListActivity extends AppCompatActivity {
    private static final String LOG_TAG = ShopListActivity.class.getName();
    private FirebaseUser user;
    private RecyclerView mRecyclerView;
    private ArrayList<ShoppingItem> mItemList;
    private ShoppingItemAdapter mAdapter;
    private int gridNumber = 1;
    private boolean viewRow = false;
    private FrameLayout greenCircle;
    private TextView contentTextView;
    private int courseItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list);
        int SecretKey = getIntent().getIntExtra("SecretKey",0);
        if(SecretKey != 69 ) finish();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            Log.d(LOG_TAG,"Authenticated user!");
        }else{
            Log.d(LOG_TAG,"Anonim user!");
            finish();
        }
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this,gridNumber));
        mItemList = new ArrayList<>();
        mAdapter = new ShoppingItemAdapter(this,mItemList);
        mRecyclerView.setAdapter(mAdapter);
        initializeData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseAuth.getInstance().signOut();
    }

    private void initializeData() {
        String[] itemList = getResources().getStringArray(R.array.shopping_item_names);
        String[] itemInfo = getResources().getStringArray(R.array.shopping_item_info);
        String[] itemDate = getResources().getStringArray(R.array.shopping_item_date);
        int[] itemPrice = getResources().getIntArray(R.array.shopping_item_price);
        boolean itemPaymentMethod = getResources().getBoolean(R.bool.payment_method);

        mItemList.clear();

        for(int i = 0; i < itemList.length; i++){
            mItemList.add(new ShoppingItem(itemList[i],itemInfo[i],itemDate[i],itemPrice[i],itemPaymentMethod));
        }
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.shop_list_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(LOG_TAG,s);
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.logout_button:
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            case R.id.setting_button: return true;
            case R.id.courses: return true;
            case R.id.view_selector:
                if(viewRow){
                    changeSpanCount(item,R.drawable.baseline_view_module_24,1);
                }else{
                    changeSpanCount(item,R.drawable.baseline_view_stream_24,2);
                }
                return true;
            default: return super.onOptionsItemSelected(item);

        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        final MenuItem alertMenuItem = menu.findItem(R.id.courses);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();

        greenCircle = (FrameLayout) rootView.findViewById(R.id.view_alert_green_circle);
        contentTextView = (TextView) rootView.findViewById(R.id.view_alert_count_textview);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOptionsItemSelected(alertMenuItem);
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }

    private void changeSpanCount(MenuItem item, int drawableID, int i) {
        viewRow = ! viewRow;
        item.setIcon(drawableID);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(i);
    }

    public void updateAlertIcon(){
        courseItem = (courseItem + 1);
        if(0 < courseItem){
            contentTextView.setText(String.valueOf(courseItem));
        }else{
            contentTextView.setText("");
        }
    }
}