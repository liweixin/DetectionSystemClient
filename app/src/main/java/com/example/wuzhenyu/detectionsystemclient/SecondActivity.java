package com.example.wuzhenyu.detectionsystemclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;

import android.util.Log;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;


public class SecondActivity extends Activity {

    public BDLocationListener myListener = new MyLocationListener();

    public static final int SET_GONE = 1;
    public static final int SET_RESULT = 2;
    public static final int SET_SUCCESS = 3;
    private ProgressBar progressBar;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg){
            switch (msg.what){
                case SET_GONE:
                    if(progressBar.getVisibility()!=View.VISIBLE) break;
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "服务器没有响应", Toast.LENGTH_SHORT).show();
                    break;
                case SET_RESULT:
                    //Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case SET_SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "全部上传成功", Toast.LENGTH_LONG).show();
            }
        }
    };

    private EditText serverIP;
    private EditText serverPort;
    private Button sendAPFeatures;
    public static WifiManager wifiManager;
    public static WifiInfo wifiInfo;//保存当前连接wifi信息
    private DhcpInfo dhcpInfo;
    private List<ScanResult> wifiList;

    LocationClient mLocationClient;
    static BDLocation bdlocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.wifi_list);
        //new Thread(new APAcessRecord()).start();  调试好APAcessRecord后再加上这句话

        //定位初始化
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        initLocation();
        mLocationClient.start();

        getScanResults();
        getConnectionInfo();
        showWifiScanList();
        serverIP = (EditText) findViewById(R.id.ip);
        serverPort = (EditText) findViewById(R.id.port);
        sendAPFeatures = (Button) findViewById(R.id.send);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        sendAPFeatures.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            TimeUnit.MILLISECONDS.sleep(30000);  //30s后无响应停止等待
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Message msg = new Message();
                        msg.what = SET_GONE;
                        handler.sendMessage(msg);
                    }
                }).start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String ip = serverIP.getText().toString();
                        String port = serverPort.getText().toString();
                        String serverPath = "http://" + ip + ":" + port + "/apFeatures";
                        for (int i = 0; i < wifiList.size(); i++) {
                            //Log.e(wifiList.size()+"", i+"");
                            try {
                                InfoList infoList = new InfoList(wifiList.get(i), wifiInfo, dhcpInfo, bdlocation);
                                HttpPost httpPost = new HttpPost(serverPath);
                                List<NameValuePair> params = new ArrayList<NameValuePair>();
                                params.add(new BasicNameValuePair("MacAdress", infoList.wifiInfo.getMacAddress()));
                                if(infoList.scanResult!=null){
                                    params.add(new BasicNameValuePair("BSSID", infoList.scanResult.BSSID));
                                    params.add(new BasicNameValuePair("SSID", infoList.scanResult.SSID));
                                    params.add(new BasicNameValuePair("Security", infoList.scanResult.capabilities));
                                    params.add(new BasicNameValuePair("Signal", String.valueOf(Math.abs(infoList.scanResult.level))));
                                }
                                //暂时不需要上传的数据
                                /*if(infoList.dhcpInfo!=null){
                                    params.add(new BasicNameValuePair("IpAdress", InfoList.getIpString(infoList.dhcpInfo.ipAddress)));
                                    params.add(new BasicNameValuePair("Gateway",InfoList.getIpString(infoList.dhcpInfo.gateway)));
                                    params.add(new BasicNameValuePair("Netmask",InfoList.getIpString(infoList.dhcpInfo.netmask)));
                                    params.add(new BasicNameValuePair("Dns1", InfoList.getIpString(infoList.dhcpInfo.dns1)));
                                    params.add(new BasicNameValuePair("Dns2", InfoList.getIpString(infoList.dhcpInfo.dns2)));
                                    params.add(new BasicNameValuePair("DhcpServer", InfoList.getIpString(infoList.dhcpInfo.serverAddress)));
                                }*/
                                if(infoList.location!=null){
                                    params.add(new BasicNameValuePair("Latitude", String.valueOf(infoList.location.getLatitude())));
                                    params.add(new BasicNameValuePair("Longtitude", String.valueOf(infoList.location.getLongitude())));
                                }
                                httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                                HttpClient client = new DefaultHttpClient();
                                HttpResponse response = client.execute(httpPost);
                                HttpEntity entity = response.getEntity();
                                String str = EntityUtils.toString(entity, "utf-8");
                                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                                    Message msg = new Message();
                                    if(i==wifiList.size()-1){
                                        msg.what = SET_SUCCESS;
                                    } else {
                                        msg.what = SET_RESULT;
                                    }
                                    msg.obj = str;
                                    handler.sendMessage(msg);
                                } else {
                                    //Log.e("ERROR", str);
                                }
                            }
                            catch (ClientProtocolException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        });
    }

    private void getScanResults(){
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiList = wifiManager.getScanResults();
    }

    private void getConnectionInfo(){
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiInfo = wifiManager.getConnectionInfo();
        dhcpInfo = wifiManager.getDhcpInfo();
        //show information
      //  Toast.makeText(getApplicationContext(), wifiInfo.toString(), Toast.LENGTH_LONG).show();
       // Log.e("WifiInfo:", wifiInfo.toString());
      //  Toast.makeText(getApplicationContext(), dhcpInfo.toString(), Toast.LENGTH_LONG).show();
       // Log.e("DHCPInfo:", dhcpInfo.toString());
       // Location location = findLocation();
       // Toast.makeText(getApplicationContext(), "Latitude is " + location.getLatitude() + "\nLongtitude is " + location.getLongitude(), Toast.LENGTH_LONG).show();
        //show information
    }

    private void showWifiScanList() {
        ListView listView = (ListView) findViewById(R.id.listView);
        if (wifiList == null) {
            Toast.makeText(this, "wifi not activated", Toast.LENGTH_LONG).show();
        }else {
            listView.setAdapter(new MyAdapter(this,wifiList));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ScanResult scanResult = wifiList.get(position);
                    String ssid=scanResult.SSID;
                    String bssid=scanResult.BSSID;
                    int freq=scanResult.frequency;
                    String sec=scanResult.capabilities;
                    int strength=scanResult.level;
                    Intent intent=new Intent(SecondActivity.this,ThirdActivity.class);
                    intent.putExtra("ssid",ssid);
                    intent.putExtra("bssid",bssid);
                    intent.putExtra("freq",freq);
                    intent.putExtra("sec",sec);
                    intent.putExtra("strength",strength);
                    startActivity(intent);
                }
            });
        }
    }

    //Wifi
    public class MyAdapter extends BaseAdapter {

        LayoutInflater inflater;
        List<ScanResult> list;

        public MyAdapter(Context context, List<ScanResult> list) {
            // TODO Auto-generated constructor stub
            this.inflater = LayoutInflater.from(context);
            this.list = list;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View view = null;
            view = inflater.inflate(R.layout.wifi_item, null);
            ScanResult scanResult = list.get(position);
            TextView ssidView = (TextView) view.findViewById(R.id.ssidView);
            ssidView.setText(scanResult.SSID);
            ssidView.setWidth(400);
            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            // Setting the image, according to signal intensity
            if (Math.abs(scanResult.level) > 100) {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.stat_sys_wifi_signal_0));
            } else if (Math.abs(scanResult.level) > 80) {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.stat_sys_wifi_signal_1));
            } else if (Math.abs(scanResult.level) > 70) {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.stat_sys_wifi_signal_1));
            } else if (Math.abs(scanResult.level) > 60) {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.stat_sys_wifi_signal_2));
            } else if (Math.abs(scanResult.level) > 50) {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.stat_sys_wifi_signal_3));
            } else {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.stat_sys_wifi_signal_4));
            }
            return view;
        }
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

    public class MyLocationListener implements BDLocationListener {
        boolean isFirstLoc = true; // 是否首次定位

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            /*if (location == null || mMapView == null) {
                return;
            }*/
            bdlocation =location;
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            //mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
               // mBaiduMap.animateMapStatus(u);
            }
        }
    }
}
