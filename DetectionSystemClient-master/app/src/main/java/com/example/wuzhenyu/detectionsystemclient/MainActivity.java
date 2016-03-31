package com.example.wuzhenyu.detectionsystemclient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lwx on 2015/11/29.
 */
public class MainActivity extends Activity implements BaiduMap.OnMarkerClickListener,
        BaiduMap.OnMapClickListener, BaiduMap.OnMapLongClickListener, BaiduMap.OnMapTouchListener{

    MapView mMapView = null;
    BaiduMap mBaiduMap = null;
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    BitmapDescriptor mCurrentMarker;
    private MyLocationConfiguration.LocationMode mCurrentMode;

    View mPop;
    private String[] data={"TF-PC", "CMCC-EDU", "SJTU", "想蹭网吗？"};
    private List<WifiInfo> wifiInfos = new ArrayList<>();

    LocationInfoAPI locationInfoAPI = null;
    WifiInfosAPI wifiInfosAPI = null;
    RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        //定位初始化
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        //监听点击事件
        mBaiduMap.setOnMarkerClickListener(this);
        mBaiduMap.setOnMapClickListener(this);

        final Button list = (Button) findViewById(R.id.list);

        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,SecondActivity.class);
                startActivity(intent);
            }
        });

        final Button locate = (Button) findViewById(R.id.locate);
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mCurrentMode) {
                    case NORMAL:
                        locate.setText("跟随");
                        mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
                        mBaiduMap
                                .setMyLocationConfigeration(new MyLocationConfiguration(
                                        mCurrentMode, true, mCurrentMarker));
                        break;
                    case COMPASS:
                        locate.setText("普通");
                        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
                        mBaiduMap
                                .setMyLocationConfigeration(new MyLocationConfiguration(
                                        mCurrentMode, true, mCurrentMarker));
                        break;
                    case FOLLOWING:
                        locate.setText("罗盘");
                        mCurrentMode = MyLocationConfiguration.LocationMode.COMPASS;
                        mBaiduMap
                                .setMyLocationConfigeration(new MyLocationConfiguration(
                                        mCurrentMode, true, mCurrentMarker));
                        break;
                    default:
                        break;
                }
            }
        });

        initmPop();
        initLocation();
        mLocationClient.start();
        addAllMark();
    }

    private void setLocationInfoAPI(LocationInfoAPI response){
        locationInfoAPI = response;
    }

    private void addAllMark(){
        mQueue = Volley.newRequestQueue(getApplicationContext());
        GsonRequest<LocationInfoAPI> gsonRequest = new GsonRequest<LocationInfoAPI>("http://202.120.36.190:8080/wifiLatLng", LocationInfoAPI.class,
                new Response.Listener<LocationInfoAPI>() {
                    @Override
                    public void onResponse(LocationInfoAPI response) {
                        setLocationInfoAPI(response);
                        //Log.e(locationInfoAPI.getCount()+"", locationInfoAPI.getLocation().get(1).getLatitude()+"");
                        for(int i=0; i<response.getCount(); i++){
                            addMark(response.getLocation().get(i).getLatitude(), response.getLocation().get(i).getLongtitude());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
        mQueue.add(gsonRequest);

        /*addMark(39.963175, 116.400244, "wifiA");
        addMark(31.030832, 121.44247, "wifiB");
        addMark(31.031532, 121.45472, "wifiC");
        addMark(31.031832, 121.44947, "wifiD");*/
    }

    private void initPopView(LatLng latLng){
        initWifiInfos(latLng);
        TextView longtitude = (TextView) mPop.findViewById(R.id.longtitude);
        longtitude.setText(latLng.longitude + "");
        TextView latitude = (TextView) mPop.findViewById(R.id.latitude);
        latitude.setText(latLng.latitude + "");
    }

    private void setWifiInfosAPI(WifiInfosAPI response){
        wifiInfosAPI = response;
    }

    private void setAdapter(){
        WifiInfoAdapter adapter = new WifiInfoAdapter(MainActivity.this, R.layout.wifi_item, wifiInfos);
        ListView listView = (ListView) mPop.findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder diolog = new AlertDialog.Builder(MainActivity.this);
                WifiInfo wifiInfo = wifiInfos.get(position);
                diolog.setTitle(wifiInfo.getSsid());
                diolog.setMessage("Bssid:" + wifiInfo.getBssid()
                        + "\nSecurity:" + wifiInfo.getSecurity()
                        + "\nSignal:" + wifiInfo.getSignal()
                        + "\n添加时间:" + wifiInfo.getTimeString()
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

    private void initWifiInfos(LatLng latlng){
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
                        setWifiInfosAPI(response);
                        wifiInfos.clear();
                        for(int i=0; i<response.getCount(); i++){
                            WifiInfosAPI.WifiInfosEntity entity = response.getWifiInfos().get(i);
                            wifiInfos.add(new WifiInfo(entity.getBssid(), entity.getSsid(), entity.getSecurity(), entity.getTimeString(), entity.getSignals()));
                        }
                        setAdapter();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
        mQueue.add(gsonRequest);
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

    private void initmPop(){
        LayoutInflater mInflater = getLayoutInflater();
        mPop = (View) mInflater.inflate(R.layout.activity_favorite_infowindow, null, false);
    }

    @Override
    protected void onDestroy() {

        // 退出时销毁定位
        mLocationClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    private void addMark(double latitude, double longtitude) {
        //定义Maker坐标点
        LatLng point = new LatLng(latitude, longtitude);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.wifi);
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
    public boolean onMarkerClick(Marker marker) {
        if(marker==null){
            return false;
        }
       // Toast.makeText(getApplicationContext(), marker.getTitle(), Toast.LENGTH_SHORT).show();
        initPopView(marker.getPosition());
        InfoWindow mInfoWindow = new InfoWindow(mPop, marker.getPosition(), -47);
        mBaiduMap.showInfoWindow(mInfoWindow);
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(marker.getPosition());
        mBaiduMap.setMapStatus(update);
       // currentID = marker.getExtraInfo().getString("id");
        return true;
    }

    @Override
    public void onMapClick(LatLng point){
        mBaiduMap.hideInfoWindow();
    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(1000);
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        mLocationClient.setLocOption(option);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mBaiduMap.hideInfoWindow();
    }

    @Override
    public void onTouch(MotionEvent motionEvent) {
        mBaiduMap.hideInfoWindow();
    }

    public class MyLocationListener implements BDLocationListener {
        boolean isFirstLoc = true; // 是否首次定位

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            //Log.e("longtitude", location.getLongitude() + "");
            //Log.e("latitude", location.getLatitude() + "");
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u);
            }
        }
    }

    public void hideLocation(View v){
        switch (v.getId()){
            case R.id.set_locate:
                TextView text = (TextView) findViewById(R.id.set_locate);
                if(text.getText()=="隐藏定位"){
                    mBaiduMap.setMyLocationEnabled(false);
                    text.setText("显示定位");
                } else {
                    mBaiduMap.setMyLocationEnabled(true);
                    text.setText("隐藏定位");
                }
                break;
        }
    }
}