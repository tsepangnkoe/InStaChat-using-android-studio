package com.example.instachat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText edtMessageInput;
    private TextView txtChattingWith;
    private ProgressBar progressBar;
    private ImageView image_toolbar,image_send;

    private MessageAdapter messageAdapter;
    private ArrayList<Message> messages;

    String usernameOfTheRoommate,emailOfRoommate,chatRoomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_activity);
        usernameOfTheRoommate=getIntent().getStringExtra("username_of_roommate");
        emailOfRoommate=getIntent().getStringExtra("email_of_roommate");

        recyclerView=findViewById(R.id.recyclerMessages);
        edtMessageInput=findViewById(R.id.edtText);
        progressBar=findViewById(R.id.progressMessages);
        txtChattingWith=findViewById(R.id.txt_chattingwith);
        image_toolbar=findViewById(R.id.image_toolbar);
        image_send=findViewById(R.id.image_send);

        messages=new ArrayList<>();
        messageAdapter=new MessageAdapter(messages,getIntent().getStringExtra("my_img"),getIntent().getStringExtra("image_of_roommate"),MessageActivity.this);
        txtChattingWith.setText(usernameOfTheRoommate);
        image_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference("messages/"+chatRoomId).push().setValue(new Message(FirebaseAuth.getInstance().getCurrentUser().getEmail(),emailOfRoommate,edtMessageInput.getText().toString()));
                edtMessageInput.setText("");
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);
        Glide.with(MessageActivity.this).load(getIntent().getStringExtra("image_of_roommate")).placeholder(R.drawable.account_img).error(R.drawable.account_img).into(image_toolbar);

        setUpChatRoom();

    }
    private void setUpChatRoom(){
        FirebaseDatabase.getInstance().getReference("user/"+ FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String myusername=snapshot.getValue(User.class).getUsername();
                if(usernameOfTheRoommate.compareTo(myusername)>0){
                    chatRoomId=myusername+usernameOfTheRoommate;

                }else if (usernameOfTheRoommate.compareTo(myusername)==0){
                    chatRoomId=myusername+usernameOfTheRoommate;
                }else{
                    chatRoomId=usernameOfTheRoommate+myusername;
                }
                attachMessageListener(chatRoomId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void attachMessageListener(String chatRoomId){
        FirebaseDatabase.getInstance().getReference("messages/"+chatRoomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    messages.add(dataSnapshot.getValue(Message.class));
                }

                messageAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messages.size()-1);
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}