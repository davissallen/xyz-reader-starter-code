package com.example.xyzreader.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ArticleUtils;
import com.example.xyzreader.pojo.Article;

import java.util.ArrayList;

import static com.example.xyzreader.ui.ArticleListActivity.EXTRA_CLICKED_IMAGE_URL;
import static com.example.xyzreader.ui.ArticleListActivity.EXTRA_INITIAL_POSITION;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // Log tag
    private static final String TAG = ArticleDetailActivity.class.getSimpleName();

    // Member variables
    private int mInitialPosition;
    private String mInitialPhotoUrl;
    private ArrayList<Article> mArticles;

    // views
    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;
    private View mUpButtonContainer;
    private View mUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_article_detail);

        // start loader to grab all article data
        getLoaderManager().initLoader(0, null, this);

        // Grab data from received intent
        if (savedInstanceState == null) {
            Intent receivedIntent = getIntent();
            mInitialPosition = receivedIntent.getIntExtra(EXTRA_INITIAL_POSITION, 0);
            mInitialPhotoUrl = receivedIntent.getStringExtra(EXTRA_CLICKED_IMAGE_URL);
        }

        mUpButtonContainer = findViewById(R.id.up_container);
        mUpButton = findViewById(R.id.action_up);
        mUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSupportNavigateUp();
            }
        });

        mPagerAdapter = new MyPagerAdapter(getFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(mInitialPosition);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d(TAG, "Loader started for all articles");
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.d(TAG, "Loader finished.");

        // store articles in arraylist
        mArticles = ArticleUtils.generateArticlesFromCursor(cursor);

        mPagerAdapter.notifyDataSetChanged();

        mPager.setCurrentItem(mInitialPosition, false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPagerAdapter.notifyDataSetChanged();
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            if (mArticles != null) { return mArticles.size(); }
            else return 0;
        }

        @Override
        public Fragment getItem(int position) {
            // COMPLETED: get article object
            ArticleDetailFragment fragment = ArticleDetailFragment.newInstance(mArticles.get(position));
            return fragment;
        }

    }
}
