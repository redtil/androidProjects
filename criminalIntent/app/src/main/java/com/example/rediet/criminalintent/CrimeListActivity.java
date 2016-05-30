package com.example.rediet.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by Rediet on 4/5/16.
 */
public class CrimeListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment(){
        return new CrimeListFragment();
    }
}
