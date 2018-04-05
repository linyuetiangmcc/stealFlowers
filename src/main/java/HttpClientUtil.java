
 
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;  
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;  
/* 
 * 利用HttpClient进行post请求的工具类 
 */  
public class HttpClientUtil {  
    public String doPost(String url,Map<String,String> map,String charset,String content){  
        HttpClient httpClient = null;  
        HttpPost httpPost = null; 
        HttpResponse response = null;
        String result = null;  
        try{  
            httpClient = new SSLClient();  
            httpPost = new HttpPost(url);
            httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 2000);
            httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 2000);
            Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();  
            while(iterator.hasNext()){  
				Entry<String,String> elem = (Entry<String, String>) iterator.next();  
                httpPost.addHeader(elem.getKey(), elem.getValue());  
            }
            
            StringEntity entity = new StringEntity(content, "UTF-8");
            httpPost.setEntity(entity);
            
            response = httpClient.execute(httpPost);
            if(response != null){  
                HttpEntity resEntity = response.getEntity();  
                if(resEntity != null){  
                    result = EntityUtils.toString(resEntity,charset);  
                }  
            } 
        }catch(Exception ex){  
            ex.printStackTrace();  
        }finally {
			httpClient.getConnectionManager().shutdown();
			if(response != null) { 

			    try {
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} //会自动释放连接

			  }
		}
        return result;  
    }
    
    
    public String doGet(String url,Map<String,String> map,String charset){  
        HttpClient httpClient = null;  
        HttpGet httpGet = null;  
        String result = null;  
        HttpResponse response = null;
        try{  
            httpClient = new SSLClient();  
            
            //stealType=0
            /*List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("stealType", "0"));  
            String str = EntityUtils.toString(new UrlEncodedFormEntity(params, Consts.UTF_8)); 
            url = url + "?" + str;*/
            //System.out.println(url);
            httpGet = new HttpGet(url);
            httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
            httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();  
            while(iterator.hasNext()){  
				Entry<String,String> elem = (Entry<String, String>) iterator.next();  
                httpGet.addHeader(elem.getKey(), elem.getValue());  
            }
            
            response = httpClient.execute(httpGet);
            if(response != null){  
                HttpEntity resEntity = response.getEntity();  
                if(resEntity != null){  
                    result = EntityUtils.toString(resEntity,charset);  
                } 
                
                int statusLine = response.getStatusLine().getStatusCode();
                if(statusLine == 200)
                	return "200 OK";
                else
                	return "error";
            } 
        }catch(Exception ex){  
            ex.printStackTrace();  
        }finally {
        	httpClient.getConnectionManager().shutdown();
			if(response != null) { 

			    try {
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} //会自动释放连接

			  }
		}
        return result;  
    }
}