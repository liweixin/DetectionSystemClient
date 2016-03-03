


# DetectionSystemClient

功能：收集、显示并上传用户周围的wifi信息

##平台
*  android
*  minSdkVersion 14
*  targetSdkVersion 23

##使用的第三方类库
*  网络部分：  官方提供的httpclient。  <br>
*  定位：采用了百度地图API的定位功能（没有采用Android内置的定位功能，因为它和百度地图的定位会返回不同标准的坐标，为了统一所以采用百度地图API的定位功能。） <br>

##serverIp
202.120.36.190.

##port
目前为8080.

##相关API
URL："http://" + ip + ":" + port + "/apFeatures"  <br>
采用POST向服务器发送数据，每次发送一个wifi的信息，分多次发送  <br>

####向服务器上传附近WIFI的位置信息
* 示例：http://202.120.36.190:8080/apFeatures  <br>
```
[MacAdress=70:72:3c:33:0b:ef,
 BSSID=3c:46:d8:66:98:9c, 
 SSID=LoveLive!, 
 Security=[WPA-PSK-CCMP][WPA2-PSK-CCMP][ESS], 
 Signal=83, 
 Latitude=31.030783, 
 Longtitude=121.442435]
```
post的内容为List<NameValuePair>类型（NameValuePair是HttpClient中的一个类，目前已被官方建议不要使用）。每次post必须包含这七个参数。

##Hint
*  无需认证或登录  <br>

##可以完善的部分
*  数据应采用json格式传输，上传所需的Json格式数据可以用第三方库Gson生成，网络部分可以换成第三方库Volley或者retrofit更加简洁高效。 <br>
*  POST部分应该改为一次上传所有WIFI信息比较合理（不知道为什么分多次上传很慢，而且也不科学）。  <br>
