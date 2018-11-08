package com.example.ss.realm_test;


import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import io.realm.RealmResults;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private RealmResults<Member> mDataset;
    private Context context;
    private NotificationManager notificationManager;
    private Notification notiDistance;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mMinor;
        public TextView mName;
        public TextView mTxpower;
        public TextView mDistance;
        public TextView mRssi;
        public TextView mState;
        public ImageView mPhoto;
        public Switch onOff;


        public ViewHolder(View v) {
            super(v);
            //show_device_info의 각각의 텍스트뷰 변수와 연결
            mName = (TextView) v.findViewById(R.id.name_textView);
            mDistance = (TextView)v.findViewById(R.id.beaconDistance_textView);
            mState = (TextView)v.findViewById(R.id.status_textView);
            onOff= (Switch) v.findViewById(R.id.onOff_switch);

        }
    }

    public MyAdapter(Context context, RealmResults<Member> myDataset) {
        mDataset = myDataset;
        this.context = context;
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_device_info, parent, false);

        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder holder, final int position) {
        // 텍스트뷰에 값 출력
        holder.mName.setText(mDataset.get(position).getName());
        final String distance = String.format("%.2f",mDataset.get(position).getDistance());
        holder.mDistance.setText(distance+"M");
        holder.mState.setText(mDataset.get(position).getState());
        if(mDataset.get(position).getDistance()>mDataset.get(position).getSafeDistance()){
            holder.mState.setTextColor(Color.RED);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext() , DeviceInfo.class);
                intent.putExtra("id",mDataset.get(position).getId());
                v.getContext().startActivity(intent);
            }
        });

         holder.onOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        Log.v("Switch State=",mDataset.get(position).getId()+"True");

//                        notificationManager=(NotificationManager) context
//                                .getSystemService(context.NOTIFICATION_SERVICE);
//                        notificationManager.notify(1,notiDistance);
//                        notiDistance = new Notification.Builder(context)
//                                .setTicker("test")
//                                .setContentTitle(mDataset.get(position).getName())
//                                .setContentText(distance+"M")
//                                .setSmallIcon(android.R.drawable.stat_notify_more)
//                                .setWhen(System.currentTimeMillis())
//                                .build();
//
//                        NotificationCompat.Builder noti =
//                                new NotificationCompat.Builder(context)
//                                .setSmallIcon(R.mipmap.ic_belling_image)
//                                .setContentTitle(mDataset.get(position).getName())
//                                .setContentText(distance+"M");
                    }else{
                        Log.v("Switch State=",mDataset.get(position).getId()+"False");
                    }

                }
            });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}