package com.codepath.apps.restclienttemplate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

import com.codepath.apps.restclienttemplate.TwitterClient;

public class TimelineActivity extends AppCompatActivity {

    public static final String TAG="TimelineActivity";
    TwitterClient client;
    //define recycler view
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;
    SwipeRefreshLayout swipeContainer;
    EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        //get instance of Twitter client
        client=TwitterApp.getRestClient(this);

        swipeContainer=findViewById(R.id.swipeContainer);
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG,"fetching new data");
                populateHomeTimeline();
            }
        });

        //find the recycler view
        rvTweets=findViewById(R.id.rvTweets);
        //init the list of tweet and adapter
        tweets=new ArrayList<>();
        adapter=new TweetsAdapter(this,tweets);
        //extract linear
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        //recycler view setup: layout manager and adapter
        rvTweets.setLayoutManager(layoutManager);
        rvTweets.setAdapter(adapter);

        scrollListener=new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG,"onLoadMore"+page);
                loadMoreData();
            }
        };
        //adds the scroll listener to recyclerview
        rvTweets.addOnScrollListener(scrollListener);
        
        populateHomeTimeline();
    }

    private void loadMoreData() {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        client.getNextPageOfTweets(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG,"onSuccess for loadMoreData"+json.toString());
                //  --> Deserialize and construct new model objects from the API response
                JSONArray jsonArray=json.jsonArray;
                try {
                    List<Tweet> tweets = Tweet.fromJsonArray(jsonArray);
                    //  --> Append the new data objects to the existing set of items inside the array of items
                    //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()
                    adapter.addAll(tweets);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG,"onFailure for loadMoreData",throwable);
            }
        },tweets.get(tweets.size()-1).id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate the menu, this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.compose){
            //compose icon has been selected
//            Toast.makeText(this,"Compose!",Toast.LENGTH_SHORT).show();
            //navigate to the compose activity
            Intent intent=new Intent(this,ComposeActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG,"onSuccess"+json.toString());

                JSONArray jsonArray = json.jsonArray;
                try {
                    //remember to clear out old items before appending in new ones
                    adapter.clear();
                    //... the data has come back, add new items to your adapter
                    adapter.addAll(Tweet.fromJsonArray(jsonArray));
                    //now we call setrefreshing(false) to signal refresh has finished
                    swipeContainer.setRefreshing(false);
                } catch (JSONException e) {
                    Log.e(TAG,"Json exception",e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG,"onFailure"+response,throwable);
            }
        });
    }
}