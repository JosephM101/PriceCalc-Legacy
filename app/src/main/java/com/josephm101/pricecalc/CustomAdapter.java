package com.josephm101.pricecalc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<DataModel> implements View.OnClickListener {

    private ArrayList<DataModel> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtPrice;
        TextView txtDeductible;
    }

    public CustomAdapter(ArrayList<DataModel> data, Context context) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        Object object = getItem(position);
        DataModel dataModel = (DataModel) object;
        switch (v.getId()) {
            case R.id.itemName:
                //Do whatever you want here.
                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DataModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.itemName);
            viewHolder.txtPrice = (TextView) convertView.findViewById(R.id.itemCost);
            viewHolder.txtDeductible = (TextView) convertView.findViewById(R.id.isTaxable);
            //viewHolder.txtTotal = (TextView) convertView.findViewById(R.id.itemCostTotal);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.up_from_bottom);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtName.setText(dataModel.getItemName());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("$");
        stringBuilder.append(dataModel.getItemPrice());
        stringBuilder.append(" (Quantity: ");
        stringBuilder.append(dataModel.getItemQuantity());
        stringBuilder.append(")");
        viewHolder.txtPrice.setText(stringBuilder.toString());
        viewHolder.txtDeductible.setText("Is Taxable: " + BooleanHandling.BoolToString(dataModel.getIsTaxable(), "Yes", "No"));
        return convertView;
    }
}