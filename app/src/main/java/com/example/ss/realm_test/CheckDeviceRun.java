package com.example.ss.realm_test;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmQuery;

public class CheckDeviceRun extends AppCompatActivity {

    Realm realm;
    TextView tv;
    private static ArrayList<Beacon> beaconList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_device_run);

        tv = (TextView)findViewById(R.id.distance);

        realm = Realm.getDefaultInstance();
        beaconList = MainActivity.getBeaconList(); //MainActivity 비콘 목록

        handler.sendEmptyMessage(0); // 핸들러 호출
    }

    @Override
    protected void onDestroy() {
        handler.removeMessages(0);
        realm.close();
        super.onDestroy();
    }

    //Next 버튼
    public void onNextButtonClicked(View v){
        Intent intent = new Intent(this,InsertName.class);
        startActivity(intent);
    }

   //핸들러 정의
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            searchDatabase();
            handler.sendEmptyMessageDelayed(0, 1000);
        }
        //입력된 코드와 Minor값이 일치하는 비콘 검색 ~ Rssi값, Txpower값 받아와 거리 계산 출력
        public void searchDatabase() {
            realm.executeTransaction(new Realm.Transaction(){
                @Override
                public void execute(Realm realm) {
                    Number num = realm.where(Member.class).max("id");
                    int nextID = num.intValue() ;
                    RealmQuery<Member> results = realm.where(Member.class).equalTo("id",nextID);
                    Member member = results.findFirst();
                    for(Beacon beacon : beaconList){
                        if(String.valueOf(beacon.getId3()).equals(member.getMinor())){
                            Distance mdistance = new Distance(beacon.getRssi(),beacon.getTxPower());
                             String distance = String.format("%.2f",mdistance.getDistance());
                             tv.setText(distance+"M");
                        }
                    }
                }
            });
        }
    };

    //return 버튼
    public void onReturnButtonClicked(View v){
        finish();
    }
}
