package com.example.muhammadashfaq.eatit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muhammadashfaq.eatit.Common.Common;
import com.example.muhammadashfaq.eatit.Database.Database;
import com.example.muhammadashfaq.eatit.Interface.ItemClickListner;
import com.example.muhammadashfaq.eatit.Model.Category;
import com.example.muhammadashfaq.eatit.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class HomeResturant extends AppCompatActivity {

    //Firebase refrence
    FirebaseDatabase firebaseDatabase;
    DatabaseReference category;

    FirebaseRecyclerAdapter<Category, MenuViewHolder> recyclerAdapter;
    SpinKitView spinKitView;

    //Recycler menu
    RecyclerView recyclerMenu;
    RecyclerView.LayoutManager layoutManager;
    Toolbar toolbar;

    // favorites
    Database localDB;
    public static final String TAG = Home.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_resturant);
        Toolbar toolbar = findViewById(R.id.toolbar);
        spinKitView=findViewById(R.id.spin_kit_home);

        if(getIntent()!=null){
            toolbar.setTitle(getIntent().getStringExtra("name")+" Menu");
        }else{
            toolbar.setTitle("Menu");
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Firebase init
        firebaseDatabase = FirebaseDatabase.getInstance();
        category = firebaseDatabase.getReference("Category");
        category.keepSynced(true);


        //Add to favrotes
        localDB=new Database(this);
        //Load menu

        recyclerMenu = findViewById(R.id.recycler_menu);
        recyclerMenu.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this,2);
        recyclerMenu.setLayoutManager(layoutManager);

        //Will get data for recyclerView from Firebase by using Adapter.
        if(Common.isConnectedtoInternet(getBaseContext())) {
            loadMenu();
        }else{
            Toast.makeText(HomeResturant.this, "Check your internet connection !!", Toast.LENGTH_SHORT).show();
            return;
        }
        
    }

    private void loadMenu() {
        spinKitView.setVisibility(View.VISIBLE);
        recyclerAdapter = new FirebaseRecyclerAdapter<Category,MenuViewHolder>
                (Category.class,R.layout.menu_layout, MenuViewHolder.class,category.getRef()) {
            @Override
            protected void populateViewHolder(final MenuViewHolder viewHolder, final Category model, final int position) {
                spinKitView.setVisibility(View.GONE);
                viewHolder.menuName.setText(model.getName());
                Picasso.get().load(model.getImage()).placeholder(R.drawable.placeholder)
                        .into(viewHolder.imageView);



                //Add to favorites
                //Add to Favorites
                if(localDB.isFavorite(recyclerAdapter.getRef(position).getKey())){
                    viewHolder.imageViewfavMenu.setImageResource(R.drawable.ic_favorite_black_24dp);
                }

                //change status of favorite
                viewHolder.imageViewfavMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!localDB.isFavorite(recyclerAdapter.getRef(position).getKey())){
                            localDB.addToFavorites(recyclerAdapter.getRef(position).getKey());
                            viewHolder.imageViewfavMenu.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(HomeResturant.this, " "+ model.getName()+" added to Favorties", Toast.LENGTH_SHORT).show();

                        }else{
                            localDB.removeFromFavorites(recyclerAdapter.getRef(position).getKey());
                            viewHolder.imageViewfavMenu.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(HomeResturant.this, " "+ model.getName()+" removed from Favorties", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                final Category clickitem = model;
                viewHolder.setItemClickListner(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        //Getting category id
                        Intent menuId = new Intent(HomeResturant.this, FoodList.class);
                        //Because we just want key to only item key will be send
                        menuId.putExtra("CategoryId", recyclerAdapter.getRef(position).getKey());
                        menuId.putExtra("CategoryName", model.getName());
                        startActivity(menuId);
                    }
                });


            }
        };
        recyclerAdapter.notifyDataSetChanged();
        recyclerMenu.setAdapter(recyclerAdapter);
    }
}
