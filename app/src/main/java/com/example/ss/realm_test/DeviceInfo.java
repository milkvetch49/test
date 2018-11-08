package com.example.ss.realm_test;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

import io.realm.Realm;
import io.realm.RealmQuery;

public class DeviceInfo extends AppCompatActivity {

    Realm realm;
    SeekBar safeDistanceSeekBar;
    EditText safeDistanceText;
    RealmQuery<Member> query;
    Button removeButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);

        Intent intent = getIntent();
        int id=intent.getExtras().getInt("id");

        realm = Realm.getDefaultInstance();
        safeDistanceSeekBar = (SeekBar) findViewById(R.id.safeDistance_seekBar);
        safeDistanceText = (EditText) findViewById(R.id.safeDistance_editText);

        removeButton = (Button) findViewById(R.id.removeDevice_Button);

        query = realm.where(Member.class).equalTo("id",id);
        final Member member=query.findFirst();
        safeDistanceSeekBar.setProgress(member.getSafeDistance());
        safeDistanceText.setText(""+member.getSafeDistance());

        safeDistanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                if(progress<1) {
                    progress = 1; // 0으로 설정되지 않도록
                    safeDistanceSeekBar.setProgress(progress);
                }
                safeDistanceText.setText(String.valueOf(progress));
            }
            public void onStopTrackingTouch(SeekBar seekbar){
                realm.executeTransaction(new Realm.Transaction(){
                    @Override
                    public void execute(Realm realm){
                        member.setSafeDistance(safeDistanceSeekBar.getProgress());
                    }
                });

            }
            public void onStartTrackingTouch(SeekBar seekbar){

            }
        });

        safeDistanceText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode==KeyEvent.KEYCODE_ENTER){
                    int safeDistance = Integer.parseInt(safeDistanceText.getText().toString());
                    if(safeDistance<1) {
                        safeDistance = 1;
                        safeDistanceText.setText("1");
                    }
                    else if(safeDistance>40){
                        safeDistance=40;
                        safeDistanceText.setText("40");
                    }
                    else;
                    safeDistanceSeekBar.setProgress(safeDistance);
                    realm.executeTransaction(new Realm.Transaction(){
                        @Override
                        public void execute(Realm realm){
                            member.setSafeDistance(safeDistanceSeekBar.getProgress());
                        }
                    });
                    return true;
                }
                return false;
            }
        });

        //팝업창 설정
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("기기 삭제");
        builder.setMessage("정말로 삭제하시겠습니까?");
        builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        member.deleteFromRealm();
                    }
                });
                finish();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //builder로 설정한 팝업창 띄우기
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    public void onDestroy(){
        super.onDestroy();
        realm.close();
    }
}