package com.example.ss.realm_test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmQuery;

public class InsertName extends AppCompatActivity {

    Realm realm;
    TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_name);

        name = (TextView) findViewById(R.id.name);

        realm = Realm.getDefaultInstance();

    }

    public void onNextButtonClicked(View v) {

        insertDatabase();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onReturnButtonClicked(View v) {
        finish();
    }


    private void insertDatabase() {
        realm.executeTransaction(new Realm.Transaction(){

            @Override
            public void execute(Realm realm) {
                Number num = realm.where(Member.class).max("id");
                int nextID = num.intValue() ;
                RealmQuery<Member> results = realm.where(Member.class).equalTo("id",nextID);
                Member member = results.findFirst();
                member.setName(name.getText().toString());

                }
        });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        realm.close();
    }
}