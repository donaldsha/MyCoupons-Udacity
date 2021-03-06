package com.example.mycoupons.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mycoupons.R;
import com.example.mycoupons.logger.DebugLog;
import com.example.mycoupons.model.Coupon;
import com.example.mycoupons.util.Utilities;

public class CouponListCursorAdapter extends RecyclerView.Adapter<CouponListCursorAdapter.ViewHolder> {
    private Cursor cursor;
    private Context context;

    private OnCursorCouponClickListener onCursorCouponClickListener;

    public CouponListCursorAdapter(Activity activity) {
        context = activity.getApplicationContext();
        onCursorCouponClickListener = (OnCursorCouponClickListener) activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item_coupon, parent, false);
        return new ViewHolder(cardView);
    }

    public void setCursor(Cursor cursor) {
        DebugLog.logMethod();
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        cursor.moveToPosition(position);
        Coupon coupon = Coupon.getCoupon(cursor);

        holder.merchant.setText(coupon.merchant);
        holder.category.setText(coupon.category);
        holder.validUntil.setText(Utilities.getStringDate(coupon.validUntil));
        holder.couponCode.setText(coupon.couponCode);

        // Coupon has been used
        if (coupon.state == 1) {
            holder.imageViewCouponState.setImageDrawable(
                    ContextCompat.getDrawable(context, R.drawable.ic_check_circle_green_24dp)
            );
            holder.textViewCouponState.setText(context.getString(R.string.coupon_state_used));
            holder.textViewCouponState.setTextColor(ContextCompat.getColor(context, R.color.material_green_700));
            return;
        }

        // Coupon is still valid
        if (coupon.validUntil >= Utilities.getLongDateToday()) {
            holder.imageViewCouponState.setImageDrawable(
                    ContextCompat.getDrawable(context, R.drawable.ic_add_shopping_cart_orange_25dp)
            );
            holder.textViewCouponState.setText(context.getString(R.string.coupon_state_available));
            holder.textViewCouponState.setTextColor(ContextCompat.getColor(context, R.color.material_orange_900));
        }
    }

    @Override
    public int getItemCount() {
        if (cursor == null) {
            return 0;
        }
        return cursor.getCount();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardView;

        TextView merchant;
        TextView category;
        TextView validUntil;
        TextView couponCode;

        ImageView imageViewCouponState;
        TextView textViewCouponState;

        public ViewHolder(CardView cardView) {
            super(cardView);
            this.cardView = cardView;
            this.cardView.setOnClickListener(this);

            merchant = (TextView) cardView.findViewById(R.id.text_view_merchant);
            category = (TextView) cardView.findViewById(R.id.text_view_category);
            validUntil = (TextView) cardView.findViewById(R.id.text_view_valid_until);
            couponCode = (TextView) cardView.findViewById(R.id.text_view_coupon_code);

            imageViewCouponState = (ImageView) cardView.findViewById(R.id.image_view_coupon_state);
            textViewCouponState = (TextView) cardView.findViewById(R.id.text_view_coupon_state);
        }

        @Override
        public void onClick(View v) {
            DebugLog.logMethod();
            if (onCursorCouponClickListener == null) {
                return;
            }
            int adapterPosition = getAdapterPosition();
            cursor.moveToPosition(adapterPosition);
            onCursorCouponClickListener.onCursorCouponClick(Coupon.getCoupon(cursor));
        }
    }

    public void releaseResources() {
        context = null;
        cursor = null;
        onCursorCouponClickListener = null;
    }

    public interface OnCursorCouponClickListener {
        void onCursorCouponClick(Coupon coupon);
    }
}
