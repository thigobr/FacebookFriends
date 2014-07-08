package com.blue.facebookfriends;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Response.PagingDirection;
import com.facebook.Session;
import com.facebook.model.GraphMultiResult;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphObjectList;
import com.facebook.model.GraphUser;
import com.squareup.picasso.Picasso;

public class FriendListAdapter extends ArrayAdapter<GraphUser> {
    protected static final String TAG = "FriendListAdapter";
    private static final int sBatchSize = 10;
    private final Context mContext;
    private final List<GraphUser> mFriends;
    private final int mAvatarSize;
    private Request mNextRequest;
    private Boolean mIsLoading;
    private Boolean mHaveNextRequest;
    private final LayoutInflater mInflater;

    public FriendListAdapter(Context context, int resource, Session session) {
        super(context, resource);

        this.mContext = context;
        this.mAvatarSize = (int) context.getResources().getDimension(
                R.dimen.avatar_size);

        mFriends = new ArrayList<GraphUser>();
        if (session != null && session.isOpened()) {
            executeFirstQueryAsync(session);
        }

        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mHaveNextRequest = true;
    }

    @Override
    public int getCount() {
        if (mHaveNextRequest) {
            return mFriends.size() + 1;
        } else {
            return mFriends.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mFriends.size() && mHaveNextRequest) {
            return 1;
        }
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    static class ViewHolder {
        public TextView nameHolder;
        public ImageView pictureHolder;
        public ProgressBar progressBar;
    }

    public View getView(int position, View convertview, ViewGroup parent) {
        if (position == mFriends.size() && !mIsLoading  && mNextRequest != null) {
            mIsLoading = true;
            mNextRequest.executeAsync();
        }

        ViewHolder viewHolder;
        int viewType = getItemViewType(position);

        if (convertview == null) {
            convertview = mInflater.inflate(R.layout.list_item, null, false);
            viewHolder = new ViewHolder();
            viewHolder.nameHolder = (TextView) convertview.findViewById(R.id.name);
            viewHolder.pictureHolder = (ImageView) convertview.findViewById(R.id.picture);
            viewHolder.progressBar = (ProgressBar) convertview.findViewById(R.id.progressbar);
            convertview.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertview.getTag();
        }

        if (viewType == 0) {
            GraphUser friend = mFriends.get(position);

            viewHolder.progressBar.setVisibility(View.GONE);
            viewHolder.nameHolder.setVisibility(View.VISIBLE);
            viewHolder.pictureHolder.setVisibility(View.VISIBLE);

            viewHolder.nameHolder.setText(friend.getName());

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                .authority("graph.facebook.com")
                .appendPath(friend.getId().toString())
                .appendPath("picture")
                .appendQueryParameter("type", "large");

            Picasso.with(mContext).setLoggingEnabled(true);
            Picasso.with(mContext).load(builder.build())
                .resize(mAvatarSize, mAvatarSize)
                .centerCrop()
                .placeholder(R.drawable.person)
                .into(viewHolder.pictureHolder);
        } else {
            viewHolder.progressBar.setVisibility(View.VISIBLE);
            viewHolder.nameHolder.setVisibility(View.GONE);
            viewHolder.pictureHolder.setVisibility(View.GONE);
        }

        return convertview;
    }

    private final Request.Callback mCallBack = new Request.Callback() {
        @Override
        public void onCompleted(Response response) {
            Log.d(TAG, "Friend request completed!");
            mNextRequest = response.getRequestForPagedResults(PagingDirection.NEXT);
            if (mNextRequest != null) {
                mNextRequest.setCallback(mCallBack);
            } else {
                mHaveNextRequest = false;
            }
            updateDataSet(response);
        }
    };

    private void updateDataSet(Response response) {
        GraphMultiResult multiResult = response.getGraphObjectAs(GraphMultiResult.class);
        if (multiResult != null) {
            GraphObjectList<GraphObject> data = multiResult.getData();
            mFriends.addAll(data.castToListOf(GraphUser.class));
        }
        mIsLoading = false;
        notifyDataSetChanged();
    }

    private void executeFirstQueryAsync(Session session) {
        Bundle b = new Bundle();
        b.putInt("limit", sBatchSize);
        mIsLoading = true;
        new Request(session, "me/friends", b, null, mCallBack).executeAsync();
    }
}
