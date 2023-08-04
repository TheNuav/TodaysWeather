package com.example.todayssweather;
import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder>{
    Context context;
    ArrayList<WeatherActivity> WeatherActivityArrayList;

    public WeatherAdapter(Context context, ArrayList<WeatherActivity> weatherActivityArrayList) {
        this.context = context;
        this.WeatherActivityArrayList = weatherActivityArrayList;
    }

    @NonNull
    @Override
    public WeatherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherAdapter.ViewHolder holder, int position) {
        WeatherActivity modal= WeatherActivityArrayList.get(position);
        holder.temperaturetv.setText(modal.getTemperature()+"°C");
        Picasso.get().load("http://".concat(modal.getIcon())).into(holder.conditionIv);
        holder.windTV.setText(modal.getWindspeed()+"Kmph");
        SimpleDateFormat input =new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output =new SimpleDateFormat("yyyy-MM-dd hh:mm");
        try{
            Date t=input.parse(modal.getTime());
            holder.timetv.setText(output.format(t));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return WeatherActivityArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView    windTV,temperaturetv,timetv;
        ImageView conditionIv;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            windTV=itemView.findViewById(R.id.idTVWinspeed);
            temperaturetv=itemView.findViewById(R.id.idTVtemperature);
            timetv=itemView.findViewById(R.id.idTVtime);
            conditionIv= itemView.findViewById(R.id.idTVcondition);
        }
    }
}
