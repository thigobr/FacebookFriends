package com.blue.facebookfriends;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;

import com.facebook.Request;
import com.facebook.Request.GraphUserListCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;

public class FriendListFragment extends ListFragment {
    private static String TAG = "FriendListFragment";
    private FriendListAdapter mListAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "Friend Fragment Called");

        Session session = Session.getActiveSession();

        Request.newMyFriendsRequest(session, new GraphUserListCallback() {
            @Override
            public void onCompleted(List<GraphUser> users, Response response) {
                mListAdapter = new FriendListAdapter(getActivity(), 0, users);
                FriendListFragment.this.setListAdapter(mListAdapter);
            }
        }).executeAsync();
    }
}
