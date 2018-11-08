package com.example.ss.realm_test;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class RingtonePlayingService extends Service {
    MediaPlayer mediaPlayer;
    int startId;
    boolean isRunning;
    private SharedPreferences pref;

    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d("ring service","start");

        if (Build.VERSION.SDK_INT >= 26){
            String CHANNEL_ID = "default";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .createNotificationChannel(channel);

            Notification noti = new NotificationCompat.Builder(this,CHANNEL_ID)
                    .setContentTitle("알람시작")
                    .setContentText("알람음이 재생됩니다.")
                    .setSmallIcon(R.mipmap.ic_belling_image)
                    .build();

            startForeground(1,noti);
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        String getState = intent.getExtras().getString("state");

        assert getState != null;
        switch (getState){
            case  "alarm on":
                startId = 1;
                break;
            case "alarm off":
                startId = 0;
                break;
            default:
                startId = 0;
                break;
        }

        Realm.init(this);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Member> m = realm.where(Member.class)
                .equalTo("state","이탈") //isRunning 추가
                .findAll();
        Log.v("Realm: ","where");
        if(!m.isEmpty()){
                this.startId=1;
            }else{
                this.startId=0;
            }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        Log.d("onDestroy() 실행","서비스 파괴");
    }
}
