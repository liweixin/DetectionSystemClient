package com.prp.detectionsystemclient.RecyclerView2;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.prp.detectionsystemclient.R;
import com.prp.detectionsystemclient.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lwx on 2016/1/31.
 */
public class MyWifiInfoAdapter extends RecyclerView.Adapter<MyWifiInfoAdapter.ViewHolder> implements View.OnClickListener {

    public static int securityFilter = -1;

    private MyWifiInfoOnItemClickListener mOnItemClickListener = null;
    public void setmOnItemClickListener(MyWifiInfoOnItemClickListener listener){
        this.mOnItemClickListener = listener;
    }

    public List<MyWifiInfo> datas;
    public List<MyWifiInfo> allDatas = new ArrayList<>();
    public MyWifiInfoAdapter(List<MyWifiInfo> datas){
        this.datas = datas;
        allDatas.addAll(datas);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        view.setOnClickListener(this);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MyWifiInfo myWifiInfo = datas.get(position);

        holder.content.setText(myWifiInfo.getContent());

        //此处的signal为abs(wifiInfo.level)，level范围是-100~0,所以signal范围是0~100.
        int signal = myWifiInfo.getSignal();
        if(signal>=100){
            throw new IllegalArgumentException("signal should between 0 and 100.");
        }
        if(signal<=50){
            holder.wifiState.setImageResource(R.drawable.wifi_state_4);
        } else if(signal<=60) {
            holder.wifiState.setImageResource(R.drawable.wifi_state_3);
        } else if(signal<=70) {
            holder.wifiState.setImageResource(R.drawable.wifi_state_2);
        } else if(signal<=80) {
            holder.wifiState.setImageResource(R.drawable.wifi_state_1);
        } else {
            holder.wifiState.setImageResource(R.drawable.wifi_state_0);
        }

        //security
        switch (myWifiInfo.security){
            case MyWifiInfo.SAFE:
                holder.security.setImageResource(R.drawable.safe);
                break;
            case MyWifiInfo.DANGEROUS:
                holder.security.setImageResource(R.drawable.dangerous);
                break;
            case MyWifiInfo.UNKNOW:
                holder.security.setImageResource(R.drawable.unknow);
                break;
        }

        holder.itemView.setTag(myWifiInfo);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void setFilter(int securityLevel){
        datas = new ArrayList<>();
        for(MyWifiInfo obj : allDatas){
            if(obj.getSecurity()>=securityLevel){
                datas.add(obj);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v){
        if (mOnItemClickListener!=null) {
            mOnItemClickListener.onItemClick(v, (MyWifiInfo) v.getTag());
        }
    }

    public void addItem(int position, MyWifiInfo item){
        datas.add(position, item);
        notifyItemInserted(position);
    }

    public void removeItem(MyWifiInfo item){
        int position = datas.indexOf(item);
        if(position>=0) {
            datas.remove(position);
            notifyItemRemoved(position);
        } else {
            Util.toast("列表中不存在" + item.getContent() + "了哟" + "(´・∀・｀)");
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView content;
        public ImageView wifiState;
        public ImageView security;
        public ViewHolder (View view){
            super(view);
            security = (ImageView) view.findViewById(R.id.security);
            wifiState = (ImageView) view.findViewById(R.id.iv);
            content = (TextView) view.findViewById(R.id.tv);
        }
    }
}