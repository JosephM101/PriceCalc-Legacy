package com.josephm101.pricecalc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

@SuppressLint("NonConstantResourceId")

public class CustomAdapter extends ArrayAdapter<DataModel> implements View.OnClickListener {
    Context mContext;
    private final ArrayList<DataModel> dataSet;
    private int lastPosition = -1;

    public CustomAdapter(ArrayList<DataModel> data, Context context) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext = context;
    }

    public ArrayList<DataModel> getDataSet() {
        return dataSet;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        //Object object = getItem(position);
        //DataModel dataModel = (DataModel) object;
        DataModel dataModel = getItem(position);
        if (v.getId() != R.id.itemName) {
            //throw new IllegalStateException("Unexpected value: " + v.getId());
        } else {//Do whatever you want here.
        }
/*        switch (v.getId()) {
            case R.id.itemName:
                //Do whatever you want here.
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }*/
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DataModel dataModel = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        final View result;

        if (convertView == null) {
            //LayoutInflater inflater = LayoutInflater.from(parent.getContext().getApplicationContext());
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE); //Only method that supported app theme matching
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.txtName = convertView.findViewById(R.id.itemName);
            viewHolder.txtPrice = convertView.findViewById(R.id.itemCost);
            viewHolder.txtDeductible = convertView.findViewById(R.id.isTaxable);
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
        String stringBuilder = "$" +
                dataModel.getItemPrice() +
                " (Quantity: " +
                dataModel.getItemQuantity() +
                ")";
        viewHolder.txtPrice.setText(stringBuilder);

        StringBuilder sb = new StringBuilder();
        sb.append("Is Taxable: ");
        sb.append(BooleanHandling.BoolToString(dataModel.getIsTaxable(), "Yes", "No"));
        if (dataModel.getIsTaxable()) {
            sb.append(" (");
            sb.append(PriceHandling.PriceToString(PriceHandling.getTaxCost(Double.parseDouble(dataModel.getItemPrice()), PriceHandling.getDefaultTaxRatePercentage(mContext.getApplicationContext()))));
            sb.append(")");
        }
        viewHolder.txtDeductible.setText(sb.toString());
        return convertView;
    }

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtPrice;
        TextView txtDeductible;
    }
}