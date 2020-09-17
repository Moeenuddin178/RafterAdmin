package com.job.rafteradmin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.job.rafteradmin.Modles.Jobs_Model;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class Images_Adapter extends RecyclerView.Adapter<Images_Adapter.MyViewHolder> {

    List<Jobs_Model> images;
    Context context;

    DatabaseReference  jobsDb= FirebaseDatabase.getInstance().getReference().child("Jobs");

    public Images_Adapter(Context context, List<Jobs_Model> images) {
        this.context = context;
        this.images = images;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_images, parent, false);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

//        byte[] decodedString = Base64.decode(images.get(position).getImage(), Base64.DEFAULT);
//        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        Glide.with(context).load(images.get(position).getImage()).into(holder.imageView);

        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Jobs_Model jobModel =images.get(position);
                Log.d("Check",jobModel.getJobid());
                jobsDb.child(jobModel.getJobid()).removeValue();
               Toast.makeText(context, "Job Deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }




    @Override
    public int getItemCount() {
        return images.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        Button btn_delete;
        public MyViewHolder(View itemView) {
            super(itemView);
            // get the reference of item view's
            imageView = itemView.findViewById(R.id.iv_images);
            btn_delete=  itemView.findViewById(R.id.btn_delete);
        }
    }

}
