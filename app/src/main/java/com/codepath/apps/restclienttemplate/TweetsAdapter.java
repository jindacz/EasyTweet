package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    Context context;
    List<Tweet> tweets;
    //pass in the context and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    @NonNull
    @Override
    //for each row, inflate the layout for tweet
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //take layout inflater, pass in context, inflae item_tweet
        View view=LayoutInflater.from(context).inflate(R.layout.item_tweet,parent,false);
        return new ViewHolder(view);
    }

    //bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //get the data at position
        Tweet tweet=tweets.get(position);

        //bind the tweet with the viewholder we passed in
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }






    //define a view holder
    public class ViewHolder extends RecyclerView.ViewHolder{
        //get reference from item_view.xml
        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        //item is one row in recycler view
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //get reference for each
            ivProfileImage=itemView.findViewById(R.id.ivProfileImage);
            tvBody=itemView.findViewById(R.id.tvBody);
            tvScreenName=itemView.findViewById(R.id.tvScreenName);
        }

        public void bind(Tweet tweet) {
            //take out attribute of tweet, to fill out the view in screen
            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.screenName);
            //load the image based on image url
            Glide.with(context).load(tweet.user.profileImageUrl).into(ivProfileImage);
        }
    }
}
