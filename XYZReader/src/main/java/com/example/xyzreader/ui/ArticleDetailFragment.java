package com.example.xyzreader.ui;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.pojo.Article;
import com.squareup.picasso.Picasso;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A fragment representing a single Article detail screen. This fragment is
 * either contained in a {@link ArticleListActivity} in two-pane mode (on
 * tablets) or a {@link ArticleDetailActivity} on handsets.
 */
public class ArticleDetailFragment extends Fragment {
    // Log Tag
    private static final String TAG = "ArticleDetailFragment";

    // Bundle params
    public static final String ARG_ARTICLE = "article";

    // Views
    private FrameLayout mUpContainer;
    private View mRootView;
    private ImageView mPhotoView;
    TextView mTitleView;
    TextView mBylineView;
    RecyclerView mRecyclerBodyView;
    ImageButton mFAB;
    FrameLayout mSpinnerContainer;
    private ScrollView mHidePhotoScrollView;

    // Cursor Info
    Article mArticle;
    ArrayList<String> mBodyParagraphs;

    private boolean mIsCard;
    private AppCompatActivity mParentActivity;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticleDetailFragment() {
    }

    public static ArticleDetailFragment newInstance(Article articleObject) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(ARG_ARTICLE, articleObject);
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParentActivity = (AppCompatActivity) getActivity();
        mUpContainer = (FrameLayout) mParentActivity.findViewById(R.id.up_container);

        // TODO: See what this does ...
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mParentActivity.postponeEnterTransition();
//            setSharedElementEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.move));
//            mParentActivity.setExitSharedElementCallback(null);
//        }

        if (getArguments().containsKey(ARG_ARTICLE)) {
            mArticle = getArguments().getParcelable(ARG_ARTICLE);
        }
        mBodyParagraphs = new ArrayList<>();

        mIsCard = getResources().getBoolean(R.bool.detail_is_card);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // initialize views
        mRootView = inflater.inflate(R.layout.fragment_article_detail, container, false);

        mPhotoView = (ImageView) mRootView.findViewById(R.id.photo);

        mHidePhotoScrollView = (ScrollView) mRootView.findViewById(R.id.sv_hide_photo);

        mTitleView = (TextView) mRootView.findViewById(R.id.article_title);
        mBylineView = (TextView) mRootView.findViewById(R.id.article_byline);

        mRecyclerBodyView = (RecyclerView) mRootView.findViewById(R.id.rv_body_text);

        mFAB = (ImageButton) mRootView.findViewById(R.id.share_fab);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText("Some sample text")
                        .getIntent(), getString(R.string.action_share)));
            }
        });

        mSpinnerContainer = (FrameLayout) mRootView.findViewById(R.id.spinner_container);

        bindViews();
        return mRootView;
    }

    private void bindViews() {
        if (mRootView == null) {
            return;
        }

        if (mArticle != null) {

//            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) mHidePhotoScrollView.getLayoutParams();
//            int photoHeight = (int)(mParentActivity.getResources().getDimension(R.dimen.detail_photo_height));
//            layoutParams.bottomMargin = -photoHeight;
//            mHidePhotoScrollView.setLayoutParams(layoutParams);

            // set the title line
            mTitleView.setText(mArticle.getTitle());

            // set the date and author line
            String byLine = mArticle.getDate() + " by " + mArticle.getAuthor();
            mBylineView.setText(byLine);

            // set the body
            mBodyParagraphs = breakBodyIntoParagraphs(mArticle.getBody());
            LinearLayoutManager manager = new LinearLayoutManager(mParentActivity);
            mRecyclerBodyView.setLayoutManager(manager);
            BodyAdapter bodyAdapter = new BodyAdapter();
            bodyAdapter.setHasStableIds(true);
            mRecyclerBodyView.setAdapter(bodyAdapter);

            // set height for recyclerview
            ViewGroup.LayoutParams params = mRecyclerBodyView.getLayoutParams();
            params.height = getPxHeightForRecyclerView();
            mRecyclerBodyView.setLayoutParams(params);

            mRecyclerBodyView.setHasFixedSize(true);

            // set the image
            String imageUrl = mArticle.getPhotoUrl();
            if (imageUrl != null) {
                Picasso.with(getActivity()).load(imageUrl).into(mPhotoView);
            }

            hideSpinner();

        } else {
            showSpinner();
        }
    }

    private int getPxHeightForRecyclerView() {
        // set height of recyclerview to height of screen - meta bar height - up bar containter height
        int screenHeightPx;
        int metabarHeightPx;
        int upContainerPx;
        int navBarHeightPx;

        // get screen height
        Display display = mParentActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenHeightPx = size.y;

        // get meta bar height
        metabarHeightPx = (int) (3 * mParentActivity.getResources().getDimension(R.dimen.detail_metabar_vert_padding));

        // get up container height
        upContainerPx = mUpContainer.getHeight();

        // get nav bar height
        navBarHeightPx = getNavigationBarSize(mParentActivity).y;

        return screenHeightPx - metabarHeightPx - upContainerPx - navBarHeightPx;
    }

    private ArrayList<String> breakBodyIntoParagraphs(String body) {
        ArrayList<String> paragraphs = new ArrayList<>(Arrays.asList(body.split("\n")));

        return paragraphs;
    }

    private void hideSpinner() {
        mRecyclerBodyView.setVisibility(View.VISIBLE);
        mFAB.setVisibility(View.VISIBLE);
        mSpinnerContainer.setVisibility(View.INVISIBLE);
    }
    private void showSpinner() {
        mRecyclerBodyView.setVisibility(View.INVISIBLE);
        mFAB.setVisibility(View.INVISIBLE);
        mSpinnerContainer.setVisibility(View.VISIBLE);
    }

    private class BodyAdapter extends RecyclerView.Adapter<ParagraphViewHolder> {
        @Override
        public ParagraphViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.paragraph, parent, false);
            return new ParagraphViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ParagraphViewHolder holder, int position) {
            if (mBodyParagraphs != null && !mBodyParagraphs.isEmpty()) {
                holder.paragraphView.setText(mBodyParagraphs.get(position));
            }
        }

        @Override
        public int getItemCount() {
            if (mBodyParagraphs != null) {
                return mBodyParagraphs.size();
            } else {
                return 0;
            }
        }
    }

    private static class ParagraphViewHolder extends RecyclerView.ViewHolder {
        private TextView paragraphView;
//        private View topView;

        public ParagraphViewHolder(View itemView) {
            super(itemView);
//            topView = (View) itemView.findViewById(R.id.top_view);
            paragraphView = (TextView) itemView.findViewById(R.id.paragraph);
        }
    }

    public static Point getNavigationBarSize(Context context) {
        Point appUsableSize = getAppUsableScreenSize(context);
        Point realScreenSize = getRealScreenSize(context);

        // navigation bar on the right
        if (appUsableSize.x < realScreenSize.x) {
            return new Point(realScreenSize.x - appUsableSize.x, appUsableSize.y);
        }

        // navigation bar at the bottom
        if (appUsableSize.y < realScreenSize.y) {
            return new Point(appUsableSize.x, realScreenSize.y - appUsableSize.y);
        }

        // navigation bar is not present
        return new Point();
    }

    public static Point getAppUsableScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static Point getRealScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();

        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealSize(size);
        } else if (Build.VERSION.SDK_INT >= 14) {
            try {
                size.x = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                size.y = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (IllegalAccessException e) {} catch (InvocationTargetException e) {} catch (NoSuchMethodException e) {}
        }

        return size;
    }
}
