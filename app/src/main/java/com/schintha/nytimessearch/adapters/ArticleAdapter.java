package com.schintha.nytimessearch.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.schintha.nytimessearch.R;
import com.schintha.nytimessearch.activities.WebViewActivity;
import com.schintha.nytimessearch.models.ArticleModel;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sc043016 on 7/28/16.
 */
public class ArticleAdapter  extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {
    private static final String TAG = ArticleAdapter.class.getName();
    private List<ArticleModel> articles;
    private Context context;

    public ArticleAdapter(List<ArticleModel> articles) {
        this.articles = articles;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.ivThumbNail)
        ImageView ivThumbNail;
        @Bind(R.id.tvTitle)
        TextView tvTitle;
        private Context context;

        public ViewHolder(Context context, View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.context = context;

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //Create an intent to display the article
            int position = getLayoutPosition();
            ArticleModel article = articles.get(position);
            Intent i = new Intent(context, WebViewActivity.class);
            i.putExtra("article", Parcels.wrap(article));
            context.startActivity(i);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);
        View articleView = inflater.inflate(R.layout.item_article, parent, false);

        ViewHolder viewHolder = new ViewHolder(context, articleView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        ArticleModel article = articles.get(position);

        ImageView thumbNailView = viewHolder.ivThumbNail;
        TextView titleView = viewHolder.tvTitle;

        //Log.i(TAG, "position " + position + " " + article.getFormattedHeadline());
        thumbNailView.setImageResource(0);
        String thumbNail = article.getRandomThumbNail();
        //Log.i(TAG, thumbNail);
        if (!thumbNail.equals("")) {
            //Glide.with(context).load(thumbNail).placeholder(R.drawable.ic_loading).error(R.drawable.ic_no_image).into(thumbNailView);
            Picasso.with(context).load(thumbNail).fit().centerInside().placeholder(R.drawable.ic_loading).error(R.drawable.ic_no_image).into(thumbNailView);
        }
        else {
            thumbNailView.setImageResource(R.drawable.ic_no_image);
        }
        titleView.setText(article.getFormattedHeadline());
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }
}
