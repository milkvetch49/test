package com.example.ss.realm_test;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

public class SetAlarm extends AppCompatActivity {

    /*share preference key name*/
    public static final String alarmVol = "alarmVol"; //초기값 7
    public static final String alarmOn = "alarmOn"; //초기값 true
    public static final String vibOn = "vibOn"; //초기값 false
    public static final String alarmSound = "alarmSound"; //알람 음악

    SeekBar alarmVolSeekBar;
    Switch alarmSwitch;
    Switch vibrationSwitch;
//    TextView alarmSong;

    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);

        alarmVolSeekBar = (SeekBar) findViewById(R.id.alarmVol_seekBar);
        alarmSwitch = (Switch) findViewById(R.id.alarmOnOff_switch);
        vibrationSwitch = (Switch) findViewById(R.id.vibration_switch);
//        alarmSong = (TextView) findViewById(R.id.alarmSong_textView);

        /*알람 정보 저장 파일 불러오기*/
        pref = PreferenceManager.getDefaultSharedPreferences(this);

        /*저장된 설정으로 화면 조정*/
        alarmVolSeekBar.setProgress(pref.getInt(alarmVol, 7));
        alarmSwitch.setChecked(pref.getBoolean(alarmOn, true));
        vibrationSwitch.setChecked(pref.getBoolean(vibOn, false));

        /*알람 볼륨 조절*/
        alarmVolSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt(alarmVol, progress);
                editor.apply();
                Log.v("alarmVol=", Integer.toString(pref.getInt(alarmVol, 7)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                alarmVolSeekBar.setProgress(pref.getInt(alarmVol, 7));
                Log.v("alarmVol=", Integer.toString(pref.getInt(alarmVol, 7)));
            }
        });

        /*알람 설정*/
        alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean(alarmOn, true);
                    editor.apply();

                    Toast.makeText(SetAlarm.this
                            , "알림이 설정되었습니다.", Toast.LENGTH_SHORT).show();
                    Log.v("alarmOn=", Boolean.toString(pref.getBoolean(alarmOn, false)));

                } else {
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("alarmOn", false);
                    editor.apply();
                    Log.v("alarmOn=", Boolean.toString(pref.getBoolean(alarmOn, true)));
                }
            }
        });
        /*진동 설정*/
        vibrationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean(vibOn, true);
                    editor.apply();
                    Toast.makeText(SetAlarm.this
                            , "진동이 설정되었습니다.", Toast.LENGTH_SHORT).show();
                    Log.v("alarmVib=", Boolean.toString(pref.getBoolean(vibOn, false)));
                } else {
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean(vibOn, false);
                    editor.apply();
                    Log.v("alarmVib=", Boolean.toString(pref.getBoolean(vibOn, true)));
                }
            }
        });

        /*알림 소리*/
    }
}
