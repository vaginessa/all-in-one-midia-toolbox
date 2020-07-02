package com.glofora.whatstus.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;


import com.glofora.whatstus.R;
import com.glofora.whatstus.adapter.PostsAdapter;
import com.glofora.whatstus.adapter.VideoAdapter;
import com.glofora.whatstus.models.Repost;
import com.glofora.whatstus.models.Video;
import com.glofora.whatstus.room.PostsDatabase;
import com.glofora.whatstus.room.VideoDatabase;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RecentsActivity extends AppCompatActivity {

    private ArrayList<Repost> repostList;
    private ArrayList<Video> videoList;
    private RecyclerView recyclerView;
    private PostsAdapter postsAdapter;
    private LinearLayout defaultLayout;
    private VideoAdapter videoAdapter;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        int theme=getSharedPreferences("theme",MODE_PRIVATE).getInt("mode",2);
        switch (theme){
            case 0:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case 2:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recents);

        String type=getIntent().getStringExtra("type");

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        defaultLayout=findViewById(R.id.default_layout);
        recyclerView=findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        switch(type){
            case "instagram":

                repostList=new ArrayList<>();
                postsAdapter=new PostsAdapter(repostList,this,true,defaultLayout);
                recyclerView.setAdapter(postsAdapter);

                Single<List<Repost>> post_single= PostsDatabase.getInstance(this).postsDao().getAll();
                post_single.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<List<Repost>>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(List<Repost> reposts) {
                                if(reposts.isEmpty()){
                                    defaultLayout.setVisibility(View.VISIBLE);
                                }
                                for(Repost repost: reposts){
                                    repostList.add(0,repost);
                                }
                                postsAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        });

                break;

            default:
                finish();
        }

    }
}
