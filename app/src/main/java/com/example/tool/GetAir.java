package com.example.tool;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class GetAir {
	public String getPMInfoImpl(String city){
		 String url = "http://gameservices.sinaapp.com/pm25?city="+city;
		 HttpResponse httpResponse = null;
		 StringBuffer result=null;
		 DefaultHttpClient client=null;
		 JSONObject airJson=null;
		 HttpGet httpGet=null;
		 
		 try {
			client=new DefaultHttpClient();
			 client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);
			 client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 15000);
			 httpGet = new HttpGet(url);
			 httpResponse = client.execute(httpGet);
			 if(httpResponse.getStatusLine().getStatusCode()==200){
				 result=new StringBuffer();
				 String air=EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
				 airJson=new JSONObject(air);
				 result.append("天气质量:"+airJson.getString("aqi"));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 if(result!=null){
			 return result.toString();
		 }else return null;
	}
}
