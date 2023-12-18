package com.example.instachat;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class Profile<ActivityMainBinding> extends AppCompatActivity {
    private Button logout;
    private Button button;
    private ImageView imageProfile;
  //  ActivityMainBinding binding;
   private Uri ImagePath;
    ActivityResultLauncher<String> mTakephoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        logout=findViewById(R.id.logout);
        button=findViewById(R.id.button);
        imageProfile=findViewById(R.id.profile_img);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Profile.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        });
        mTakephoto=registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                ImagePath=result;
                getImageImageView() ;
            }
        });

        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTakephoto.launch("image/*");
            }
        });

    }
    private void getImageImageView () {

        Bitmap bitmap= null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),ImagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageProfile.setImageBitmap(bitmap);
    }
    private void uploadImage(){
        ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Uploading ...");
        progressDialog.show();
        FirebaseStorage.getInstance().getReference("images/"+ UUID.randomUUID().toString()).putFile(ImagePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            updateProfilePicture(task.getResult().toString());
                        }
                    });
                    Toast.makeText(Profile.this, "InsTaChat Image uploaded succesfully", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Profile.this,task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress=100.0* snapshot.getBytesTransferred()/snapshot.getTotalByteCount();
                progressDialog.setMessage("Uploaded" + (int) progress + "%");
            }
        });
    }
    private void updateProfilePicture(String url){
        FirebaseDatabase.getInstance().getReference("user/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/profilepicture").setValue(url);
    }

}