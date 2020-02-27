package com.opriday.socialapp;

import android.content.Context;
import android.media.Image;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    Context context;
    List<Post> postList;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_item_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Post post = postList.get(position);
        if (post.getUsername() != null)
        holder.username.setText(post.getUsername());
        if (post.getTime() != null)
        holder.post_date.setText(post.getTime());
        if (post.getText() != null)
        holder.post_text.setText(post.getText());

        if (!TextUtils.isEmpty(post.getImage())) {
            Log.e("image", post.getImage());
            Picasso.get().load(post.getImage()).into(holder.post_image);
        }else {
            holder.post_image.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void updateList(List<Post> postList){
        this.postList = postList;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView post_image;
        TextView username,post_date,post_text;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            post_image = (ImageView) itemView.findViewById(R.id.post_item_post_image);
            username = (TextView) itemView.findViewById(R.id.post_item_username);
            post_date = (TextView) itemView.findViewById(R.id.post_item_date);
            post_text = (TextView) itemView.findViewById(R.id.post_item_post_text);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            final Post post = postList.get(position);

        }
    }

}
