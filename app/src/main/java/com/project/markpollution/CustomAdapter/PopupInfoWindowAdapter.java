package com.project.markpollution.CustomAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.project.markpollution.Objects.PollutionPoint;
import com.project.markpollution.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Hung on 19-Oct-16.
 */

public class PopupInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private View view = null;
    private LayoutInflater inflater;
    private HashMap<String, PollutionPoint> hashMapMarkers;
    private Context context;
    private Marker lastMarker = null;
    private String url_CountCommentByPo = "http://indi.com.vn/dev/markpollution/CountCommentByPo.php?id_po=";
    private String url_SumRateByPo = "http://indi.com.vn/dev/markpollution/SumRateByPo.php?id_po=";

    public PopupInfoWindowAdapter(Context context, LayoutInflater inflater, HashMap<String, PollutionPoint> hashMapMarkers) {
        this.context = context;
        this.inflater = inflater;
        this.hashMapMarkers = hashMapMarkers;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getInfoContents(Marker marker) {
        if (view == null) {
            view =inflater.inflate(R.layout.custom_info_window, null);
        }

        if (lastMarker == null || !lastMarker.getId().equals(marker.getId())) {
            lastMarker=marker;

            TextView tvTitle =(TextView) view.findViewById(R.id.tvTitle_InfoWindow);
            TextView tvDesc =(TextView) view.findViewById(R.id.tvDesc_InfoWindow);
            TextView tvTime =(TextView) view.findViewById(R.id.tvTime_InfoWindow);
            TextView tvComment =(TextView) view.findViewById(R.id.tvComment_InfoWindow);
            TextView tvRate = (TextView) view.findViewById(R.id.tvRate_InfoWindow);
            ImageView icon=(ImageView) view.findViewById(R.id.ivPicture_InfoWindow);

            PollutionPoint po = hashMapMarkers.get(marker.getId());

            tvTitle.setText(marker.getTitle());
            tvDesc.setText(marker.getSnippet());
            tvTime.setText(formatDateTime(po.getTime()));
            setCountCommentByPo(po.getId(), tvComment);
            setSumRateByPo(po.getId(), tvRate);

            Picasso.with(context).load(Uri.parse(po.getImage()))
                    .noFade()
                    .placeholder(R.drawable.placeholder)
                    .into(icon, new MarkerCallback(marker));
        }

        return(view);
    }

    static class MarkerCallback implements Callback {
        Marker marker=null;

        MarkerCallback(Marker marker) {
            this.marker=marker;
        }

        @Override
        public void onError() {
            Log.e(getClass().getSimpleName(), "Error loading thumbnail!");
        }

        @Override
        public void onSuccess() {
            if (marker != null && marker.isInfoWindowShown()) {
                marker.showInfoWindow();
            }
        }
    }

    private String formatDateTime(String time){
        SimpleDateFormat originFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat resultFormat = new SimpleDateFormat("hh:mm:ss dd/MM/yyyy");

        Date datetime = null;
        try {
            datetime = originFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return resultFormat.format(datetime);
    }

        private void setCountCommentByPo(String id_po, final TextView tvHolder){
        StringRequest strReq = new StringRequest(Request.Method.GET, url_CountCommentByPo +
                id_po, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.equals("Count comment failure")){
                    tvHolder.setText(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        Volley.newRequestQueue(context).add(strReq);
    }

    private void setSumRateByPo(String id_po, final TextView tvHolder){
        StringRequest strReq = new StringRequest(Request.Method.GET, url_SumRateByPo + id_po , new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        tvHolder.setText(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        Volley.newRequestQueue(context).add(strReq);
    }
}
