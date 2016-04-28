/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.veggies.android.todoList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SpinnerAdapter;

import com.veggies.android.adapter.DrawerItemAdapter;
import com.veggies.android.adapter.DropdownMenuItemAdapter;
import com.veggies.android.custom.DrawerItem;
import com.veggies.android.custom.DropdownMenuItem;
import com.veggies.android.model.DBManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This example illustrates a common usage of the DrawerLayout widget
 * in the Android support library.
 * <p/>
 * <p>When a navigation (left) drawer is present, the host activity should detect presses of
 * the action bar's Up affordance as a signal to open and close the navigation drawer. The
 * ActionBarDrawerToggle facilitates this behavior.
 * Items within the drawer should fall into one of two categories:</p>
 * <p/>
 * <ul>
 * <li><strong>View switches</strong>. A view switch follows the same basic policies as
 * list or tab navigation in that a view switch does not create navigation history.
 * This pattern should only be used at the root activity of a task, leaving some form
 * of Up navigation active for activities further down the navigation hierarchy.</li>
 * <li><strong>Selective Up</strong>. The drawer allows the user to choose an alternate
 * parent for Up navigation. This allows a user to jump across an app's navigation
 * hierarchy at will. The application should treat this as it treats Up navigation from
 * a different task, replacing the current task stack using TaskStackBuilder or similar.
 * This is the only form of navigation drawer that should be used outside of the root
 * activity of a task.</li>
 * </ul>
 * <p/>
 * <p>Right side drawers should be used for actions, not navigation. This follows the pattern
 * established by the Action Bar that navigation should be to the left and actions to the right.
 * An action should be an operation performed on the current contents of the window,
 * for example enabling or disabling a data overlay on top of the current content.</p>
 */
public class MainActivity extends Activity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private static final String TYPE = "type";
    public static final String EMAIL = "email";
    private static final String TODOFRAG = "todofragment";

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private HashMap<String, Integer> mDrawerItems;
    private String mEmail = null;
    private boolean listFragmentFlag = true;  //flag for tagging the current fragment
    public static boolean isDisplayAllList = false;  //flag for tagging whether "All List" is chosen in dropdown menu
    private List<DrawerItem> drawerItemList = new ArrayList<>();
    private String[] menuTexts;
    private List<DropdownMenuItem> dropdownMenuItemList = new ArrayList<>();
    private SpinnerAdapter spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        menuTexts = new String[]{"All Lists", "Default", "Personal", "Shopping", "Wishlist", "Work", "Unfinished"};
        dropdownMenuItemList.add(new DropdownMenuItem(R.drawable.ic_list_white_24dp, menuTexts[0]));
        dropdownMenuItemList.add(new DropdownMenuItem(R.drawable.ic_list_white_24dp, menuTexts[1]));
        dropdownMenuItemList.add(new DropdownMenuItem(R.drawable.ic_person_pin_white_24dp, menuTexts[2]));
        dropdownMenuItemList.add(new DropdownMenuItem(R.drawable.ic_local_grocery_store_white_24dp, menuTexts[3]));
        dropdownMenuItemList.add(new DropdownMenuItem(R.drawable.ic_favorite_white_24dp, menuTexts[4]));
        dropdownMenuItemList.add(new DropdownMenuItem(R.drawable.ic_card_travel_white_24dp, menuTexts[5]));
        dropdownMenuItemList.add(new DropdownMenuItem(R.drawable.ic_highlight_off_white_24dp, menuTexts[6]));
        spinnerAdapter = new DropdownMenuItemAdapter(this, dropdownMenuItemList);
        //get the email passed by login UI
        Intent intent = this.getIntent();
        mEmail = intent.getStringExtra(EMAIL);
        mTitle = mDrawerTitle = getTitle();
        //put the drawer items in a hash map, so we can just use it to lookup index
        mDrawerItems = new HashMap<>();
        String[] drawerArray = getResources().getStringArray(R.array.drawer_array);
        for(int i = 0 ; i < drawerArray.length ; i++){
            mDrawerItems.put(drawerArray[i], i);
        }
        drawerItemList.add(new DrawerItem(R.drawable.ic_list_white_24dp, "Todo List"));
        drawerItemList.add(new DrawerItem(R.drawable.ic_settings_white_24dp, "Setting"));
        BaseAdapter adapter = new DrawerItemAdapter(this, drawerItemList);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        // enable ActionBar app icon to behave as action to toggle nav drawer
        ActionBar actionbar = getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeButtonEnabled(true);
        getActionBar().setIcon(R.drawable.logo_white);
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(0xff303F9F);
        getActionBar().setBackgroundDrawable(colorDrawable);
        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                if (listFragmentFlag) {
                    setActionMenu();
                }
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                hideActionMenu();
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
            //case R.id.action_websearch:
            // create intent to perform web search for this planet
            /*
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
            // catch event that there's no activity to handle intent
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
            }
            return true;*/
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        String[] drawer_array = getResources().getStringArray(R.array.drawer_array);

        if(position == mDrawerItems.get(this.getResources().getString(R.string.drawer_item_list))){
            listFragmentFlag = true;
            setActionMenu();
            Bundle data = new Bundle();
            data.putInt(TYPE, 5);
            //selected To-do list
            // update the main content by replacing fragments
            Fragment fragment = new ListViewFragment();
            fragment.setArguments(data);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, TODOFRAG).commit();

        }
        else if(position == mDrawerItems.get(this.getResources().getString(R.string.drawer_item_setting))){
            listFragmentFlag = false;
            hideActionMenu();
            //selected setting
            // update the main content by replacing fragments
            Fragment fragment = new SettingFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        }

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(drawer_array[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = this.getResources().getString(R.string.taski);
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    private void setActionMenu() {
        isDisplayAllList = false;
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setListNavigationCallbacks(spinnerAdapter, new DropDownListenser());
    }

    private void hideActionMenu() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    class DropDownListenser implements ActionBar.OnNavigationListener {
        String[] listTypes = getResources().getStringArray(R.array.types);
        @Override
        public boolean onNavigationItemSelected(int itemPosition, long itemId) {
            Bundle data = new Bundle();
            int pos;
            if (itemPosition == 0) {
                pos = DBManager.LIST_TYPE_ALL;                 //all list
                isDisplayAllList = false;
            }
            else if (itemPosition <= 5) {
                pos = itemPosition - 1;
                isDisplayAllList = false;
            }
            else {
                pos = DBManager.LIST_TYPE_UNFINISHED;          //unfinished list
                isDisplayAllList = true;
            }
            data.putInt(TYPE, pos);
            listFragmentFlag = true;
            // update the main content by replacing fragments
            Fragment fragment = new ListViewFragment();
            fragment.setArguments(data);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, TODOFRAG).commit();
            return true;
        }
    }
}