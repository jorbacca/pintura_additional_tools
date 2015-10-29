package edu.udg.bcds.pintura.library;

import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import android.util.Log;

public class Httppostlibrary {
	
	 InputStream is = null;
	  String result = "";
	  
	  public JSONArray getserverdata(ArrayList<NameValuePair> parameters, String urlwebserver ){
	
		  //conecta via http y envia un post.
	  httppostconnect(parameters,urlwebserver);
		  
	  if (is!=null){//si obtuvo una respuesta
		  Log.v("Unity","Ingresa a InputStream diferente de NULL");
		  
		  getpostresponse();
		  
		  Log.v("Unity","Despues del getpostresponse... y a punto de retornar");
		  
		 return getjsonarray();
	  
	  }else{
		  Log.v("Unity","El servicio no ha respondido se va a retornar NULL...");
		  JSONArray no_results = new JSONArray();
	      return no_results;

	  }
		  
	  }
	  
	   
	  //peticion HTTP
   private void httppostconnect(ArrayList<NameValuePair> parametros, String urlwebserver){
  	
 	//
 	try{
 	        HttpClient httpclient = new DefaultHttpClient();
 	        HttpPost httppost = new HttpPost(urlwebserver);
 	        httppost.setEntity(new UrlEncodedFormEntity(parametros));
 	        //ejecuto peticion enviando datos por POST
 	        HttpResponse response = httpclient.execute(httppost); 
 	        HttpEntity entity = response.getEntity();
 	         is = entity.getContent();
 	         
 	}catch(Exception e){
 	        Log.e("Unity", "Error in http connection "+e.toString());
 	}
 	
 }
 
 public void getpostresponse(){
 
 	//Convierte respuesta a String
 	try{
 	        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
 	        StringBuilder sb = new StringBuilder();
 	        String line = null;
 	        while ((line = reader.readLine()) != null) {
 	                sb.append(line + "\n");
 	        }
 	        is.close();
 	 
 	        result=sb.toString();
 	        
 	        if(result.equalsIgnoreCase("")){
 	        	result = null;
 	        }
 	        Log.d("Unity","HttpPostLibrary resultado= "+sb.toString());
 	}catch(Exception e){
 	        Log.e("Unity", "Error converting result "+e.toString());
 	}
}
 
 public JSONArray getjsonarray(){
 	//parse json data
 	try{
 		if(result == null || result.equalsIgnoreCase("")){
 			result = "[{}]";
 		}
 		
         JSONArray jArray = new JSONArray(result);
         
         return jArray;
 	}
 	catch(JSONException e){
 	        Log.e("Unity", "Error parsing data "+e.toString());
 	        return null;
 	}
		
 }
 	
}
