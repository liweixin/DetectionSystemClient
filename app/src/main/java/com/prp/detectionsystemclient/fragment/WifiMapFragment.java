package com.prp.detectionsystemclient.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.prp.detectionsystemclient.MyApplication;
import com.prp.detectionsystemclient.R;
import com.prp.detectionsystemclient.data.NearbyWifi;
import com.prp.detectionsystemclient.data.WifiInfosAPI;
import com.prp.detectionsystemclient.network.GsonRequest;
import com.prp.detectionsystemclient.network.Network;
import com.prp.detectionsystemclient.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lwx on 2016/6/3.
 */
public class WifiMapFragment extends Fragment implements BaiduMap.OnMapClickListener, BaiduMap.OnMarkerClickListener {
    View mView;
    MapView mMapView;
    BaiduMap mBaiduMap;
    LocationClient mLocClient;
    View mPop;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(MyApplication.instance.getContext());
    }
    private void getNearbyWifi(){
        if(Util.getNearbyWifi()!=null){
            addMarks();
            return;
        }
        Log.d("getNear", "wifi");
        String url = Network.baseAdress + "wifiLatLng";
        GsonRequest<NearbyWifi> gsonRequest = new GsonRequest<NearbyWifi>(url, NearbyWifi.class,
                new Response.Listener<NearbyWifi>(){
                    @Override
                    public void onResponse(NearbyWifi nearbyWifi){
                        Log.d("responseNBy", nearbyWifi.toString());
                        Util.setNearbyWifi(nearbyWifi);
                        addMarks();
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Log.d("error", error.getMessage(), error);
            }
        });
        Network.getQueue().add(gsonRequest);
    }
    private void addMarks(){
        if(Util.getNearbyWifi()==null){
            throw new NullPointerException("getNearbywifi() should be called before addmarks.");
        }
        for(int i=0; i<Util.getNearbyWifi().getCount(); i++){
            addMark(Util.getNearbyWifi().getLocation().get(i).getLatitude(),
                    Util.getNearbyWifi().getLocation().get(i).getLongtitude());
        }
    }
    private void addMark(double latitude, double longtitude) {
        //定义Maker坐标点
        LatLng point = new LatLng(latitude, longtitude);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.wifi_small);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap)
                .perspective(false);
        //.title(title);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        mView = inflater.inflate(R.layout.fragment_wifimap, container, false);
        Button locate = (Button) mView.findViewById(R.id.locate);
        locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initMyLocation(Util.getLatitude(), Util.getLongtitude());
            }
        });
        mMapView = (MapView) mView.findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setOnMarkerClickListener(this);
        mBaiduMap.setOnMapClickListener(this);
        getNearbyWifi();
        initmPop();
        return mView;
    }
    @Override
    public void onStart(){
        super.onStart();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        // 退出时销毁定位
        if(mLocClient!=null){
            mLocClient.stop();
        }
        // 关闭定位图层
        if(mBaiduMap!=null){
            mBaiduMap.setMyLocationEnabled(false);
        }
        if(mMapView!=null){
            mMapView.onDestroy();
        }
        mMapView = null;
    }
    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    public void initMyLocation(double latx, double laty) {
        if(latx<0.00001) return;
        mBaiduMap.setMyLocationEnabled(true);
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(100)
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(90.0f)
                .latitude(latx)
                .longitude(laty).build();

        //精度圈会遮挡按钮，所以不显示
        /*mBaiduMap.setMyLocationData(locData);
        mBaiduMap.setMyLocationEnabled(true);*/

        LatLng ll = new LatLng(latx, laty);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(ll).zoom(18.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mBaiduMap.hideInfoWindow();
    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }
    private void initmPop(){
        LayoutInflater mInflater = getActivity().getLayoutInflater();
        mPop = (View) mInflater.inflate(R.layout.activity_favorite_infowindow, null, false);
    }
    public void initWifiInfos(LatLng latlng){
        final Util.Location location = new Util.Location(latlng.latitude, latlng.longitude);
        if(Util.map.get(location)!=null){
            setAdapter();
            return;
        }
        GsonRequest<WifiInfosAPI> gsonRequest =
                new GsonRequest<WifiInfosAPI>("http://202.120.36.190:8080/wifiInfos?"
                        +"Latitude="
                        + latlng.latitude
                        +"&Longtitude="
                        + latlng.longitude,
                        WifiInfosAPI.class,
                        new Response.Listener<WifiInfosAPI>() {
                            @Override
                            public void onResponse(WifiInfosAPI response) {
                                Util.setWifiInfosAPI(response);
                                Util.map.put(location, response);
                                setAdapter();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", error.getMessage(), error);
                    }
                });
        Network.getQueue().add(gsonRequest);
    }
    public class WifiInfoAdapter extends ArrayAdapter<WifiInfosAPI.WifiInfosEntity> {

        private int resourceId;

        public WifiInfoAdapter(Context context, int resource, List<WifiInfosAPI.WifiInfosEntity> wifiInfos) {
            super(context, resource, wifiInfos);
            resourceId = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            WifiInfosAPI.WifiInfosEntity wifiInfosEntity = getItem(position);
            View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            TextView wifiName = (TextView) view.findViewById(R.id.wifi_name);
            wifiName.setText(wifiInfosEntity.getSsid());
            return view;
        }
    }
    private void setAdapter(){
        WifiInfoAdapter adapter = new WifiInfoAdapter(getActivity(), R.layout.wifi_item_baidumap, Util.getWifiInfosAPI().getWifiInfos());
        ListView listView = (ListView) mPop.findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder diolog = new AlertDialog.Builder(getActivity());
                WifiInfosAPI.WifiInfosEntity wifiInfosAPI = Util.getWifiInfosAPI().getWifiInfos().get(position);
                diolog.setTitle(wifiInfosAPI.getSsid());
                diolog.setMessage("Bssid:" + wifiInfosAPI.getBssid()
                        + "\nSecurity:" + wifiInfosAPI.getSecurity()
                        + "\nSignal:" + wifiInfosAPI.getSignals()
                        + "\n添加时间:" + wifiInfosAPI.getTimeString()
                        + "\n安全性:未知");
                diolog.setCancelable(true);
                diolog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                diolog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                diolog.show();
            }
        });
        setListViewSize(adapter, listView);
    }
    private void setListViewSize(WifiInfoAdapter listAdapter, ListView listView){
        if (listAdapter == null) {
            return;
        }
        int totalHeight=0;
        int maxWidth=0;
        int maxItem = 5;

        for(int i=0; i<listAdapter.getCount(); i++){
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0,0);
            if(i<maxItem){
                totalHeight += listItem.getMeasuredHeight();
            }
            if(listItem.getMeasuredWidth()>maxWidth){
                maxWidth = listItem.getMeasuredWidth();
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        if(listAdapter.getCount() <= maxItem){
            params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount()-1));
        } else {
            params.height = totalHeight + (listView.getDividerHeight() * (maxItem-1));
        }
        params.width = maxWidth + 20;
        ((ViewGroup.MarginLayoutParams)params).setMargins(10, 10, 10, 10);
        listView.setLayoutParams(params);
    }
    private void initPopView(LatLng latLng){
        initWifiInfos(latLng);
        TextView longtitude = (TextView) mPop.findViewById(R.id.longtitude);
        longtitude.setText(latLng.longitude + "");
        TextView latitude = (TextView) mPop.findViewById(R.id.latitude);
        latitude.setText(latLng.latitude + "");
    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        if(marker==null){
            return false;
        }
        initPopView(marker.getPosition());
        InfoWindow mInfoWindow = new InfoWindow(mPop, marker.getPosition(), -47);
        mBaiduMap.showInfoWindow(mInfoWindow);
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(marker.getPosition());
        mBaiduMap.setMapStatus(update);
        return true;
    }
}
