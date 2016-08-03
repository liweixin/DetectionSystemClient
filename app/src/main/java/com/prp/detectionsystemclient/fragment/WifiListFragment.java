package com.prp.detectionsystemclient.fragment;

import android.content.Context;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.prp.detectionsystemclient.MyApplication;
import com.prp.detectionsystemclient.R;
import com.prp.detectionsystemclient.RecyclerView.MyWifiInfoAdapter;
import com.prp.detectionsystemclient.RecyclerView.MyWifiInfoDecoration;
import com.prp.detectionsystemclient.RecyclerView.MyWifiInfo;
import com.prp.detectionsystemclient.RecyclerView.MyWifiInfoOnItemClickListener;
import com.prp.detectionsystemclient.function.ScanAndUploeadNearbyWifi;
import com.prp.detectionsystemclient.function.TraceRoute;
import com.prp.detectionsystemclient.network.Network;
import com.prp.detectionsystemclient.network.StringPostRequest;
import com.prp.detectionsystemclient.util.Settings;
import com.prp.detectionsystemclient.util.Util;
import com.prp.detectionsystemclient.view.TabButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lwx on 2016/6/3.
 */
public class WifiListFragment extends Fragment implements View.OnClickListener {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    MyWifiInfoAdapter adapter;
    View mView;
    boolean upload = false;

    TabButton buttonOne, buttonTwo, buttonThree;

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    static LinearLayout progressGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstance) {
        mView = inflater.inflate(R.layout.fragment_wifilist, container, false);
        progressGroup = (LinearLayout) mView.findViewById(R.id.progress_group);
        List<?> list = ScanAndUploeadNearbyWifi.getWifiList();
        if(list!=null&&success==list.size()){
            progressGroup.setVisibility(View.GONE);
        } else {
            //progressGroup.setVisibility(View.VISIBLE);
        }
        //重新设置数值，当fragment重新载入后可以获取值
        if(bnp!=null){
            bnp.setProgress(100);
        }
        initButton();
        initProgressBar();
        FloatingActionButton fab = (FloatingActionButton) mView.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Snackbar snackbar = Snackbar.make(view, words[getCnt()], Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE)
                        .setAction("Action", null);
                setSnackbarMessageTextColor(snackbar, Color.parseColor("#FFFFFF"));
                snackbar.show();
                hindCnt = (hindCnt + 1) % 5;
            }
        });
        if(upload){
            initRecyclerView();
        }
        return mView;
    }

    static NumberProgressBar bnp;

    private void initProgressBar(){
        bnp = (NumberProgressBar) mView.findViewById(R.id.number_progress_bar);
        //bnp.setOnProgressBarListener(this);
    }
    private void initButton(){
        buttonOne=(TabButton) mView.findViewById(R.id.btn_one);
        buttonTwo=(TabButton) mView.findViewById(R.id.btn_two);
        buttonThree=(TabButton) mView.findViewById(R.id.btn_three);

        buttonOne.setOnClickListener(this);
        buttonTwo.setOnClickListener(this);
        buttonThree.setOnClickListener(this);
        resetState(R.id.btn_three);
    }
    public static void setSnackbarMessageTextColor(Snackbar snackbar, int color) {
        View view = snackbar.getView();
        ((TextView) view.findViewById(R.id.snackbar_text)).setTextColor(color);
    }
    @Override
    public void onStart(){
        super.onStart();
        //每次启动应用只会上传一次wifi信息
        if(!upload){
            //因为要上传的wifi信息中需要用到位置信息，所以先用baidumap获取位置信息，成功后即可上传wifi信息
            //没有使用android自带的定位功能，因为它和baidumap定位的坐标格式不同，结果差别较大，所以统一采用baidumap的格式
            mLocationClient = new LocationClient(MyApplication.instance.getContext());     //声明LocationClient类
            mLocationClient.registerLocationListener(myListener);    //注册监听函数
            initLocation();
            mLocationClient.start();
            upload = true;
        } else {
            //initRecyclerView();
        }
    }
    public void initRecyclerView() {
        recyclerView = (RecyclerView) mView.findViewById(R.id.rv);
        recyclerView.addItemDecoration(new MyWifiInfoDecoration(MyApplication.instance.getContext(), MyWifiInfoDecoration.VERTICAL_LIST, 2));
        recyclerView.addItemDecoration(new MyWifiInfoDecoration(MyApplication.instance.getContext(), MyWifiInfoDecoration.HORIZONTAL_LIST, 2));
        layoutManager = new LinearLayoutManager(MyApplication.instance.getContext());
        //layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        //recyclerView.setLayoutManager(layoutManager);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        setAdapter();
    }
    public void setAdapter() {
        List<MyWifiInfo> list = new ArrayList<>();
        List<ScanResult> wifilist = ScanAndUploeadNearbyWifi.getWifiList();
        for(int i=0; i<wifilist.size(); i++){
            list.add(new MyWifiInfo(wifilist.get(i).SSID, Math.abs(wifilist.get(i).level), wifilist.get(i).BSSID));
        }
        recyclerView.setAdapter(adapter = new MyWifiInfoAdapter(list));
        adapter.setmOnItemClickListener(new MyWifiInfoOnItemClickListener() {
            @Override
            public void onItemClick(View view, MyWifiInfo item) {
                List<WifiConfiguration> configurationList = ScanAndUploeadNearbyWifi.wifiManager.getConfiguredNetworks();
                for(WifiConfiguration configuration:configurationList){
                    //去除返回的双引号
                    String ssid = configuration.SSID;
                    ssid = ssid.substring(1, ssid.length()-1);
                    if(ssid.equals(item.getContent())){
                        //没有使用BSSID，因为它没有被set，始终为null
                        //使用SSID作为判断，因为SSID有重复，所以用户可能被连上错误的同名WIFI
                        Util.toast("正在将wifi切换至"+item.getContent());
                        ScanAndUploeadNearbyWifi.wifiManager.enableNetwork(configuration.networkId,false);
                    }
                    Log.e(configuration.SSID, item.getContent());
                }
                Util.toast(item.getContent() + " pressed.");
            }
        });
    }
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    //表示上传成功&失败的wifi数量
    //final int[] cnt = {0, 0};
    static int success = 0;
    static int fail = 0;
    public synchronized void addSuccess(){
        success++;
    }
    public synchronized void addFail(){
        fail++;
    }
    public void mOnReceiveLocation(BDLocation location){
        mLocationClient.stop();//定位一次即关闭定位功能
        Log.d("receive location", "haha");
        Log.d(location.getLatitude() + "", location.getLongitude() + "");
        Util.setLatitude(location.getLatitude());
        Util.setLongtitude(location.getLongitude());
        WifiManager wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        //扫描完自动上传

        ScanAndUploeadNearbyWifi.scan(wifiManager,
                //用于上传wifi信息的回调函数
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("{'info': 'Update Success.', 'code': 1}")){
                            addSuccess();
                            int size = ScanAndUploeadNearbyWifi.getWifiList().size();
                            if(success==size){
                                //所有wifi均成功上传，设置进度条满。
                                bnp.setProgress(100);
                            } else {
                                bnp.incrementProgressBy(100/size);
                            }
                        }
                        Log.e(ScanAndUploeadNearbyWifi.getWifiList().size()+"", success+"");
                        checkResult();
                        Log.d("response", response);
                    }
                },  new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        addFail();
                        checkResult();
                        Log.e("VolleyError", error.getMessage(), error);
                    }
                });
        if(false)
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> results = TraceRoute.getInstance().exe("./data/data/com.prp.detectionsystemclient/files/traceroute " + Network.traceRouteIp);
                String result = "";
			/* 将结果转换成字符串, 输出到 TextView中 */
                for(String line : results){
                    result += line + "\n";
                }
                final String finalResult = result;
                Log.d("traceroute", result);

                //上传traceroute结果至服务器
                Map<String, String> map = new HashMap<>();
                map.put("bssid", ScanAndUploeadNearbyWifi.getWifiInfo().getBSSID());
                map.put("macAdress", ScanAndUploeadNearbyWifi.getWifiInfo().getMacAddress());
                map.put("content", result);
                StringPostRequest stringPostRequest = new StringPostRequest(Network.baseAdress + "traceroute/upload",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("traceResponse", response);
                                //检测traceRoute结果是否正确
                                //Util.toast("traceRoute信息上传成功");
                                //Util.toast(finalResult);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("tracerouteErr", error.getMessage(), error);
                    }
                }, map);
                Network.getQueue().add(stringPostRequest);
            }
        }).start();
        //扫描完成后更新wifi列表
        initRecyclerView();
    }
    public void checkResult(){
        if(success+fail==ScanAndUploeadNearbyWifi.getWifiList().size()){

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressGroup.setVisibility(View.GONE);
                        }
                    });
                }
            }).start();

            if(fail==0){
                //全部上传成功
                switch (Settings.mode){
                    case Settings.RELEASE:
                        Util.toast("wifi安全性分析完成");
                        break;
                    case Settings.TEST:
                        Util.toast("Wifi信息上传成功:" + success + "/" + success);
                }
            } else if(success==0) {
                //全部上传失败
                switch (Settings.mode){
                    case Settings.RELEASE:
                        Util.toast("wifi安全性分析失败");
                        break;
                    case Settings.TEST:
                        Util.toast("Wifi信息上传失败:" + success + "/" + fail);
                }
            } else {
                //部分上传成功
                switch (Settings.mode){
                    case Settings.RELEASE:
                        Util.toast("wifi安全性分析部分失败");
                        break;
                    case Settings.TEST:
                        Util.toast("Wifi信息部分上传成功" + success + "/" + (success+fail));
                }
            }
        }
    }
    int hindCnt = 0;
    public int getCnt(){
        return hindCnt;
    }
    final String[] words = new String[]{"欢迎使用恶意wifi检测客户端^-^",
            "使用过程中请保持网络通畅",
            "我们会收集你周边的wifi信息，用来更好的检测钓鱼wifi",
            "但并不会侵犯您的隐私，请放心使用",
            "如果发现什么bug或者有好的建议可以联系qq627632598"};

    @Override
    public void onClick(View v)
    {
        resetState(v.getId());
        switch (v.getId())
        {
            case R.id.btn_one:
                changeView(0);
                break;
            case R.id.btn_two:
                changeView(1);
                break;
            case R.id.btn_three:
                changeView(2);
                break;
            default:
                break;
        }
    }

    private void changeView(int id){
        MyWifiInfoAdapter.securityFilter = 1-id;
        adapter.setFilter(1-id);
        adapter.notifyDataSetChanged();
    }

    private void resetState(int id) {
        // 将四个按钮背景设置为未选中
        buttonOne.setSelected(false);
        buttonTwo.setSelected(false);
        buttonThree.setSelected(false);

        // 将点击的按钮背景设置为已选中
        switch (id) {
            case R.id.btn_one:
                buttonOne.setSelected(true);
                break;
            case R.id.btn_two:
                buttonTwo.setSelected(true);
                break;
            case R.id.btn_three:
                buttonThree.setSelected(true);
                break;
        }
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            mOnReceiveLocation(location);
        }
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        mView = null;
        progressGroup = null;
        bnp = null;
        buttonOne = null;
        buttonTwo = null;
        buttonThree = null;
        recyclerView = null;
    }
}
