package com.example.web_developing_course;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Random;

public class ShoppingItemAdapter  extends RecyclerView.Adapter<ShoppingItemAdapter.ViewHolder> implements Filterable {

    private ArrayList<ShoppingItem> mShoppingItemsData;
    private ArrayList<ShoppingItem> mShoppingItemsDataAll;
    private Context mcontext;
    private int lastPosition = -1;

    public ShoppingItemAdapter(Context context, ArrayList<ShoppingItem> itemsData) {

        mShoppingItemsData = itemsData;
        mShoppingItemsDataAll = itemsData;
        mcontext = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mcontext).inflate(R.layout.list_items,parent,false));
    }

    @Override
    public void onBindViewHolder(ShoppingItemAdapter.ViewHolder holder, int position) {
        ShoppingItem currentItem = mShoppingItemsData.get(position);
        holder.bindTo(currentItem);
        Random rand = new Random();
        boolean randomBoolean = rand.nextBoolean();
        if(holder.getAdapterPosition() > lastPosition){
            Animation animation = randomBoolean ? AnimationUtils.loadAnimation(mcontext,R.anim.slide_in_row) : AnimationUtils.loadAnimation(mcontext,R.anim.slide_up);;
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mShoppingItemsData.size();
    }

    @Override
    public Filter getFilter() {
        return shoppingFilter;
    }
    private Filter shoppingFilter = new Filter(){

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<ShoppingItem> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if(charSequence == null || charSequence.length() == 0){
                results.count = mShoppingItemsDataAll.size();
                results.values = mShoppingItemsDataAll;
            }else{
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for(ShoppingItem item : mShoppingItemsDataAll){
                    if(item.getName().toLowerCase().contains(filterPattern) || item.getInfo().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
                results.count = filteredList.size();
                results.values = filteredList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mShoppingItemsData = (ArrayList) filterResults.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView mTitleText;
        private TextView mInfoText;
        private TextView mPriceText;
        private TextView mDateText;
        private RadioGroup mPaymentMethod;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitleText = itemView.findViewById(R.id.item_title);
            mInfoText= itemView.findViewById(R.id.item_description);
            mPriceText= itemView.findViewById(R.id.item_price);
            mDateText= itemView.findViewById(R.id.item_date);
            mPaymentMethod = itemView.findViewById(R.id.item_payment);

            itemView.findViewById(R.id.item_sign_in).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("Activity","Add to cart button clicked!");
                    ((ShopListActivity)mcontext).updateAlertIcon();
                }
            });
        }

        public void bindTo(ShoppingItem currentItem) {
            mTitleText.setText(currentItem.getName());
            mInfoText.setText(currentItem.getInfo());
            mPriceText.setText(currentItem.getPrice());
            mDateText.setText(currentItem.getDate());
            if(currentItem.isPaymentMethod()){
                mPaymentMethod.check(R.id.item_card);
            }else{
                mPaymentMethod.check(R.id.item_cash);
            }


        }
    }

}
