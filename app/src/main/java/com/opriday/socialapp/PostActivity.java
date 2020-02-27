package com.opriday.socialapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PostActivity extends AppCompatActivity {

    EditText text;
    ImageView image;
    TextView attachImg;
    Uri imageUri;
    Button post;
    private StorageReference mStorageRef;
    int PICK_IMAGE = 111;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_post);
        mStorageRef = FirebaseStorage.getInstance().getReference().child("post").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(String.valueOf(System.currentTimeMillis()));
        text = (EditText) findViewById(R.id.post_text);
        image = (ImageView) findViewById(R.id.post_image);
        attachImg = (TextView) findViewById(R.id.post_attach_image);
        post = (Button) findViewById(R.id.post_btn);
        attachImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (imageUri != null) {
                    uploadImage(imageUri);
                }else {
                    String str_text = text.getText().toString();
                    // Write a message to the database
                    Date date = new Date();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("post").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    String username = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    username = username.substring(0, username.indexOf("@"));
                    String pushId = myRef.push().getKey();
                    Map<String, String> map = new HashMap<>();
                    map.put("id", pushId);
                    map.put("username", username);
                    map.put("text", str_text);
                    map.put("image", "");
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm a  dd-MM-yyyy");
                    String strDate = sdf.format(c.getTime());
                    Log.e("Date","DATE : " + strDate);
                    map.put("time", "" + strDate);

                    myRef.child(pushId).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(PostActivity.this, "Post successfully.", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(PostActivity.this, NewsFeedActivity.class));
                                        finish();
                                    }
                                }, 1000);
                            } else {
                                Toast.makeText(PostActivity.this, "Failed to post", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void uploadImage(Uri file) {
        mStorageRef.putFile(file)
                .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()){
                            throw task.getException();
                        }
                        return mStorageRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    Uri downUri = task.getResult();
                    Log.d("onComplete", "onComplete: Url: "+ downUri.toString());
                    uploadPost( downUri.toString());
                }
            }
        });
    }

    private void uploadPost(String result) {
        String str_text = text.getText().toString();
        Log.e("uploadPost - image", result);
        // Write a message to the database
        Date date = new Date();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("post").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        String username = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        username = username.substring(0, username.indexOf("@"));
        String pushId = myRef.push().getKey();
        Map<String, String> map = new HashMap<>();
        map.put("id", pushId);
        map.put("username", username);
        map.put("text", str_text);
        map.put("image", result);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm a  dd-MM-yyyy");
        String strDate = sdf.format(c.getTime());
        Log.e("Date","DATE : " + strDate);
        map.put("time", "" + strDate);

        myRef.child(pushId).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(PostActivity.this, "Post successfully.", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(PostActivity.this, NewsFeedActivity.class));
                            finish();
                        }
                    }, 1000);
                } else {
                    Toast.makeText(PostActivity.this, "Failed to post", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            image.setImageURI(imageUri);
            image.setVisibility(View.VISIBLE);
        }
    }
}
