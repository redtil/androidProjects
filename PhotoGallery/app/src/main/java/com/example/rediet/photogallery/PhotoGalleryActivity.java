package com.example.rediet.photogallery;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class PhotoGalleryActivity extends SingleFragmentActivity {
    private static final String TAG = "PhotoGalleryActiviity";

    @Override
    protected Fragment createFragment() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onNewIntent(Intent intent){
        PhotoGalleryFragment fragment = (PhotoGalleryFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        String query="";
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            query = intent.getStringExtra(SearchManager.QUERY);
            Log.i(TAG, "Received a new search query" + query);
        }
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putString(FlickrFetch.PREF_SEARCH_QUERY,query)
                .commit();
        fragment.updateItems();
    }
}
