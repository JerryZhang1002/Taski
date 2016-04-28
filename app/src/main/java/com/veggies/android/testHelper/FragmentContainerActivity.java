package com.veggies.android.testHelper;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import com.veggies.android.todoList.MainActivity;
import com.veggies.android.todoList.R;


/**
 * This class is used for testing fragment.
 */
public class FragmentContainerActivity extends MainActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

     public void loadFragment(Fragment fragment){
         FragmentManager fragmentManager = getFragmentManager();
         fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
     }
}
