package com.example.brianr.rsslocation;

import android.support.v7.widget.RecyclerView;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by TRidley on 02/06/2017.
 */

public class RssFeedListAdapter
        extends RecyclerView.Adapter<RssFeedListAdapter.FeedModelViewHolder> {

    private List<RssFeedModel> mRssFeedModels;
    public static String urlArticleLink;

    public static class FeedModelViewHolder extends RecyclerView.ViewHolder {

        private View rssFeedView;

        public FeedModelViewHolder(View v) {
            super(v);
            rssFeedView = v;
        }

    }

    public RssFeedListAdapter(List<RssFeedModel> rssFeedModels) {

        mRssFeedModels = rssFeedModels;
    }

    @Override
    public FeedModelViewHolder onCreateViewHolder(ViewGroup parent, int type) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rss_feed, parent, false);
        final FeedModelViewHolder holder = new FeedModelViewHolder(v);
        final int finalPosition = getItemCount()-1;

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int position = holder.getAdapterPosition();

                if(position == finalPosition){
                    urlArticleLink = mRssFeedModels.get(position).link;
                }
                else {
                    urlArticleLink = mRssFeedModels.get(position+1).link;
                }

                Intent intent = new Intent(view.getContext(), PostViewActivity.class);
                view.getContext().startActivity(intent);

            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(FeedModelViewHolder holder, int position) {

        final RssFeedModel rssFeedModel = mRssFeedModels.get(position);
        ((TextView)holder.rssFeedView.findViewById(R.id.titleText)).setText(rssFeedModel.title);
        ((TextView)holder.rssFeedView.findViewById(R.id.descriptionText)).setText(rssFeedModel.description);
        ((TextView)holder.rssFeedView.findViewById(R.id.linkText)).setText(rssFeedModel.link);

    }

    @Override
    public int getItemCount() {

        return mRssFeedModels.size();
    }

}
