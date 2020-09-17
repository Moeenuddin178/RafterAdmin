package com.job.rafteradmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.job.rafteradmin.Modles.Jobs_Model;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton add_jobs;
    RecyclerView recyclerView;
    Images_Adapter recyclerViewAdapter;
    ProgressBar progressbar;
    LinearLayout data,nodata;

    List<Jobs_Model> itemsList;
    DatabaseReference jobsDb;
    Jobs_Model jobModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add_jobs=findViewById(R.id.add_jobs);
        progressbar=findViewById(R.id.progressbar);
        data=findViewById(R.id.data);
        nodata=findViewById(R.id.nodata);
        jobsDb= FirebaseDatabase.getInstance().getReference().getRoot();



        add_jobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,Upload_Activirty.class));

            }
    });



        itemsList = new ArrayList<>();
        recyclerView = findViewById(R.id.rv_images);

        // set a LinearLayoutManager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL); // set Horizontal Orientation
        recyclerView.setLayoutManager(linearLayoutManager);

          dbHasChild();

        recyclerViewAdapter = new Images_Adapter(this, itemsList);
        recyclerView.setAdapter(recyclerViewAdapter);


    }

   // Check if Root Ref() of database has child-----------------------------------------------------
    public void dbHasChild(){

        jobsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("Jobs")){
                    nodata.setVisibility(View.GONE);
                    data.setVisibility(View.VISIBLE);
                    getData();
                }
                else {
                    progressbar.setVisibility(View.GONE);
                    data.setVisibility(View.GONE);
                    nodata.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public  void getData(){

        jobsDb.child("Jobs").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){

                    jobModel=snapshot.getValue(Jobs_Model.class);
                    itemsList.add(jobModel);
                    recyclerViewAdapter.notifyDataSetChanged();
                    nodata.setVisibility(View.GONE);
                    data.setVisibility(View.VISIBLE);
                    progressbar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String key=snapshot.getKey();

                for(int i=0;i<itemsList.size();i++){
                    jobModel=itemsList.get(i);
                    if(jobModel.getJobid().equals(key)){
                        itemsList.remove(i);
                    }

                }

                recyclerView.setAdapter(recyclerViewAdapter);
                recyclerViewAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}