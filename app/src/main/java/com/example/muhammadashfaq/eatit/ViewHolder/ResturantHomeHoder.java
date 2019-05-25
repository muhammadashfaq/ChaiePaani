package com.example.muhammadashfaq.eatit.ViewHolder;

import android.media.Image;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.muhammadashfaq.eatit.Interface.ItemClickListner;
import com.example.muhammadashfaq.eatit.R;

import static androidx.recyclerview.widget.RecyclerView.*;

public class ResturantHomeHoder extends RecyclerView.ViewHolder
{
    public TextView resturantName;
    public ImageView imgVuResturanctPic;


    public ResturantHomeHoder(@NonNull View itemView) {
        super(itemView);
        resturantName = itemView.findViewById(R.id.resturant_list_txt_vu);
        imgVuResturanctPic = itemView.findViewById(R.id.restuant_list_image);
    }
}

