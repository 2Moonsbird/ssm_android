package com.ssm.xd.ssm;

/**
 * Created by xd on 18-4-18.
 */
import android.util.Log;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;


public class PackageNetModel {
    public JSONObject getIndexJSON(int user_id,String url) {
        JSONObject json = null;
        HttpClient httpClient = new DefaultHttpClient();
        List<NameValuePair> qparams = new ArrayList<>();
        //qparams.add(new BasicNameValuePair("method", "getDetail"));

        qparams.add(new BasicNameValuePair("user_id", String.valueOf(user_id)));


        try{
            URI uri = URIUtils.createURI("http", serverConfiguration.IP,
                    serverConfiguration.PORT,
                    url,null,null);
                    //URLEncodedUtils.format(qparams, "UTF-8"),

            HttpGet httpget = new HttpGet(uri);
            //HttpPost httpget=new HttpPost(uri);

            HttpResponse response = httpClient.execute(httpget);

            HttpEntity entity = response.getEntity();

            Log.i("tag", "uri:"+uri.toString());

            if (entity != null)
            {
                String contentString = EntityUtils.toString(entity);
                Log.i("tag", contentString);
                json = new JSONObject(contentString);
            }
        }catch (Exception e){
            Log.i("Exception",e.toString());
        }

        httpClient.getConnectionManager().shutdown();
        return json;
    }
}