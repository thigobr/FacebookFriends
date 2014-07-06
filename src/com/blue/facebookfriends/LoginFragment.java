package com.blue.facebookfriends;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;

public class LoginFragment extends Fragment {
    private UiLifecycleHelper mFbSdkUiHelper;
    private OnLoggedListener mCallback;

    /**
     * Container Activity must implement this interface to be notified about
     * login status.
     *
     * @author thiago
     *
     */
    public interface OnLoggedListener {
        /**
         * Callback to notify about login success.
         */
        public void onLoginSuccess();
    }

    private final Session.StatusCallback mSessionCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            mCallback.onLoginSuccess();
        } else if (state.isClosed()) {
            if (session != null) {
                session.closeAndClearTokenInformation();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.login_fragment, container, false);
        LoginButton loginButton = (LoginButton) rootView.findViewById(R.id.login_button);
        loginButton.setFragment(this);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnLoggedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLoggedListener in order to use this fragment");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFbSdkUiHelper = new UiLifecycleHelper(getActivity(), mSessionCallback);
        mFbSdkUiHelper.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Session session = Session.getActiveSession();
        if (session != null && (session.isOpened() || session.isClosed())) {
            onSessionStateChange(session, session.getState(), null);
        }
        mFbSdkUiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFbSdkUiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        mFbSdkUiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFbSdkUiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mFbSdkUiHelper.onSaveInstanceState(outState);
    }
}
