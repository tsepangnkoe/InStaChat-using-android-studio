package com.example.instachat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class friendsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private String userID;
    private FirebaseAuth mAuth;
    private ArrayList<User> users;
    private ProgressBar progressBar;
    private UsersAdapter usersAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    UsersAdapter.OnUserClickListener onUserClickListener;
    String myImageUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        progressBar=findViewById(R.id.progressBar);
        users=new ArrayList<>();
        recyclerView=findViewById(R.id.recycler);
        swipeRefreshLayout=findViewById(R.id.swipeLayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUsers();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser use=mAuth.getCurrentUser();
        userID=use.getUid();

        onUserClickListener= new UsersAdapter.OnUserClickListener() {
            @Override
            public void onUserClicked(int position) {
                startActivity(new Intent(friendsActivity.this,MessageActivity.class)
                        .putExtra("username_of_roommate",users.get(position).getUsername())
                        .putExtra("email_of_roommate",users.get(position).getEmail())
                        .putExtra("image_of_roommate",users.get(position).getProfilepicture())
                        .putExtra("my_img",myImageUrl)

                );
                //Toast.makeText(friendsActivity.this, "Tapped on user" + users.get(position).getUsername(), Toast.LENGTH_SHORT).show();

            }
        };
        getUsers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.menu_item_profile){
            startActivity(new Intent(friendsActivity.this,Profile.class));
        }
        return super.onOptionsItemSelected(item);
    }
    private void getUsers(){
        users.clear();
        FirebaseDatabase.getInstance().getReference("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                    if(dataSnapshot.getValue(User.class).getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                        continue;
                    }
                    else{
                        users.add(dataSnapshot.getValue(User.class));
                    }



                }

                usersAdapter=new UsersAdapter(users,friendsActivity.this,onUserClickListener);
                recyclerView.setLayoutManager(new LinearLayoutManager(friendsActivity.this));
                recyclerView.setAdapter(usersAdapter);
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                for(User user :users){
                    if(user.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                        myImageUrl=user.getProfilepicture();
                        return ;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}