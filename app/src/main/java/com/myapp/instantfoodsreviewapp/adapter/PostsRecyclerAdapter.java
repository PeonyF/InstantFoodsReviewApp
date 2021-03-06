package com.myapp.instantfoodsreviewapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.myapp.instantfoodsreviewapp.R;
import com.myapp.instantfoodsreviewapp.model.Posts;
import com.myapp.instantfoodsreviewapp.model.Product;

import java.util.List;

public class PostsRecyclerAdapter extends PagedListAdapter<Posts, RecyclerView.ViewHolder> {
    private static final int LAYOUT_DETAIL_PRODUCT = 0;
    private static final int LAYOUT_POSTS = 1;
    private static final int LAYOUT_LOADING = 2;
    private boolean retryPageLoad = false;
    private String errorMsg;
    private static final String TAG = PostsRecyclerAdapter.class.getSimpleName();
    public List<Product> pickProduct;

    public PostsRecyclerAdapter(List<Product> pickProduct) {
        super(DIFF_CALLBACK);
        this.pickProduct = pickProduct;
    }

    @Override
    public int getItemViewType(int position) {

        if (isPositionHeader(position)) {
            return LAYOUT_DETAIL_PRODUCT;
        } else {
            return LAYOUT_POSTS;
        }

    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }


    @Nullable
    @Override
    protected Posts getItem(int position) {
        Log.e("666", "post getItem(position) : " + position);
        return super.getItem(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder recyclerViewHolder = null;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case LAYOUT_DETAIL_PRODUCT:
                View viewProduct = layoutInflater.inflate(R.layout.item_detail_product, parent, false);
                recyclerViewHolder = new DetailProductViewHolder(viewProduct);
                break;

            case LAYOUT_LOADING:
                View viewLoading = layoutInflater.inflate(R.layout.item_progress, parent, false);
                recyclerViewHolder = new LoadingViewHolder(viewLoading);
                break;

            case LAYOUT_POSTS:
                View viewPosts = layoutInflater.inflate(R.layout.item_post, parent, false);
                recyclerViewHolder = new PostsViewHolder(viewPosts);
                break;
        }
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.e("POST BindPosition ", "" + position + " Size Item :" + getItemCount());
        Product productItem = pickProduct.get(0);
        Posts posts = getItem(position);

        String IMG_BASE_URL = "https://s3.ap-northeast-2.amazonaws.com/ppizil.app.review/";
        if (holder instanceof DetailProductViewHolder) {
            DetailProductViewHolder detailProductViewHolder = (DetailProductViewHolder) holder;
            detailProductViewHolder.detailProductName.setText(productItem.getPrTitle());
            String productImageUri = IMG_BASE_URL + productItem.getPrImage();
            Glide.with(holder.itemView)
                    .load(productImageUri)
                    .override(100, 100)
                    .into(detailProductViewHolder.detailProductImage);
        } else if (holder instanceof PostsViewHolder) {
            PostsViewHolder postsViewHolder = (PostsViewHolder) holder;
            assert posts != null;
            postsViewHolder.postTitle.setText(posts.getTitle());
            postsViewHolder.postGoodPoint.setText(posts.getGoodContents());
            postsViewHolder.postBadPoint.setText(posts.getBadContents());
            postsViewHolder.postRating.setText(Integer.toString(posts.getScore()));
            String postsImageUri = IMG_BASE_URL + posts.getStoredPath();
            Glide.with(holder.itemView)
                    .load(postsImageUri)
                    .override(100, 100)
                    .into(postsViewHolder.postPicture);
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            if (retryPageLoad) {
                Context context = null;
                loadingViewHolder.errorLayout.setVisibility(View.VISIBLE);
                loadingViewHolder.progressBar.setVisibility(View.GONE);
                loadingViewHolder.errorTxt.setText(errorMsg != null ? errorMsg : context.getString(R.string.error_msg_unknown));
            } else {
                loadingViewHolder.errorLayout.setVisibility(View.GONE);
                loadingViewHolder.progressBar.setVisibility(View.VISIBLE);
            }
        } else {
            Log.e("LOG_TAG", "Error.. Wrong type received");
        }
    }

    private static final DiffUtil.ItemCallback<Posts> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Posts>() {
                @Override
                public boolean areItemsTheSame(@NonNull Posts oldItem, @NonNull Posts newItem) {
                    return oldItem.getPrId().equals(newItem.getPrId());
                }

                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(@NonNull Posts oldItem, @NonNull Posts newItem) {
                    return oldItem.equals(newItem);
                }
            };


    public static class DetailProductViewHolder extends RecyclerView.ViewHolder {
        private TextView detailProductName;
        private ImageView detailProductImage;

        public DetailProductViewHolder(@NonNull View itemView) {
            super(itemView);
            initDetailProductViewHolder();
        }

        private void initDetailProductViewHolder() {
            detailProductImage = itemView.findViewById(R.id.iv_detail_product_image);
            detailProductName = itemView.findViewById(R.id.tv_detail_product_name);
        }
    }

    protected class LoadingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ProgressBar progressBar;
        private final TextView errorTxt;
        private final ConstraintLayout errorLayout;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);

            progressBar = itemView.findViewById(R.id.loadmore_progress);
            ImageButton retryBtn = itemView.findViewById(R.id.loadmore_retry);
            errorTxt = itemView.findViewById(R.id.loadmore_errortxt);
            errorLayout = itemView.findViewById(R.id.error_layout);

            retryBtn.setOnClickListener(this);
            errorLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.loadmore_errorlayout) {
                showRetry(false, null);
            }
        }
    }

    public void showRetry(boolean show, @Nullable String errorMsg) {
        retryPageLoad = show;
        if (errorMsg != null) this.errorMsg = errorMsg;
    }


    protected static class PostsViewHolder extends RecyclerView.ViewHolder {
        public ImageView postPicture;
        public TextView postTitle;
        public TextView postRating;
        public TextView postGoodPoint;
        public TextView postBadPoint;

        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);
            initPostsViewHolder();
        }

        private void initPostsViewHolder() {
            postTitle = itemView.findViewById(R.id.tv_post_title);
            postPicture = itemView.findViewById(R.id.iv_post_picture);
            postGoodPoint = itemView.findViewById(R.id.tv_post_good_point);
            postBadPoint = itemView.findViewById(R.id.tv_post_bad_point);
            postRating = itemView.findViewById(R.id.tv_post_rating);
        }
    }

    @Override
    public void submitList(@Nullable PagedList<Posts> pagedList) {
        assert pagedList != null;
        Log.e(TAG, "submitListA: " + pagedList.size());
        if (getCurrentList() != null) {
            Log.e(TAG, "submitList: " + getCurrentList().size());
        }
        this.notifyDataSetChanged();
        super.submitList(pagedList);
    }
}
