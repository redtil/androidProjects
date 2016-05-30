package com.example.rediet.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by Rediet on 4/19/16.
 */
public class CrimeCameraActivity extends SingleFragmentActivity{
    protected Fragment createFragment(){
        return new CrimeCameraFragment();
    }
}
