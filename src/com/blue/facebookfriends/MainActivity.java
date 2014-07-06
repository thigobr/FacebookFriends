package com.blue.facebookfriends;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.blue.facebookfriends.LoginFragment.OnLoggedListener;
import com.facebook.Session;

public class MainActivity extends ActionBarActivity implements OnLoggedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            Session session = Session.getActiveSession();
            if (session != null && session.isOpened()) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new FriendListFragment()).commit();
                return;
            }

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new LoginFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoginSuccess() {
        Fragment fragment = new FriendListFragment();
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }
}
