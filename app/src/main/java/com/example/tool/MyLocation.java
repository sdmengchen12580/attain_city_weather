package com.example.tool;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class MyLocation {
    String key = "b6D0rlWO1iQuVhW89EgUq01G";
    public String getAddress(String latValue, String longValue){
        String location = getJsonLocation(latValue, longValue);
        return location;
    }//获取到西安
                                                                                                                                                                                          
    private String getJsonLocation(String latValue, String longValue){
        String urlStr = "http://api.map.baidu.com/geocoder?location=" + latValue + "," + longValue + "&output=json&key=" + key;
        HttpClient httpClient = new DefaultHttpClient();
           String responseData = "";
           JSONObject addressComponent=null;
           try{
               //向指定的URL发送Http请求
               HttpResponse response = httpClient.execute(new HttpGet(urlStr));
               if (response.getStatusLine().getStatusCode() == 200){
	               //取得服务器返回的响应
	               HttpEntity entity = response.getEntity();
	               addressComponent=new JSONObject(EntityUtils.toString(entity)).getJSONObject("result").getJSONObject("addressComponent");
	               //responseData=addressComponent.getString("district");//获取到区级
	               responseData=addressComponent.getString("city");//获取到市级
               }
           }
           catch (Exception e) {
               e.printStackTrace();
           }
           return responseData;
    }
}
