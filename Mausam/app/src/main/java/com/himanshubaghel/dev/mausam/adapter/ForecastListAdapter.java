package com.himanshubaghel.dev.mausam.adapter;


import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.himanshubaghel.dev.mausam.R;
import com.himanshubaghel.dev.mausam.model.Forecast;
import com.himanshubaghel.dev.mausam.utils.Constants;

import java.util.List;

public class ForecastListAdapter extends RecyclerView.Adapter<ForecastListAdapter.CustomViewHolder>{

    List<Forecast> forecastList;

    private Context context;

    public ForecastListAdapter(List<Forecast> forecastList, Context context){
        this.forecastList = forecastList;
        this.context = context;
    }

    @Override
    public ForecastListAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.forecast_item_layout, viewGroup, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ForecastListAdapter.CustomViewHolder holder, int position) {
        Forecast forecastItem = forecastList.get(position);
        holder.tvDay.setText(forecastItem.getDay());
        holder.tvMinTemp.setText(forecastItem.getLow()+(char) 0x00B0);
        holder.tvMaxTemp.setText(forecastItem.getHigh()+(char) 0x00B0);
        Uri imageUri = Uri.parse(Constants.weatherIconBaseUrl + forecastItem.getCode()+".gif");
        holder.ivIcon.setImageURI(imageUri);
    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        TextView tvMaxTemp;
        TextView tvMinTemp;
        SimpleDraweeView ivIcon;
        TextView tvDay;
        public CustomViewHolder(View itemView) {
            super(itemView);
            tvDay = (TextView) itemView.findViewById(R.id.tvDay);
            tvMaxTemp = (TextView) itemView.findViewById(R.id.tvMaxTemp);
            tvMinTemp = (TextView) itemView.findViewById(R.id.tvMinTemp);
            ivIcon = (SimpleDraweeView) itemView.findViewById(R.id.ivIcon);
        }
    }
}
