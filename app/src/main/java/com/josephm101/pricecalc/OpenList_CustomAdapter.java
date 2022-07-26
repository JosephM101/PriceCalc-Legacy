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

import com.josephm101.pricecalc.ListFileHandler.Common.ListInstance;

import java.util.ArrayList;

@SuppressWarnings("ALL")
@SuppressLint("NonConstantResourceId")

public class OpenList_CustomAdapter extends ArrayAdapter<ListInstance> implements View.OnClickListener {
    final Context mContext;
    private final ArrayList<ListInstance> dataSet;

    public OpenList_CustomAdapter(ArrayList<ListInstance> data, Context context) {
        super(context, R.layout.open_list_row_item, data);
        this.dataSet = data;
        this.mContext = context;
    }

    public ArrayList<ListInstance> getDataSet() {
        return dataSet;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        // Object object = getItem(position);
        // DataModel dataModel = (DataModel) object;
        ListInstance dataModel = getItem(position);
        if (v.getId() != R.id.listUuid_row) {
            // throw new IllegalStateException("Unexpected value: " + v.getId());
        } else {
            // Do whatever you want here.
        }
/*        switch (v.getId()) {
            case R.id.itemName:
                //Do whatever you want here.
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        } */
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ListInstance dataModel = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        final View result;

        if (convertView == null) {
            //LayoutInflater inflater = LayoutInflater.from(parent.getContext().getApplicationContext());
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE); //Only method that supported app theme matching
            convertView = inflater.inflate(R.layout.open_list_row_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.friendlyName = convertView.findViewById(R.id.listFriendlyName_row);
            viewHolder.uuid = convertView.findViewById(R.id.listUuid_row);
            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        // Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.up_from_bottom);
        // animation.setFillAfter(true);
        // result.startAnimation(animation);
        viewHolder.friendlyName.setText(dataModel.getListFriendlyName());
        viewHolder.uuid.setText(dataModel.getUuid().toString());
        return convertView;
    }

    // View lookup cache
    private static class ViewHolder {
        TextView friendlyName;
        TextView uuid;
    }
}