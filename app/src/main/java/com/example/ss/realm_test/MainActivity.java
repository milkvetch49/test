package com.example.ss.realm_test;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TimePicker;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {

    private static final String TAG = "MainActivity";

    Realm realm;
    Toolbar toolbar;
    DrawerLayout mDrawer;
    ActionBarDrawerToggle toggle;

    private BeaconManager beaconManager;
    private static ArrayList<Beacon> beaconList = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private RealmQuery<Member> query;
    private RealmResults<Member> results;

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final int REQIEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ArrayList<String> menu_list = new ArrayList<String>();
        ArrayAdapter<String> Adapter;

        menu_list.add("알람 설정");
        menu_list.add("로그아웃");
        menu_list.add("회원탈퇴");

        Adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, menu_list);
        ListView list = (ListView) findViewById(R.id.drawer);
        list.setAdapter(Adapter);

        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        list.setDivider(new ColorDrawable(Color.WHITE));
        list.setDividerHeight(2);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent intent = new Intent(MainActivity.this, SetAlarm.class);
                        startActivity(intent);
                        break;
                }
            }
        });


        toggle = new ActionBarDrawerToggle(this, mDrawer, R.string.app_name, R.string.app_name);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black);
        //아이콘 변경


        //Realm 초기화3514
        Realm.init(this);

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(realmConfiguration);


        realm = Realm.getDefaultInstance();
        query = realm.where(Member.class);
        results = query.findAll();
        results = results.sort("id", Sort.DESCENDING); //내림차순

        results.addChangeListener(new RealmChangeListener<RealmResults<Member>>() {
            @Override
            public void onChange(RealmResults<Member> element) {
                mAdapter = new MyAdapter(getApplicationContext(), results);
                mRecyclerView.setAdapter(mAdapter);
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);//옵션
        //Linear layout manager 사용
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MyAdapter(getApplicationContext(), results);
        mRecyclerView.setAdapter(mAdapter);

        //어플 실행시 블루투스 확인
        BluetoothAdapter m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (m_BluetoothAdapter != null) {
            if (!m_BluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQIEST_ENABLE_BT);
            }
        }

        //비콘 연결 관련 코드
        beaconManager = beaconManager.getInstanceForApplication(this);

        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);

                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }

        handler.sendEmptyMessage(0); //핸들러 호출시마다 스크롤뷰 맨 위로 가는 현상 있음


    }

    //toolbar 메뉴 버튼
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override //listView 목록 클릭 함수
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_showMap:
                Log.v("menuItem=", "show map");
                Intent intent = new Intent(MainActivity.this, Map.class);
                startActivity(intent);
                break;
            case android.R.id.home: {
                mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                mDrawer.openDrawer(Gravity.LEFT);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //권한 요청 함수
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //  Log.d(TAG, "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }

    //등록 버튼
    public void onButtonClicked(View v) {
        Intent intent = new Intent(this, RegisterDevice.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        beaconManager.unbind(this);
        handler.removeMessages(0);
        realm.removeAllChangeListeners();
        realm.close();
    }

    //핸들러 정의
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            insertDatabase();
            handler.sendEmptyMessageDelayed(0, 500);
        }

        private void insertDatabase() { //각각 member의 minor값과 동일한 minor값을 가지고 있는 비콘 검색 ~ rssi ,txpwer 값을 얻어와서 거리 계산->member에 대입
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    int id;
                    Number max_id = realm.where(Member.class).max("id");
                    Number min_id = realm.where(Member.class).min("id");

                    if (max_id != null) {
                        for (id = min_id.intValue(); id <= max_id.intValue(); id++) {
                            RealmQuery<Member> results = realm.where(Member.class).equalTo("id", id);
                            Member member = results.findFirst();
                            for (Beacon beacon : beaconList) {
                                if (String.valueOf(beacon.getId3()).equals(member.getMinor())) {
                                    Distance mdistance = new Distance(beacon.getRssi(), beacon.getTxPower());
                                    member.setDistance(mdistance.getDistance());
                                    member.setRssi(Integer.valueOf(beacon.getRssi()));
                                    member.setTxpower(Integer.valueOf(beacon.getTxPower()));
                                    if (member.getDistance() > member.getSafeDistance()) { //안전거리 관련
                                        member.setState("이탈");
                                    } else if (member.getDistance() <= member.getSafeDistance()) {
                                        member.setState("안전");
                                    }//인식 안되는건 뭘로 봐야하지
                                }
                            }
                        }
                    }
                }
            });
        }
    };

    //비콘목록 추출
    public static ArrayList<Beacon> getBeaconList() {
        return beaconList;
    }

    @Override
    //비콘 서비스 연결 시
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    beaconList.clear();
                    for (Beacon beacon : beacons) {
                        beaconList.add(beacon);
                    }
                }
            }
        });
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
        }
    }


}