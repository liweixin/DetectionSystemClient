package com.example.wuzhenyu.detectionsystemclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by lwx on 2015/11/30.
 */
public class WifiInfoAdapter extends ArrayAdapter<WifiInfo> {

    private int resourceId;

    public WifiInfoAdapter(Context context, int resource, List<WifiInfo> wifiInfos) {
        super(context, resource, wifiInfos);
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        WifiInfo wifiInfo = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        TextView wifiName = (TextView) view.findViewById(R.id.wifi_name);
        wifiName.setText(wifiInfo.getSsid());
        return view;
    }
}