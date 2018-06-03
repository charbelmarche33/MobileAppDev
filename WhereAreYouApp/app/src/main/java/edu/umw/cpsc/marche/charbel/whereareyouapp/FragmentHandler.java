package edu.umw.cpsc.marche.charbel.whereareyouapp;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * Created by Charbel on 4/19/2018.
 */

public class FragmentHandler{
    private static final FragmentHandler FRAGMENT_HANDLER = new FragmentHandler();

    //private constructor to avoid client applications to use constructor
    private FragmentHandler(){}

    public static FragmentHandler getInstance(){
        return FRAGMENT_HANDLER;
    }

}
