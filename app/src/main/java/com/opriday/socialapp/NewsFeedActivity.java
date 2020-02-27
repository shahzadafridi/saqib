package com.opriday.socialapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NewsFeedActivity extends AppCompatActivity {

    String TAG = "NewsFeedActivity";
    FirebaseDatabase database;
    DatabaseReference myRef;
    List<Post> postList = new ArrayList<Post>();
    RecyclerView recyclerView;
    PostAdapter adapter;
    Button newPost;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("post").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        recyclerView = (RecyclerView) findViewById(R.id.news_feed_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PostAdapter(this,postList);
        recyclerView.setAdapter(adapter);
        newPost = (Button) findViewById(R.id.new_post);
        readposts();
        newPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewsFeedActivity.this,PostActivity.class));
            }
        });
        progressBar = (ProgressBar) findViewById(R.id.progressBar_newsfeed);
    }

    private void readposts() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    if (snapshot.exists() && snapshot.getValue() != null) {
                        Post post = snapshot.getValue(Post.class);
                        postList.add(post);
                        Log.d(TAG, "Value is: " + post.id);
                    }
                }

                progressBar.setVisibility(View.GONE);

                if (postList.size() > 0){
                    adapter.updateList(postList);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(NewsFeedActivity.this, MainActivity.class));
                        finish();
                    }
                },1000);
                break;
        }
        return true;
    }
}
