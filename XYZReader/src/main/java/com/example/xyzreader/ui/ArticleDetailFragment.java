package com.example.xyzreader.ui;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.pojo.Article;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.xyzreader.ui.UIUtils.getNavigationBarSize;

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
//    RecyclerView mRecyclerBodyView;
    ImageButton mFAB;
    FrameLayout mSpinnerContainer;
    private ObservableScrollView mHidePhotoScrollView;
//    private ScrollDisablingLinearLayoutManager mScrollableManager;
    private WebView mWebView;

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

        mHidePhotoScrollView = (ObservableScrollView) mRootView.findViewById(R.id.sv_hide_photo);

        mTitleView = (TextView) mRootView.findViewById(R.id.article_title);
        mBylineView = (TextView) mRootView.findViewById(R.id.article_byline);

//        mRecyclerBodyView = (RecyclerView) mRootView.findViewById(R.id.rv_body_text);
        mWebView = (WebView) mRootView.findViewById(R.id.webview);

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
//            mBodyParagraphs = breakBodyIntoParagraphs(mArticle.getBody());
            mWebView.loadDataWithBaseURL("", mArticle.getBody(), "text/html", "UTF-8", "");

//            mScrollableManager = new ScrollDisablingLinearLayoutManager(mParentActivity);
//            mScrollableManager.setScrollEnabled(false);
//            mRecyclerBodyView.setLayoutManager(mScrollableManager);
//            BodyAdapter bodyAdapter = new BodyAdapter();
//            bodyAdapter.setHasStableIds(true);
//            mRecyclerBodyView.setAdapter(bodyAdapter);

            // set height for recyclerview
//            ViewGroup.LayoutParams params = mRecyclerBodyView.getLayoutParams();
//            params.height = getPxHeightForRecyclerView();
//            mRecyclerBodyView.setLayoutParams(params);
//            mRecyclerBodyView.setHasFixedSize(true);

            // set the image
            String imageUrl = mArticle.getPhotoUrl();
            if (imageUrl != null) {
                Picasso.with(getActivity()).load(imageUrl).into(mPhotoView);
            }

//            mHidePhotoScrollView.setCallbacks(this);

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
//        mRecyclerBodyView.setVisibility(View.VISIBLE);

        mFAB.setVisibility(View.VISIBLE);
        mSpinnerContainer.setVisibility(View.INVISIBLE);
    }
    private void showSpinner() {
//        mRecyclerBodyView.setVisibility(View.INVISIBLE);
        mFAB.setVisibility(View.INVISIBLE);
        mSpinnerContainer.setVisibility(View.VISIBLE);
    }

//    @Override
//    public void onScrollChanged(boolean enableScrolling) {
////        mScrollableManager.setScrollEnabled(enableScrolling);
//    }

//    private class BodyAdapter extends RecyclerView.Adapter<ParagraphViewHolder> {
//        @Override
//        public ParagraphViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.paragraph, parent, false);
//            return new ParagraphViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(ParagraphViewHolder holder, int position) {
//            Log.d(TAG, "position: " + position);
//            Log.d(TAG, "adapter position: " + holder.getAdapterPosition());
//            Log.d(TAG, "layout position: " + holder.getLayoutPosition());
//            Log.d(TAG, "");
//
////            if (mBodyParagraphs != null && !mBodyParagraphs.isEmpty()) {
////                holder.paragraphView.setText(mBodyParagraphs.get(position));
////            }
//
//            holder.paragraphView.loadData(mArticle.getBody(), "text/html; charset=utf-8", "utf-8");
//
//        }
//
//        @Override
//        public int getItemCount() {
//            return 1;
////            if (mBodyParagraphs != null) {
////                return mBodyParagraphs.size();
////            } else {
////                return 0;
////            }
//        }
//    }

//    private static class ParagraphViewHolder extends RecyclerView.ViewHolder {
//        private WebView paragraphView;
//
//        public ParagraphViewHolder(View itemView) {
//            super(itemView);
//            paragraphView = (WebView) itemView.findViewById(R.id.paragraph);
//        }
//    }

//    public class ScrollDisablingLinearLayoutManager extends LinearLayoutManager {
//        private boolean isScrollEnabled = true;
//
//        public ScrollDisablingLinearLayoutManager(Context context) {
//            super(context);
//        }
//
//        public void setScrollEnabled(boolean flag) {
//            this.isScrollEnabled = flag;
//        }
//
//        @Override
//        public boolean canScrollVertically() {
//            //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
//            return isScrollEnabled && super.canScrollVertically();
//        }
//    }
}
