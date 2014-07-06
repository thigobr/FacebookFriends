package com.blue.facebookfriends;

import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.model.GraphUser;
import com.squareup.picasso.Picasso;

public class FriendListAdapter extends ArrayAdapter<GraphUser> {
    private final Context mContext;
    private final List<GraphUser> mFriends;
    private final int mAvatarSize;

    public FriendListAdapter(Context context, int resource,
            List<GraphUser> objects) {
        super(context, resource, objects);

        this.mContext = context;
        this.mFriends = objects;
        this.mAvatarSize = (int) context.getResources().getDimension(
                R.dimen.avatar_size);
    }

    @Override
    public int getCount() {
        if (mFriends == null) {
            return 0;
        }
        return mFriends.size();
    }

    static class ViewHolder {
        public TextView nameHolder;
        public ImageView pictureHolder;
    }

    public View getView(int position, View convertview, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertview == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertview = inflater.inflate(R.layout.list_item, null, false);
            viewHolder = new ViewHolder();
            viewHolder.nameHolder = (TextView) convertview.findViewById(R.id.name);
            viewHolder.pictureHolder = (ImageView) convertview.findViewById(R.id.picture);
            convertview.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertview.getTag();
        }

        GraphUser friend = mFriends.get(position);

        viewHolder.nameHolder.setText(friend.getName());

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
            .authority("graph.facebook.com")
            .appendPath(friend.getId().toString())
            .appendPath("picture")
            .appendQueryParameter("type", "medium");

        Picasso.with(mContext).setLoggingEnabled(true);
        Picasso.with(mContext).load(builder.build())
            .resize(mAvatarSize, mAvatarSize)
            .centerCrop()
            .placeholder(R.drawable.person)
            .into(viewHolder.pictureHolder);

        return convertview;
    }
}
