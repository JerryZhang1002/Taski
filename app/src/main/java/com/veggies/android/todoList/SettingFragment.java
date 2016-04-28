package com.veggies.android.todoList;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

/**
 * Created by JerryCheung Lin on 4/13/2016.
 * Fragment for user setting
 */
public class SettingFragment extends PreferenceFragment {

    private static final String SETTINGS = "setting";
    private static final String REM_PWD_ISCHECKED = "rem_pwd_ischecked";
    private static final String AUTO_LOGIN_ISCHECKED = "auto_login_ischecked";

    private SharedPreferences sharedPreferences;
    private SwitchPreference remPwdSwitch;
    private SwitchPreference autoLoginSwitch;

    @Override
    public void onStart() {
        super.onStart();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        sharedPreferences = getActivity().getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);

        remPwdSwitch = (SwitchPreference) findPreference(getResources().getString(R.string.remem_pwd_setting));
        autoLoginSwitch = (SwitchPreference) findPreference(getResources().getString(R.string.auto_login_setting));
        if (sharedPreferences.getBoolean(REM_PWD_ISCHECKED, false)) {
            remPwdSwitch.setChecked(true);
        }
        if (sharedPreferences.getBoolean(AUTO_LOGIN_ISCHECKED, false)) {
            autoLoginSwitch.setChecked(true);
        }

        remPwdSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
            boolean isChecked = ((Boolean) newValue).booleanValue();
            if (isChecked) {
                sharedPreferences.edit().putBoolean(REM_PWD_ISCHECKED, true).commit();
            } else {
                if (autoLoginSwitch.isChecked()) {
                    sharedPreferences.edit().putBoolean(AUTO_LOGIN_ISCHECKED, false).commit();
                    autoLoginSwitch.setChecked(false);
                }
                sharedPreferences.edit().putBoolean(REM_PWD_ISCHECKED, false).commit();
            }
            return true;
            }
        });

        autoLoginSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
            boolean isChecked = ((Boolean) newValue).booleanValue();
            if (isChecked) {
                sharedPreferences.edit().putBoolean(AUTO_LOGIN_ISCHECKED, true).commit();
                if (!remPwdSwitch.isChecked()) {
                    remPwdSwitch.setChecked(true);
                    sharedPreferences.edit().putBoolean(REM_PWD_ISCHECKED, true).commit();
                }
            } else {
                sharedPreferences.edit().putBoolean(AUTO_LOGIN_ISCHECKED, false).commit();
            }
            return true;
            }
        });
    }
}
