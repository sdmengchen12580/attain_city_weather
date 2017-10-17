package com.example.tool;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class CopyOfGetAir extends Thread{
	private String city;
	private int air;
	public CopyOfGetAir(String c) {
		// TODO Auto-generated constructor stub
		city=c;
		start();
	}
	public int getPMInfoImpl(String city){
		 String url = "http://gameservices.sinaapp.com/pm25?city="+city;
		 HttpResponse httpResponse = null;
		 int result=0;
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
				 String air=EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
				 airJson=new JSONObject(air);
				 result=airJson.getInt("aqi");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return result;
	}
	
	
	public int getAirIsFinish(){
		while(air==0){
			continue;
		}
		return air;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		air=getPMInfoImpl(city);
	}
}
