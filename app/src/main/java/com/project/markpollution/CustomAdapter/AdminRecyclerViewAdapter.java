package com.project.markpollution.CustomAdapter;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.markpollution.Interfaces.OnItemClickListener;
import com.project.markpollution.Objects.PollutionPoint;
import com.project.markpollution.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Hung on 14-Nov-16.
 */

public class AdminRecyclerViewAdapter extends RecyclerView.Adapter<AdminRecyclerViewAdapter.AdminRecyclerViewHolder> {
    private Context context;
    private List<PollutionPoint> listPo;
    private List<Integer> listCountSpam;
    private OnItemClickListener onItemClickListener;

    public AdminRecyclerViewAdapter(Context context, List<PollutionPoint> listPo, List<Integer> listCountSpam) {
        this.context = context;
        this.listPo = listPo;
        this.listCountSpam = listCountSpam;
    }

    @Override
    public AdminRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recyclerview_admin, parent, false);
        return new AdminRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdminRecyclerViewHolder holder, int position) {
        final PollutionPoint curPo = listPo.get(position);
        int curSpam = listCountSpam.get(position);

        holder.tvTitle.setText(curPo.getTitle());
        holder.tvDesc.setText(curPo.getDesc());
        holder.tvTime.setText(formatDateTime(curPo.getTime()));
        Picasso.with(context).load(Uri.parse(curPo.getImage())).placeholder(R.drawable.placeholder).resize(200, 200)
                .centerCrop().into(holder.ivPicture);
        setIconCate(curPo.getId_cate(), holder.ivCate);
        holder.tvSpam.setText(Integer.toString(curSpam)+ " reported spam");

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(curPo);
            }
        };

        // Whenever click on one item. It passes pollution object into interface's method
        holder.container.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        return listPo.size();
    }

    class AdminRecyclerViewHolder extends RecyclerView.ViewHolder{
        ImageView ivCate, ivPicture;
        TextView tvTitle, tvDesc, tvTime, tvSpam;
        CardView container;

        public AdminRecyclerViewHolder(View itemView) {
            super(itemView);
            ivCate = (ImageView) itemView.findViewById(R.id.imageViewCateAdmin);
            ivPicture = (ImageView) itemView.findViewById(R.id.imageViewPictureAdmin);
            tvTitle = (TextView) itemView.findViewById(R.id.textViewTitleAdmin);
            tvDesc = (TextView) itemView.findViewById(R.id.textViewDescAdmin);
            tvTime = (TextView) itemView.findViewById(R.id.textViewTimeAdmin);
            tvSpam = (TextView) itemView.findViewById(R.id.textViewSpamAdmin);
            container = (CardView) itemView.findViewById(R.id.card_view_spam);
        }
    }

    private void setIconCate(String id_cate, ImageView ivHolder){
        switch (id_cate){
            case "1":
                ivHolder.setImageResource(R.drawable.land_icon);
                break;
            case "2" :
                ivHolder.setImageResource(R.drawable.water_icon);
                break;
            case "3":
                ivHolder.setImageResource(R.drawable.air_icon);
                break;
            case "4" :
                ivHolder.setImageResource(R.drawable.thermal_icon);
                break;
            case "5":
                ivHolder.setImageResource(R.drawable.light_icon);
                break;
            case "6" :
                ivHolder.setImageResource(R.drawable.noise_icon);
                break;
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

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
}
