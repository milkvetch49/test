package com.example.ss.realm_test;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import io.realm.Realm;

public class RegisterDevice extends AppCompatActivity {

    TextView idCode;
    Button check_Button;
    Realm realm;
    Member member;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_device);

        idCode = (TextView) findViewById(R.id.idCode);
        check_Button = (Button) findViewById(R.id.check_Button);

        realm = Realm.getDefaultInstance();
    }

   public void onCheckButtonClicked(View v){
        if(idCode.length()!=4){ //자리값 오류시 출력
            Toast.makeText(this, "4자리값을 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }
        insertDatabase();
        Intent intent = new Intent(this,CheckDeviceRun.class);
        startActivity(intent);
        finish();
    }

    //Minor 값 입력
    private void insertDatabase() {
        realm.executeTransaction(new Realm.Transaction(){
            @Override
            public void execute(Realm realm) {
                Number num = realm.where(Member.class).max("id");
                int nextID;
                if (num == null) {
                    nextID = 1;
                } else {
                    nextID = num.intValue() + 1;
                }
                member = realm.createObject(Member.class, nextID);
                member.setMinor(idCode.getText().toString());
            }
        });
    }



    @Override
    public void onDestroy(){
        super.onDestroy();
        realm.close();
    }
}