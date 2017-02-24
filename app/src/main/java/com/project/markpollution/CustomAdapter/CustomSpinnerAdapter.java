package com.project.markpollution.CustomAdapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.markpollution.Objects.Category;
import com.project.markpollution.R;

import java.util.ArrayList;

/**
 * Created by Le Duc Thanh on 11/12/2016.
 * Developer Android
 * m.me/leducthanh93
 */

public class CustomSpinnerAdapter extends ArrayAdapter<Category> {

    private Activity activity;
    private ArrayList data;
    private int layotid;
    private Category tempValues = null;
    private LayoutInflater inflater;

    public CustomSpinnerAdapter(Activity activitySpinner,
                                int textViewResourceId,
                                ArrayList<Category> objects){
        super(activitySpinner, textViewResourceId, objects);
        activity = activitySpinner;
        data = objects;
    layotid = textViewResourceId;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        View row = inflater.inflate(R.layout.custom_spinner, parent, false);

        tempValues = null;
        tempValues = (Category) data.get(position);

        TextView label = (TextView) row.findViewById(R.id.tvSpinner);
        ImageView icon = (ImageView) row.findViewById(R.id.imvSpinner);
        label.setText(tempValues.getName());
        label.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
        icon.setImageResource(setIconCategory(tempValues.getId()));
        return row;
    }
    private int setIconCategory(String id){
        int idIcon =1;
        switch (id){
            case "1":
                idIcon = R.drawable.land_icon;
                break;
            case "2" :
                idIcon = R.drawable.water_icon;
                break;
            case "3":
                idIcon = R.drawable.air_icon;
                break;
            case "4" :
                idIcon = R.drawable.thermal_icon;
                break;
            case "5":
                idIcon = R.drawable.light_icon;
                break;
            case "6" :
                idIcon = R.drawable.noise_icon;
                break;
            default:
                idIcon = 1;
                break;

        }
        return idIcon;
    }
}