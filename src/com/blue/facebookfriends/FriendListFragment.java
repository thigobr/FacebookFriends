package com.blue.facebookfriends;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;

import com.facebook.Session;

public class FriendListFragment extends ListFragment {
    private static String TAG = "FriendListFragment";
    private FriendListAdapter mListAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "Friend Fragment Called");

        Session session = Session.getActiveSession();
        mListAdapter = new FriendListAdapter(getActivity(), 0, session);
        setListAdapter(mListAdapter);
    }
}
