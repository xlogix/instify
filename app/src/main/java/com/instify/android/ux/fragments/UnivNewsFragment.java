package com.instify.android.ux.fragments;

/**
 * Created by Abhish3k on 3/06/2016. Thanks to Ravi
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;
import com.instify.android.R;
import com.instify.android.helpers.RetrofitBuilder;
import com.instify.android.helpers.SQLiteHandler;
import com.instify.android.interfaces.RetrofitInterface;
import com.instify.android.models.NewsItemModel;
import com.instify.android.models.NewsItemModelList;
import com.thefinestartist.finestwebview.FinestWebView;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class UnivNewsFragment extends Fragment {

  @BindView(R.id.error_message) TextView errorMessage;
  @BindView(R.id.placeholder_error) LinearLayout placeholderError;
  Unbinder unbinder;

  private SwipeRefreshLayout mSwipeRefreshLayout;
  private SimpleStringRecyclerViewAdapter mAdapter;
  private RecyclerView recyclerView;
  private List<Object> news = new ArrayList<>();

  public UnivNewsFragment() {
  }

  public static UnivNewsFragment newInstance() {
    UnivNewsFragment frag = new UnivNewsFragment();
    Bundle args = new Bundle();
    frag.setArguments(args);
    return frag;
  }

  @Override public void onAttach(Context context) {
    super.onAttach(context);
  }

  @Override public void onDestroy() {
    super.onDestroy();
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.fragment_university_news, container, false);
    unbinder = ButterKnife.bind(this, rootView);
    // Taking control of the menu options
    setHasOptionsMenu(true);
    // Prevent crash on Rotate
    setRetainInstance(true);
    // Initialize SwipeRefreshLayout
    mSwipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
    mSwipeRefreshLayout.setColorSchemeResources(R.color.red_primary, R.color.black,
        R.color.google_blue_900);

    // Setting up recycle view
    recyclerView = rootView.findViewById(R.id.recycler_view_university);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    recyclerView.setItemAnimator(new DefaultItemAnimator());

    // Make it look like something is happening
    showRefreshing();

    // Make the request!
    makeJSONRequestRetrofit();

    mSwipeRefreshLayout.setOnRefreshListener(() -> {
      showRefreshing();
      makeJSONRequestRetrofit();
    });

    mAdapter = new SimpleStringRecyclerViewAdapter(getContext(), news);
    recyclerView.setAdapter(mAdapter);

    return rootView;
  }

  public void makeJSONRequestRetrofit() {
    RetrofitInterface client = RetrofitBuilder.createService(RetrofitInterface.class);
    Call<NewsItemModelList> call = client.GetUnivNews();
    call.enqueue(new Callback<NewsItemModelList>() {

      @Override public void onResponse(@Nullable Call<NewsItemModelList> call,
          @Nullable Response<NewsItemModelList> response) {
        if (response.isSuccessful()) {
          // Clear the recycle view
          news.clear();

          news.addAll(response.body().getNewsItems());
          /* for (int i = 0; i < news.size(); i += 5) {
            final NativeExpressAdView n = new NativeExpressAdView(getContext());
            news.add(i, n);
          }
          */

          // Handle UI
          hidePlaceHolder();
          hideRefreshing();

          // setUpAndLoadNativeExpressAds();
          if (news.size() == 0) {
            showErrorPlaceholder("Error in fetching news");
          } else {
            hidePlaceHolder();
          }
          // Setting the adapter
          mAdapter.notifyDataSetChanged();
        } else {
          showErrorPlaceholder("Error Receiving University News");
          hideRefreshing();
        }
      }

      @Override public void onFailure(Call<NewsItemModelList> call, Throwable t) {
        // Clear the view
        news.clear();
        // Update UI
        showErrorPlaceholder("Failed to Receive University News");
        mAdapter.notifyDataSetChanged();
        hideRefreshing();
      }
    });
  }

  private void setUpAndLoadNativeExpressAds() {
    // Use a Runnable to ensure that the RecyclerView has been laid out before setting the
    // ad size for the Native Express ad. This allows us to set the Native Express ad's
    // width to match the full width of the RecyclerView.
    recyclerView.post(() -> {
      final float scale = getContext().getResources().getDisplayMetrics().density;
      // Set the ad size and ad unit ID for each Native Express ad in the items list.
      for (int i = 0; i <= news.size(); i += 5) {
        final NativeExpressAdView adView = (NativeExpressAdView) news.get(i);

        AdSize adSize = new AdSize((int) ((recyclerView.getWidth()
            - recyclerView.getPaddingRight()
            - recyclerView.getPaddingLeft()
            - 32) / scale), 150);
        adView.setAdSize(adSize);
        adView.setAdUnitId(getString(R.string.native_express_ad_unit_id));
      }

      // Load the first Native Express ad in the items list.
      loadNativeExpressAd(0);
    });
  }

  private void loadNativeExpressAd(final int index) {
    if (index >= news.size()) {
      return;
    }

    Object item = news.get(index);
    if (!(item instanceof NativeExpressAdView)) {
      throw new ClassCastException(
          "Expected item at index " + index + " to be a Native" + " Express ad.");
    }

    final NativeExpressAdView adView = (NativeExpressAdView) item;

    // Set an AdListener on the NativeExpressAdView to wait for the previous Native Express ad
    // to finish loading before loading the next ad in the items list.
    adView.setAdListener(new AdListener() {
      @Override public void onAdLoaded() {
        super.onAdLoaded();
        // The previous Native Express ad loaded successfully, call this method again to
        // load the next ad in the items list.
        loadNativeExpressAd(index + 5);
      }

      @Override public void onAdFailedToLoad(int errorCode) {
        // The previous Native Express ad failed to load. Call this method again to load
        // the next ad in the items list.
        Timber.e("The previous Native Express ad failed to load. Attempting to"
            + " load the next Native Express ad in the items list.");
        loadNativeExpressAd(index + 5);
      }
    });

    adView.loadAd(new AdRequest.Builder().build());
  }

  @Override public void onPrepareOptionsMenu(Menu menu) {
    menu.removeGroup(R.id.main_menu_group);
    super.onPrepareOptionsMenu(menu);
  }

  private void showRefreshing() {
    if (!mSwipeRefreshLayout.isRefreshing()) {
      mSwipeRefreshLayout.setRefreshing(true);
    }
  }

  private void hideRefreshing() {
    if (mSwipeRefreshLayout.isRefreshing()) {
      mSwipeRefreshLayout.setRefreshing(false);
    }
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  public void showErrorPlaceholder(String message) {
    if (placeholderError != null && errorMessage != null) {
      if (placeholderError.getVisibility() != View.VISIBLE) {
        placeholderError.setVisibility(View.VISIBLE);
      }
      errorMessage.setText(message);
    }
  }

  public void hidePlaceHolder() {
    if (placeholderError != null && errorMessage != null) {
      if (placeholderError.getVisibility() == View.VISIBLE) {
        placeholderError.setVisibility(View.INVISIBLE);
      }
      errorMessage.setText("Something Went Wrong Try Again");
    }
  }

  public static class SimpleStringRecyclerViewAdapter
      extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int AD_TYPE = 1;
    private static final int CONTENT_TYPE = 0;
    private Context mContext;
    private List<Object> newsArray;

    // Constructor
    private SimpleStringRecyclerViewAdapter(Context context, List<Object> newsArray) {
      mContext = context;
      this.newsArray = newsArray;
    }

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      RecyclerView.ViewHolder viewHolder;
      LayoutInflater inflater = LayoutInflater.from(parent.getContext());
      if (viewType == AD_TYPE) {
        View v = inflater.inflate(R.layout.card_view_with_ad, parent, false);
        viewHolder = new AdViewHolder(v);
      } else {
        View v = inflater.inflate(R.layout.card_view_univ, parent, false);
        viewHolder = new ViewHolder(v);
      }
      return viewHolder;
    }

    @Override public int getItemViewType(int position) {
      /* if (position % 5 == 0) {
        return CONTENT_TYPE;
      } */
      return CONTENT_TYPE;
    }

    @Override public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
      int viewType = getItemViewType(position);
      switch (viewType) {
        case CONTENT_TYPE:
          ViewHolder viewHolder = (ViewHolder) holder;
          NewsItemModel newsItemModelItem = (NewsItemModel) newsArray.get(position);
          viewHolder.mUnivNewsTitle.setText(newsItemModelItem.getTitle());
          viewHolder.mUnivNewsSnip.setText(newsItemModelItem.getSnip());
          viewHolder.mImageButton2.setOnClickListener(view -> {
            SQLiteHandler db = new SQLiteHandler(mContext);
            // OLD
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBodyText = "'"
                + newsItemModelItem.getTitle().toUpperCase()
                + "',"
                + "\n"
                + newsItemModelItem.getSnip()
                + "\n"
                + newsItemModelItem.getLink()
                + "\n"
                + db.getUserDetails().getName()
                + " has shared a topic with you from Instify https://goo.gl/YRSMJa";
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT,
                db.getUserDetails().getName() + " has shared a topic with you from Instify");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText);
            mContext.startActivity(Intent.createChooser(sharingIntent, "Share this topic on"));
          });
          viewHolder.mUnivNewsSnip.setOnClickListener(v -> {

            new FinestWebView.Builder(v.getContext()).theme(R.style.FinestWebViewTheme)
                .titleDefault("News Update")
                .showUrl(false)
                .statusBarColorRes(R.color.colorPrimaryDark)
                .toolbarColorRes(R.color.colorPrimary)
                .titleColorRes(R.color.finestWhite)
                .urlColorRes(R.color.colorPrimaryLight)
                .iconDefaultColorRes(R.color.finestWhite)
                .progressBarColorRes(R.color.finestWhite)
                .stringResCopiedToClipboard(R.string.copied_to_clipboard)
                .stringResCopiedToClipboard(R.string.copied_to_clipboard)
                .stringResCopiedToClipboard(R.string.copied_to_clipboard)
                .updateTitleFromHtml(true)
                .swipeRefreshColorRes(R.color.colorPrimaryDark)
                .menuSelector(R.drawable.selector_light_theme)
                .menuTextGravity(Gravity.CENTER)
                .menuTextPaddingRightRes(R.dimen.defaultMenuTextPaddingLeft)
                .dividerHeight(0)
                .gradientDivider(false)
                .setCustomAnimations(R.anim.slide_up, R.anim.hold, R.anim.hold, R.anim.slide_down)
                .show(newsItemModelItem.getLink());
          });
          break;
        case AD_TYPE:
          // Fall Through
        default:
          AdViewHolder nativeExpressHolder = (AdViewHolder) holder;
          NativeExpressAdView adView = (NativeExpressAdView) newsArray.get(position);
          ViewGroup adCardView = (ViewGroup) nativeExpressHolder.itemView;

          // The NativeExpressAdViewHolder recycled by the RecyclerView may be a different
          // instance than the one used previously for this position. Clear the
          // NativeExpressAdViewHolder of any subviews in case it has a different
          // AdView associated with it, and make sure the AdView for this position doesn't
          // already have a parent of a different recycled NativeExpressAdViewHolder.
          if (adCardView.getChildCount() > 0) {
            adCardView.removeAllViews();
          }
          if (adView.getParent() != null) {
            ((ViewGroup) adView.getParent()).removeView(adView);
          }
          // Add the Native Express ad to the native express ad view.
          adCardView.addView(adView);
          break;
      }
    }

    @Override public int getItemCount() {
      return newsArray.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
      private View mView;
      @BindView(R.id.imageView2) ImageView mImageView2;
      @BindView(R.id.univ_news_title) TextView mUnivNewsTitle;
      @BindView(R.id.univ_news_snip) TextView mUnivNewsSnip;
      @BindView(R.id.imageButton2) ImageButton mImageButton2;
      @BindView(R.id.btnLikeUniv) CheckBox mImageButton;

      public ViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
        mView = view;
      }
    }

    public static class AdViewHolder extends RecyclerView.ViewHolder {
      private AdViewHolder(View view) {
        super(view);
      }
    }
  }
}
