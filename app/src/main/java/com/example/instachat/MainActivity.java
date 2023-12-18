package com.example.instachat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private EditText edtusername ,edtPassword,edtEmail;
    private Button btnsubmit;
    private TextView txtLogInfo;

    private boolean isSigningUp=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtusername=findViewById(R.id.edtusername);
        edtPassword=findViewById(R.id.edtPassword);
        edtEmail=findViewById(R.id.edtEmail);

        btnsubmit=findViewById(R.id.btnsubmit);
        txtLogInfo =findViewById(R.id.txtLoginInfo);
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            startActivity(new Intent(MainActivity.this,friendsActivity.class));
            finish();
        }
        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtEmail.getText().toString().isEmpty()||edtPassword.getText().toString().isEmpty()){
                    if(isSigningUp&&edtusername.getText().toString().isEmpty()){
                        Toast.makeText(MainActivity.this, "Invalid input ", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }

                if(isSigningUp){
                    handleSignUp();
                }else{
                    handleLogin();
                }
            }
        });

        txtLogInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isSigningUp){
                    isSigningUp=false;
                    btnsubmit.setText("Login");
                    edtusername.setVisibility(View.GONE);
                    txtLogInfo.setText("Don't have an account ? Sign Up");
                }else{
                    isSigningUp=true;
                    btnsubmit.setText("Sign Up");
                    edtusername.setVisibility(View.VISIBLE);
                    txtLogInfo.setText("Already have an account? Login ");

                }
            }
        });


    }
    private void handleSignUp(){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference("user/"+FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(new User(edtusername.getText().toString(),edtEmail.getText().toString(),""));
                    startActivity(new Intent(MainActivity.this,friendsActivity.class));
                    Toast.makeText(MainActivity.this,"Signed Up Succesfully",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this,task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void handleLogin(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(MainActivity.this,friendsActivity.class));
                    Toast.makeText(MainActivity.this,"Logged in succesfully",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this,task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}