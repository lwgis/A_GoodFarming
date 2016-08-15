package com.zhonghaodi.networking;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.NameValuePair;

import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.zhonghaodi.goodfarming.UILApplication;
import com.zhonghaodi.model.Comment;
import com.zhonghaodi.model.GFMessage;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.NetImage;
import com.zhonghaodi.model.NetResponse;
import com.zhonghaodi.model.Nys;
import com.zhonghaodi.model.Quan;
import com.zhonghaodi.model.Question;
import com.zhonghaodi.model.Response;
import com.zhonghaodi.model.Solution;
import com.zhonghaodi.model.UpdateUser;
import com.zhonghaodi.model.User;

/**
 * @author Administrator
 *
 */
public class HttpUtil {
	public static String WX_APP_ID="wx8fd908378b8ab3e5";
	public static String QQ_APP_ID="1104653579";
//	public static String RootURL = "http://zhd.zhonghaodi.com/dfyy/rest/";
//	public static String ImageUrl = "http://zhd.zhonghaodi.com/appimage/";
//	public static String ViewUrl = "http://zhd.zhonghaodi.com/dfyy/view/";
	public static String RootURL = "http://121.40.62.120:8088/dfyy/rest/";
	public static String ImageUrl = "http://121.40.62.120/appimage8/";
	public static String ViewUrl = "http://121.40.62.120:8088/dfyy/view/";

	public static String executeHttpGet(String urlString) {
		StringBuffer sb = new StringBuffer();
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(urlString);
		get.addHeader("Content-type", "application/json;charset=UTF-8");
		get.addHeader("Accept-Charset", "utf-8");
		get.addHeader("X-Token",UILApplication.getAuth());
		try {
			HttpResponse response = client.execute(get);

			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				InputStream inputStream = response.getEntity().getContent();
				BufferedReader buffer = new BufferedReader(
						new InputStreamReader(inputStream,
								Charset.forName("utf-8")));
				String line = null;
				while ((line = buffer.readLine()) != null) {
					sb.append(line);
				}
				inputStream.close();
				return sb.toString();
			} else {
				// TODO 返回错误信息
				
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			// TODO 返回协议错误信息
		} catch (IOException e) {
			e.printStackTrace();
			// TODO 返回网络错误
		}
		return null;
	}
	
	/**
	 * 根据不同状态码
	 * @param urlString
	 * @return
	 */
	public static String executeHttpGet1(String urlString) {
		StringBuffer sb = new StringBuffer();
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(urlString);
		get.addHeader("Content-type", "application/json;charset=UTF-8");
		get.addHeader("Accept-Charset", "utf-8");
		get.addHeader("X-Token",UILApplication.getAuth());
		try {
			HttpResponse response = client.execute(get);

			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				InputStream inputStream = response.getEntity().getContent();
				BufferedReader buffer = new BufferedReader(
						new InputStreamReader(inputStream,
								Charset.forName("utf-8")));
				String line = null;
				while ((line = buffer.readLine()) != null) {
					sb.append(line);
				}
				inputStream.close();
				return sb.toString();
			} else {
				// TODO 返回错误信息
				InputStream inputStream = response.getEntity().getContent();
				BufferedReader buffer = new BufferedReader(
						new InputStreamReader(inputStream,
								Charset.forName("utf-8")));
				String line = null;
				while ((line = buffer.readLine()) != null) {
					sb.append(line);
				}
				inputStream.close();
				return "error:"+sb.toString();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			// TODO 返回协议错误信息
			return "error:"+e.getMessage();
		} catch (IOException e) {
			e.printStackTrace();
			// TODO 返回网络错误
			return "error:"+e.getMessage();
		}
	}

	public static String executeHttpGetNoHead(String urlString) {
		StringBuffer sb = new StringBuffer();
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(urlString);
		get.addHeader("X-Token",UILApplication.getAuth());
		try {
			HttpResponse response = client.execute(get);

			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				InputStream inputStream = response.getEntity().getContent();
				BufferedReader buffer = new BufferedReader(
						new InputStreamReader(inputStream,
								Charset.forName("utf-8")));
				String line = null;
				while ((line = buffer.readLine()) != null) {
					sb.append(line);
				}
				inputStream.close();
				return sb.toString();
			} else {
				// TODO 返回错误信息
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			// TODO 返回协议错误信息
		} catch (IOException e) {
			e.printStackTrace();
			// TODO 返回网络错误
		}
		return null;
	}

	public static String executeHttpDelete(String urlString) {
		StringBuffer sb = new StringBuffer();
		DefaultHttpClient client = new DefaultHttpClient();
		HttpDelete delete = new HttpDelete(urlString);
		delete.addHeader("X-Token",UILApplication.getAuth());
		try {
			HttpResponse response = client.execute(delete);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200 || statusCode == 201) {
				InputStream inputStream = response.getEntity().getContent();
				BufferedReader buffer = new BufferedReader(
						new InputStreamReader(inputStream,
								Charset.forName("utf-8")));
				String line = null;
				while ((line = buffer.readLine()) != null) {
					sb.append(line);
				}
				inputStream.close();
				return sb.toString();
			} else {
				// TODO 返回错误信息
				InputStream inputStream = response.getEntity().getContent();
				BufferedReader buffer = new BufferedReader(
						new InputStreamReader(inputStream,
								Charset.forName("utf-8")));
				String line = null;
				while ((line = buffer.readLine()) != null) {
					sb.append(line);
				}
				inputStream.close();
				return sb.toString();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			// TODO 返回协议错误信息
		} catch (IOException e) {
			e.printStackTrace();
			// TODO 返回网络错误
		}
		return null;
	}

	public static NetResponse executeHttpPost(String serviceUrl, String informjson)
			throws Throwable {
		NetResponse netResponse = new NetResponse();
		StringBuffer sb = new StringBuffer();
		StringEntity entity = new StringEntity(informjson, "UTF-8");
		entity.setContentType("application/json;charset=UTF-8");
		entity.setContentEncoding("UTF-8");

		HttpPost post = new HttpPost(serviceUrl);
		post.setEntity(entity);
		post.setHeader("Content-Type", "application/json;charset=UTF-8");
		post.addHeader("X-Token",UILApplication.getAuth());
		HttpClientParams.setRedirecting(post.getParams(), false);
		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(post);
		System.out.println(response.getStatusLine().getStatusCode());
		if (response.getStatusLine().getStatusCode() == 201
				|| response.getStatusLine().getStatusCode() == 200) {
			InputStream inputStream = response.getEntity().getContent();
			BufferedReader buffer = new BufferedReader(new InputStreamReader(
					inputStream, Charset.forName("utf-8")));
			String line = null;
			while ((line = buffer.readLine()) != null) {
				sb.append(line);
			}
			inputStream.close();
			netResponse.setStatus(1);
			netResponse.setResult(sb.toString());
			return netResponse;
		} else {
			InputStream inputStream = response.getEntity().getContent();
			BufferedReader buffer = new BufferedReader(new InputStreamReader(
					inputStream, Charset.forName("utf-8")));
			String line = null;
			while ((line = buffer.readLine()) != null) {
				sb.append(line);
			}
			inputStream.close();
			netResponse.setStatus(0);
			netResponse.setMessage(sb.toString());
			return netResponse;
		}
	}
	
	public static NetResponse executeUserPost(String serviceUrl, String informjson)
			throws Throwable {
		NetResponse netResponse = new NetResponse();
		StringBuffer sb = new StringBuffer();
		StringEntity entity = new StringEntity(informjson, "UTF-8");
		entity.setContentType("application/json;charset=UTF-8");
		entity.setContentEncoding("UTF-8");

		HttpPost post = new HttpPost(serviceUrl);
		post.setEntity(entity);
		post.setHeader("Content-Type", "application/json;charset=UTF-8");
		HttpClientParams.setRedirecting(post.getParams(), false);
		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(post);
		System.out.println(response.getStatusLine().getStatusCode());
		if (response.getStatusLine().getStatusCode() == 201
				|| response.getStatusLine().getStatusCode() == 200) {
			InputStream inputStream = response.getEntity().getContent();
			BufferedReader buffer = new BufferedReader(new InputStreamReader(
					inputStream, Charset.forName("utf-8")));
			String line = null;
			while ((line = buffer.readLine()) != null) {
				sb.append(line);
			}
			inputStream.close();
			netResponse.setStatus(1);
			netResponse.setResult(sb.toString());
			String auth = response.getFirstHeader("X-Token").getValue();
			netResponse.setAuth(auth);
			return netResponse;
		} else {
			InputStream inputStream = response.getEntity().getContent();
			BufferedReader buffer = new BufferedReader(new InputStreamReader(
					inputStream, Charset.forName("utf-8")));
			String line = null;
			while ((line = buffer.readLine()) != null) {
				sb.append(line);
			}
			inputStream.close();
			netResponse.setStatus(0);
			netResponse.setMessage(sb.toString());
			return netResponse;
			
		}
	}

	public static NetResponse executeHttpPost(String serviceUrl,
			List<NameValuePair> paramList) throws Throwable {
		NetResponse netResponse = new NetResponse();
		StringBuffer sb = new StringBuffer();
		HttpPost post = new HttpPost(serviceUrl);
		post.addHeader("X-Token",UILApplication.getAuth());
		post.setEntity(new UrlEncodedFormEntity(paramList, HTTP.UTF_8));
		HttpClientParams.setRedirecting(post.getParams(), false);
		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(post);
		System.out.println(response.getStatusLine().getStatusCode());
		if (response.getStatusLine().getStatusCode() == 201
				|| response.getStatusLine().getStatusCode() == 200) {
			InputStream inputStream = response.getEntity().getContent();
			BufferedReader buffer = new BufferedReader(new InputStreamReader(
					inputStream, Charset.forName("utf-8")));
			String line = null;
			while ((line = buffer.readLine()) != null) {
				sb.append(line);
			}
			inputStream.close();
			netResponse.setStatus(1);
			netResponse.setResult(sb.toString());
			return netResponse;
		} else {
			InputStream inputStream = response.getEntity().getContent();
			BufferedReader buffer = new BufferedReader(new InputStreamReader(
					inputStream, Charset.forName("utf-8")));
			String line = null;
			while ((line = buffer.readLine()) != null) {
				sb.append(line);
			}
			inputStream.close();
			netResponse.setStatus(0);
			netResponse.setMessage(sb.toString());
			return netResponse;
		}
	}

	public static String executeHttpPut(String urlString, String informjson)
			throws Throwable {
		StringBuffer sb = new StringBuffer();
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPut put = new HttpPut(urlString);
		put.addHeader("Content-Type", "application/json;charset=UTF-8");
		put.addHeader("Accept-Charset", "utf-8");
		put.addHeader("X-Token",UILApplication.getAuth());
		StringEntity entity = new StringEntity(informjson, "UTF-8");
		entity.setContentType("application/json;charset=UTF-8");
		entity.setContentEncoding("UTF-8");
		put.setEntity(entity);
		try {
			HttpResponse response = client.execute(put);

			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				InputStream inputStream = response.getEntity().getContent();
				BufferedReader buffer = new BufferedReader(
						new InputStreamReader(inputStream,
								Charset.forName("utf-8")));
				String line = null;
				while ((line = buffer.readLine()) != null) {
					sb.append(line);
				}
				inputStream.close();
				return sb.toString();
			} else {
				// TODO 返回错误信息
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw e;
			// TODO 返回协议错误信息
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
			// TODO 返回网络错误
		}
		return null;
	}
	
	public static String executeHttpPut(String urlString, List<NameValuePair> paramList)
			throws Throwable {
		StringBuffer sb = new StringBuffer();
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPut put = new HttpPut(urlString);
		put.addHeader("Content-Type", "application/x-www-form-urlencoded");
		put.addHeader("Accept-Charset", "utf-8");
		put.addHeader("X-Token",UILApplication.getAuth());
		put.setEntity(new UrlEncodedFormEntity(paramList, HTTP.UTF_8));
		try {
			HttpResponse response = client.execute(put);

			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				InputStream inputStream = response.getEntity().getContent();
				BufferedReader buffer = new BufferedReader(
						new InputStreamReader(inputStream,
								Charset.forName("utf-8")));
				String line = null;
				while ((line = buffer.readLine()) != null) {
					sb.append(line);
				}
				inputStream.close();
				return sb.toString();
			} else {
				// TODO 返回错误信息
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw e;
			// TODO 返回协议错误信息
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
			// TODO 返回网络错误
		}
		return null;
	}
	/**
	 * 获取图片
	 * @param urlStr
	 * @return
	 */
	public static Bitmap getBitmap(String urlStr ) {		
		try {
			URL url = new URL(urlStr);
			URLConnection connection = url.openConnection();
			connection.setUseCaches(true);
			InputStream iStream=connection.getInputStream();
			Bitmap bmp=BitmapFactory.decodeStream(iStream);
			iStream.close();
			return bmp;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Bitmap downloadBitmap(String url) {
        final HttpClient client = new DefaultHttpClient();
        final HttpGet getRequest = new HttpGet(url);
                                                               
        try {
            HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }
                                                                   
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent();
                    FilterInputStream fit = new FlushedInputStream(inputStream);
                    return BitmapFactory.decodeStream(fit);
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                        inputStream = null;
                    }
                    entity.consumeContent();
                }
            }
        } catch (IOException e) {
            getRequest.abort();
        } catch (IllegalStateException e) {
            getRequest.abort();
        } catch (Exception e) {
            getRequest.abort();
        } finally {
            client.getConnectionManager().shutdown();
        }
        return null;
    }
	
	/**
     * An InputStream that skips the exact number of bytes provided, unless it reaches EOF.
     */
    static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int b = read();
                    if (b < 0) {
                        break;  // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }
    
    public static String getPointdicsString() {
		String jsonString = HttpUtil.executeHttpGet(RootURL + "dics/all");
		return jsonString;
	}
	
	public static String getQuestionsString(String zone) {
		String url = RootURL + "questions";
		url=url+"?zone="+zone;
		String jsonString = HttpUtil.executeHttpGet(url);
		return jsonString;
	}

	public static String getQuestionsString(int qid,String zone) {
		String url = RootURL+ "questions?fromid=" + qid;
		url=url+"&zone="+zone;
		String jsonString = HttpUtil.executeHttpGet(url);
		return jsonString;
	}
	
	public static String getMyQuestionsString(String uid) {
		String jsonString = HttpUtil.executeHttpGet(RootURL + "users/"+uid+"/questions");
		return jsonString;
	}
	
	public static String getMyGossipsString(String uid) {
		String jsonString = HttpUtil.executeHttpGet(RootURL + "users/"+uid+"/gossips");
		return jsonString;
	}
	
	public static String getMyPlantinfoString(String uid) {
		String jsonString = HttpUtil.executeHttpGet(RootURL + "users/"+uid+"/plantinfo");
		return jsonString;
	}

	public static String getMyQuestionsString(String uid,int qid) {
		String jsonString = HttpUtil.executeHttpGet(RootURL
				+ "users/"+uid+"/questions?fromid=" + qid);
		return jsonString;
	}
	
	public static String getMyGossipsString(String uid,int qid) {
		String jsonString = HttpUtil.executeHttpGet(RootURL
				+ "users/"+uid+"/gossips?fromid=" + qid);
		return jsonString;
	}
	
	public static String getMyPlantString(String uid,int qid) {
		String jsonString = HttpUtil.executeHttpGet(RootURL
				+ "users/"+uid+"/plantinfo?fromid=" + qid);
		return jsonString;
	}
	
	
	public static String getAscQuestionsString(String uid,int zone) {
		String url = RootURL + "users/"+uid+"/ascquestions";
		if(zone!=0){
			url = url + "?zone="+zone;
		}
		String jsonString = HttpUtil.executeHttpGet(url);
		return jsonString;
	}

	public static String getAscQuestionsString(String uid,int qid,int zone) {
		String url = RootURL+ "users/"+uid+"/ascquestions?fromid=" + qid;
		if(zone!=0){
			url = url + "&zone="+zone;
		}
		String jsonString = HttpUtil.executeHttpGet(url);
		return jsonString;
	}
	
	public static String getPlant(int zone) {
		String url = RootURL + "plantinfo";
		if(zone!=0){
			url = url + "?zone="+zone;
		}
		String jsonString = HttpUtil.executeHttpGet(url);
		return jsonString;
	}

	public static String getMorePlant(int qid,int zone) {
		String url = RootURL+ "plantinfo?fromid=" + qid;
		if(zone!=0){
			url = url + "&zone="+zone;
		}
		String jsonString = HttpUtil.executeHttpGet(url);
		return jsonString;
	}
	
	public static String getGossipsString(String zone) {
		String url = RootURL + "gossips";
		url=url+"?zone="+zone;
		String jsonString = HttpUtil.executeHttpGet(url);
		return jsonString;
	}

	public static String getGossipsString(int qid,String zone) {
		String url = RootURL+ "gossips?fromid=" + qid;
		url=url+"&zone="+zone;
		String jsonString = HttpUtil.executeHttpGet(url);
		return jsonString;
	}
	
	public static String getFavorites(String uid) {
		String jsonString = HttpUtil.executeHttpGet(RootURL + "users/"+uid+"/myFavQuestion");
		return jsonString;
	}

	public static String getSingleQuestion(int qid) {
		String urlString = RootURL + "questions/" + String.valueOf(qid);
		String jsonString = HttpUtil.executeHttpGet(urlString);
		return jsonString;
	}
	
	public static String getSingleGossip(int qid) {
		String urlString = RootURL + "gossips/" + String.valueOf(qid);
		String jsonString = HttpUtil.executeHttpGet(urlString);
		return jsonString;
	}
	
	public static String getSinglePlant(int qid) {
		String urlString = RootURL + "plantinfo/" + String.valueOf(qid);
		String jsonString = HttpUtil.executeHttpGet(urlString);
		return jsonString;
	}
	
	public static String getSingleResponse(int qid,int rid) {
		String urlString = RootURL + "questions/" + String.valueOf(qid)+"/responses/"+String.valueOf(rid);
		String jsonString = HttpUtil.executeHttpGet(urlString);
		return jsonString;
	}
	
	public static String getGossipsResponse(int qid,int rid) {
		String urlString = RootURL + "gossips/" + String.valueOf(qid)+"/responses/"+String.valueOf(rid);
		String jsonString = HttpUtil.executeHttpGet(urlString);
		return jsonString;
	}
	
	public static String getPlantResponse(int qid,int rid) {
		String urlString = RootURL + "plantinfo/" + String.valueOf(qid)+"/responses/"+String.valueOf(rid);
		String jsonString = HttpUtil.executeHttpGet(urlString);
		return jsonString;
	}

	public static String getAllCropsString() {
		String jsonString = HttpUtil.executeHttpGet(RootURL + "crops");
		return jsonString;
	}
	
	public static String getGossipCropsString() {
		String jsonString = HttpUtil.executeHttpGet(RootURL + "gossips/cate");
		return jsonString;
	}
	public static String getPlantInfoCropsString() {
		String jsonString = HttpUtil.executeHttpGet(RootURL + "plantinfo/cate");
		return jsonString;
	}
	
	public static String getAllCropsHasDisease() {
		String jsonString = HttpUtil.executeHttpGet(RootURL + "crops/hasDisease");
		return jsonString;
	}

	public static NetResponse sendQuestion(Question question) throws Throwable {
		String jsonString = JsonUtil.convertObjectToJson(question,
				"yyyy-MM-dd HH:mm:ss", new String[] {
						Question.class.toString(), NetImage.class.toString() });
		NetResponse response = HttpUtil.executeHttpPost(RootURL + "questions", jsonString);
		return response;
	}
	public static NetResponse sendGossip(Question question) throws Throwable {
		String jsonString = JsonUtil.convertObjectToJson(question,
				"yyyy-MM-dd HH:mm:ss", new String[] {
						Question.class.toString(), NetImage.class.toString() });
		NetResponse response = HttpUtil.executeHttpPost(RootURL + "gossips", jsonString);
		return response;
	}
	public static NetResponse sendPlant(Question question) throws Throwable {
		String jsonString = JsonUtil.convertObjectToJson(question,
				"yyyy-MM-dd HH:mm:ss", new String[] {
						Question.class.toString(), NetImage.class.toString() });
		NetResponse response = HttpUtil.executeHttpPost(RootURL + "plantinfo", jsonString);
		return response;
	}

	public static NetResponse sendResponse(Response response, int qid)
			throws Throwable {
		String urlString = RootURL + "questions/" + String.valueOf(qid)
				+ "/reply";
		String jsonString = JsonUtil.convertObjectToJson(response,
				"yyyy-MM-dd HH:mm:ss",
				new String[] { Response.class.toString(), });
		return HttpUtil.executeHttpPost(urlString, jsonString);
	}
	
	public static NetResponse sendResponseForGossip(Response response, int qid)
			throws Throwable {
		String urlString = RootURL + "gossips/" + String.valueOf(qid)
				+ "/reply";
		String jsonString = JsonUtil.convertObjectToJson(response,
				"yyyy-MM-dd HH:mm:ss",
				new String[] { Response.class.toString(), });
		return HttpUtil.executeHttpPost(urlString, jsonString);
	}
	
	public static NetResponse sendResponseForPlant(Response response, int qid)
			throws Throwable {
		String urlString = RootURL + "plantinfo/" + String.valueOf(qid)
				+ "/reply";
		String jsonString = JsonUtil.convertObjectToJson(response,
				"yyyy-MM-dd HH:mm:ss",
				new String[] { Response.class.toString(), });
		return HttpUtil.executeHttpPost(urlString, jsonString);
	}
	
	public static NetResponse sendResponseForForum(Response response, int aid)
			throws Throwable {
		String urlString = RootURL + "agrotechnicals/" + String.valueOf(aid)
				+ "/reply";
		String jsonString = JsonUtil.convertObjectToJson(response,
				"yyyy-MM-dd HH:mm:ss",
				new String[] { Response.class.toString(), });
		return HttpUtil.executeHttpPost(urlString, jsonString);
	}

	public static String getDisease(int diseaseId,String uid) {
		String urlString = RootURL + "diseases/" + String.valueOf(diseaseId)+"?uid="+uid;
		String jsonString = HttpUtil.executeHttpGet(urlString);
		return jsonString;
	}
	
	public static NetResponse sendSolution(Solution solution, int did)
			throws Throwable {
		String urlString = RootURL + "diseases/" + String.valueOf(did)
				+ "/advise";
		String jsonString = JsonUtil.convertObjectToJson(solution,
				"yyyy-MM-dd HH:mm:ss",
				new String[] { Solution.class.toString(), });
		return HttpUtil.executeHttpPost(urlString, jsonString);
	}
	
	public static NetResponse commentSolution(int did,int sid,final String uid,final String content) {
		String urlString = RootURL + "diseases/"+did+"/advice/"+sid+"/comment";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		NameValuePair uidValuePair1 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return uid;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "uid";
			}
		};
		
		nameValuePairs.add(uidValuePair1);
		
		NameValuePair cotentValuePair1 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return content;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "content";
			}
		};
		
		nameValuePairs.add(cotentValuePair1);
		
		try {
			return HttpUtil.executeHttpPost(urlString, nameValuePairs);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			NetResponse netResponse = new NetResponse();
			netResponse.setStatus(0);
			netResponse.setMessage(e.getMessage());
			return netResponse;
		}
	}
	
	public static String deleteSolution(int did,int sid){
		String urlString = RootURL + "diseases/"+did+"/advice/"+sid+"/delete";
		String result =HttpUtil.executeHttpDelete(urlString);
		return result;
	}
	
	public static NetResponse zanSolution(int did,int sid,final String uid) {
		String jsonString = null;
		String urlString = RootURL + "diseases/"+did+"/advice/"+sid+"/zan";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		NameValuePair uidValuePair1 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return uid;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "uid";
			}
		};
		
		nameValuePairs.add(uidValuePair1);
		
		try {
			return HttpUtil.executeHttpPost(urlString, nameValuePairs);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			NetResponse netResponse = new NetResponse();
			netResponse.setStatus(0);
			netResponse.setMessage(e.getMessage());
			return netResponse;
		}
	}
	
	public static NetResponse cancelZanSolution(int did,int sid,final String uid) {
		String jsonString = null;
		String urlString = RootURL + "diseases/"+did+"/advice/"+sid+"/cancelzan";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		NameValuePair uidValuePair1 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return uid;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "uid";
			}
		};
		
		nameValuePairs.add(uidValuePair1);
		
		try {
			return HttpUtil.executeHttpPost(urlString, nameValuePairs);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			NetResponse netResponse = new NetResponse();
			netResponse.setStatus(0);
			netResponse.setMessage(e.getMessage());
			return netResponse;
		}
	}
	
	public static String getSingleSolution(int did,int sid,String uid) {
		String urlString = RootURL + "diseases/" + String.valueOf(did)+"/advice/"+String.valueOf(sid)+"?uid="+uid;
		String jsonString = HttpUtil.executeHttpGet(urlString);
		return jsonString;
	}
	
	public static String deleteSolutionComment(int did,int sid,int cid){
		String urlString = RootURL + "diseases/"+did+"/advice/"+sid+"/comments/"+cid+"/delete";
		String result =HttpUtil.executeHttpDelete(urlString);
		return result;
	}

	public static String checkUserIsExist(String phone) throws Throwable {
		User user = new User();
		user.setPhone(phone);
		String jsonString = JsonUtil.convertObjectToJson(user);
		String resultString = HttpUtil.executeHttpPut(RootURL + "users/check",
				jsonString);
		return resultString;
	}

	public static NetResponse login(String phone, String password) throws Throwable {
		User user = new User();
		user.setPhone(phone);
		user.setPassword(password);
		String jsonString = JsonUtil.convertObjectToJson(user,
				"yyyy-MM-dd HH:mm:ss", new String[] { User.class.toString() });
		return HttpUtil.executeUserPost(RootURL + "users/login", jsonString);
	}
	
	/**
	 * 检查别名是否存在
	 * @param alias
	 * @return
	 */
	public static NetResponse checkAlias(final String alias) {
		String jsonString = null;
		String urlString = RootURL + "users/checkname";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		NameValuePair uidValuePair1 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return alias;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "alias";
			}
		};
		
		nameValuePairs.add(uidValuePair1);
		
		try {
			return HttpUtil.executeHttpPost(urlString, nameValuePairs);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			NetResponse netResponse = new NetResponse();
			netResponse.setStatus(0);
			netResponse.setMessage(e.getMessage());
			return netResponse;
		}
	}

	/**
	 * @param user
	 * @return
	 * @throws Throwable
	 */
	public static NetResponse registerUser(User user) throws Throwable {
		String urlString = RootURL + "users";
		String jsonString = JsonUtil.convertObjectToJson(user,
				"yyyy-MM-dd HH:mm:ss", new String[] { User.class.toString() });
		return HttpUtil.executeUserPost(urlString, jsonString);
	}
	
	public static NetResponse modifyUser(User user) throws Throwable {
		String urlString = RootURL + "users/"+user.getId();
		String jsonString = JsonUtil.convertObjectToJson(user,
				"yyyy-MM-dd HH:mm:ss", new String[] { User.class.toString() });
		return HttpUtil.executeHttpPost(urlString, jsonString);
	}

	public static String getSmsCheckNum(String phone,int n) {
		String urlString = RootURL + "users/checkcode?phone=" + phone+"&n="+n;
		String jsonString = HttpUtil.executeHttpGetNoHead(urlString);
		if(jsonString!=null){
			return jsonString.trim();
		}
		else{
			return null;
		}
	}

	public static void zanResponse(String uid,int qid, int rid) {
		String urlString = RootURL + "questions/" + String.valueOf(qid)
				+ "/responses/" + String.valueOf(rid) + "/zan";
		String jsonString = "{\"id\":\""
				+ uid + "\"}";
		try {
			HttpUtil.executeHttpPut(urlString, jsonString);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void cancelZanResponse(String uid,int qid, int rid) {
		String urlString = RootURL + "questions/" + String.valueOf(qid)
				+ "/responses/" + String.valueOf(rid) + "/cancelzan?uid="
				+ uid;
		try {
			HttpUtil.executeHttpDelete(urlString);
		} catch (Throwable e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	/**
	 * 赞同回复
	 * @param qid
	 * @param rid
	 * @return
	 */
	public static NetResponse agreeResponse(int qid,int rid,final String uid) {
		String jsonString = null;
		String urlString = RootURL + "questions/"+qid+"/responses/"+rid+"/agree";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		NameValuePair uidValuePair1 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return uid;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "uid";
			}
		};
		
		nameValuePairs.add(uidValuePair1);
		
		try {
			return HttpUtil.executeHttpPost(urlString, nameValuePairs);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			NetResponse netResponse = new NetResponse();
			netResponse.setStatus(0);
			netResponse.setMessage(e.getMessage());
			return netResponse;
		}
	}
	
	/**
	 * 赞同回复
	 * @param qid
	 * @param rid
	 * @return
	 */
	public static NetResponse agreeResponseForGossip(int qid,int rid,final String uid) {
		String jsonString = null;
		String urlString = RootURL + "gossips/"+qid+"/responses/"+rid+"/agree";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		NameValuePair uidValuePair1 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return uid;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "uid";
			}
		};
		
		nameValuePairs.add(uidValuePair1);
		
		try {
			return HttpUtil.executeHttpPost(urlString, nameValuePairs);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			NetResponse netResponse = new NetResponse();
			netResponse.setStatus(0);
			netResponse.setMessage(e.getMessage());
			return netResponse;
		}
	}
	
	/**
	 * 赞同种植分享回复
	 * @param qid
	 * @param rid
	 * @return
	 */
	public static NetResponse agreeResponseForPlant(int qid,int rid,final String uid) {
		String jsonString = null;
		String urlString = RootURL + "plantinfo/"+qid+"/responses/"+rid+"/agree";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		NameValuePair uidValuePair1 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return uid;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "uid";
			}
		};
		
		nameValuePairs.add(uidValuePair1);
		
		try {
			return HttpUtil.executeHttpPost(urlString, nameValuePairs);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			NetResponse netResponse = new NetResponse();
			netResponse.setStatus(0);
			netResponse.setMessage(e.getMessage());
			return netResponse;
		}
	}
	
	/**
	 * 反对回复
	 * @param qid
	 * @param rid
	 * @return
	 */
	public static NetResponse disagreeResponse(int qid,int rid,final String uid) {
		String jsonString = null;
		String urlString = RootURL + "questions/"+qid+"/responses/"+rid+"/disagree";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		NameValuePair uidValuePair1 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return uid;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "uid";
			}
		};
		
		nameValuePairs.add(uidValuePair1);
		
		try {
			return HttpUtil.executeHttpPost(urlString, nameValuePairs);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			NetResponse netResponse = new NetResponse();
			netResponse.setStatus(0);
			netResponse.setMessage(e.getMessage());
			return netResponse;
		}
	}
	
	/**
	 * 反对回复
	 * @param qid
	 * @param rid
	 * @return
	 */
	public static NetResponse disagreeResponseForGossip(int qid,int rid,final String uid) {
		String jsonString = null;
		String urlString = RootURL + "gossips/"+qid+"/responses/"+rid+"/disagree";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		NameValuePair uidValuePair1 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return uid;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "uid";
			}
		};
		
		nameValuePairs.add(uidValuePair1);
		
		try {
			return HttpUtil.executeHttpPost(urlString, nameValuePairs);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			NetResponse netResponse = new NetResponse();
			netResponse.setStatus(0);
			netResponse.setMessage(e.getMessage());
			return netResponse;
		}
	}
	
	/**
	 * 收藏问题
	 * @return
	 */
	public static NetResponse favoriteQue(int qid,final String uid,final boolean add) {
		String jsonString = null;
		String urlString = RootURL + "questions/"+qid+"/favorite";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		NameValuePair uidValuePair1 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return uid;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "uid";
			}
		};
		NameValuePair addValuePair1 = new NameValuePair() {

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "add";
			}

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return String.valueOf(add);
			}

			
		};
		
		nameValuePairs.add(uidValuePair1);
		nameValuePairs.add(addValuePair1);
		
		try {
			return HttpUtil.executeHttpPost(urlString, nameValuePairs);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			NetResponse netResponse = new NetResponse();
			netResponse.setStatus(0);
			netResponse.setMessage(e.getMessage());
			return netResponse;
		}
	}
	
	/**
	 * 采纳回复
	 * @param qid
	 * @param rid
	 * @return
	 */
	public static NetResponse adoptResponse(int qid,int rid,final String uid) {
		String jsonString = null;
		String urlString = RootURL + "questions/"+qid+"/responses/"+rid+"/adopt";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		NameValuePair uidValuePair1 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return uid;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "uid";
			}
		};
		
		nameValuePairs.add(uidValuePair1);
		
		try {
			return HttpUtil.executeHttpPost(urlString, nameValuePairs);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			NetResponse netResponse = new NetResponse();
			netResponse.setStatus(0);
			netResponse.setMessage(e.getMessage());
			return netResponse;
		}
	}
	
	/**
	 * 采纳回复
	 * @param qid
	 * @param rid
	 * @return
	 */
	public static NetResponse adoptResponseForGossip(int qid,int rid,final String uid) {
		String jsonString = null;
		String urlString = RootURL + "gossips/"+qid+"/responses/"+rid+"/adopt";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		NameValuePair uidValuePair1 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return uid;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "uid";
			}
		};
		
		nameValuePairs.add(uidValuePair1);
		
		try {
			return HttpUtil.executeHttpPost(urlString, nameValuePairs);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			NetResponse netResponse = new NetResponse();
			netResponse.setStatus(0);
			netResponse.setMessage(e.getMessage());
			return netResponse;
		}
	}
	
	/**
	 * 给分享点赞
	 * @param qid
	 * @param rid
	 * @return
	 */
	public static NetResponse agreePlant(int qid,final String uid) {
		String jsonString = null;
		String urlString = RootURL + "plantinfo/"+qid+"/agree";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		NameValuePair uidValuePair1 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return uid;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "uid";
			}
		};
		
		nameValuePairs.add(uidValuePair1);
		
		try {
			return HttpUtil.executeHttpPost(urlString, nameValuePairs);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			NetResponse netResponse = new NetResponse();
			netResponse.setStatus(0);
			netResponse.setMessage(e.getMessage());
			return netResponse;
		}
	}
	
	/**
	 * 评论回复
	 * @param qid
	 * @param rid
	 * @return
	 */
	public static NetResponse commentResponse(int qid,int rid,final String uid,final String content) {
		String jsonString = null;
		String urlString = RootURL + "questions/"+qid+"/responses/"+rid+"/comment";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		NameValuePair uidValuePair1 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return uid;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "uid";
			}
		};
		
		nameValuePairs.add(uidValuePair1);
		
		NameValuePair cotentValuePair1 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return content;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "content";
			}
		};
		
		nameValuePairs.add(cotentValuePair1);
		
		try {
			return HttpUtil.executeHttpPost(urlString, nameValuePairs);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			NetResponse netResponse = new NetResponse();
			netResponse.setStatus(0);
			netResponse.setMessage(e.getMessage());
			return netResponse;
		}
	}
	
	/**
	 * 评论回复
	 * @param qid
	 * @param rid
	 * @return
	 */
	public static NetResponse commentGossipResponse(int qid,int rid,final String uid,final String content) {
		String jsonString = null;
		String urlString = RootURL + "gossips/"+qid+"/responses/"+rid+"/comment";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		NameValuePair uidValuePair1 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return uid;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "uid";
			}
		};
		
		nameValuePairs.add(uidValuePair1);
		
		NameValuePair cotentValuePair1 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return content;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "content";
			}
		};
		
		nameValuePairs.add(cotentValuePair1);
		
		try {
			return HttpUtil.executeHttpPost(urlString, nameValuePairs);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			NetResponse netResponse = new NetResponse();
			netResponse.setStatus(0);
			netResponse.setMessage(e.getMessage());
			return netResponse;
		}
	}
	
	/**
	 * 评论种植分享一级评价
	 * @param qid
	 * @param rid
	 * @return
	 */
	public static NetResponse commentPlantResponse(int qid,int rid,final String uid,final String content) {
		String jsonString = null;
		String urlString = RootURL + "plantinfo/"+qid+"/responses/"+rid+"/comment";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		NameValuePair uidValuePair1 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return uid;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "uid";
			}
		};
		
		nameValuePairs.add(uidValuePair1);
		
		NameValuePair cotentValuePair1 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return content;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "content";
			}
		};
		
		nameValuePairs.add(cotentValuePair1);
		
		try {
			return HttpUtil.executeHttpPost(urlString, nameValuePairs);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			NetResponse netResponse = new NetResponse();
			netResponse.setStatus(0);
			netResponse.setMessage(e.getMessage());
			return netResponse;
		}
	}

	public static String getRecipe(String nzdCode, int rid) {
		String urlString = RootURL + "users/" + nzdCode
				+ "/recipes/" + String.valueOf(rid);
		String jsonString = HttpUtil.executeHttpGet(urlString);
		return jsonString;
	}
	/** 订购药方
	 * @param nzdid 农资点用户id
	 * @param rid 药方id
	 * @param uid 用户id
	 * @param count 数量
	 * @return 返回订单
	 */
	public static NetResponse orderRecipe(String nzdid, int rid, final String uid, final int count) {
		String jsonString = null;
		String urlString = RootURL + "users/" + nzdid
				+ "/recipes/" + String.valueOf(rid) + "/order";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		NameValuePair nameValuePair1 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return uid;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "uid";
			}
		};
		NameValuePair nameValuePair2 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return String.valueOf(count);
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "count";
			}
		};
		nameValuePairs.add(nameValuePair1);
		nameValuePairs.add(nameValuePair2);
		try {
			return HttpUtil.executeHttpPost(urlString, nameValuePairs);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			NetResponse netResponse = new NetResponse();
			netResponse.setStatus(0);
			netResponse.setMessage(e.getMessage());
			return netResponse;
		}
	}

	public static Bitmap getRecipeQRCode(String nzdId, int recipeId, String userId,
			String qrCode) {
		Bitmap bitmap = null;
		String urlString = RootURL + "users/" + nzdId
				+ "/recipes/" + String.valueOf(recipeId) + "/order/"+qrCode+"/QR";
		bitmap=HttpUtil.getBitmap(urlString);
		return bitmap;
	}
	/**
	 * 获取用户信息
	 * @param userId
	 * @return
	 */
	public static String getUser(String userId) {
		String jsonString=HttpUtil.executeHttpGet(RootURL+"users/"+userId);
		return jsonString;
	}

	public static boolean updateUser(UpdateUser updateUser) {
		String urlString = RootURL + "users/update";
		String jsonString = JsonUtil.convertObjectToJson(updateUser,
				"yyyy-MM-dd HH:mm:ss",new String[]{NetImage.class.toString()}) ;
		 try {
			HttpUtil.executeHttpPost(urlString, jsonString);
			return true;
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}		
	}

	public static NetResponse getUsers(List<GFMessage> messages) {
		String jsonString="[ ";
		for (GFMessage emMessage : messages) {
			jsonString=jsonString+"\""+emMessage.getTitle()+"\",";
		}
		jsonString=jsonString.substring(0, jsonString.length()-1);
		jsonString=jsonString+" ]";
		
		try {
			return HttpUtil.executeHttpPost(RootURL+"users/inphones", jsonString);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			NetResponse netResponse = new NetResponse();
			netResponse.setStatus(0);
			netResponse.setMessage(e.getMessage());
			return netResponse;
		}
	}
	public static NetResponse getUserByPhone(String phone) {
		String jsonString="[ \""+phone+"\" ]";
		try {
			return HttpUtil.executeHttpPost(RootURL+"users/inphones", jsonString);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			NetResponse netResponse = new NetResponse();
			netResponse.setStatus(0);
			netResponse.setMessage(e.getMessage());
			return netResponse;
		}
	}
	
	public static String getAreaString(double x,double y) {
		
		String url = RootURL + "zone/my?x="+x+"&y="+y;
		String jsonString = HttpUtil.executeHttpGet(url);
		return jsonString;
	}
	
	public static String getStoresString(double x,double y) {
		
		String url = RootURL + "users/around?x="+x+"&y="+y;
		String jsonString = HttpUtil.executeHttpGet(url);
		return jsonString;
	}
	
	public static String getMoreStoresString(double x,double y,int position) {
		
		String url = RootURL + "users/around?x="+x+"&y="+y+"&position="+position;
		String jsonString = HttpUtil.executeHttpGet(url);
		return jsonString;
	}
	
	public static String getAllStoresString(double x,double y) {
		
		String url = RootURL + "users/nzds?x="+x+"&y="+y;
		String jsonString = HttpUtil.executeHttpGet(url);
		return jsonString;
	}
	
	public static String getAllMoreStoresString(double x,double y,int position) {
		
		String url = RootURL + "users/nzds?x="+x+"&y="+y+"&position="+position;
		String jsonString = HttpUtil.executeHttpGet(url);
		return jsonString;
	}
	
	public static String getRecipesByUid(String uid){
		String url = RootURL + "users/"+uid+"/recipes/checked";
		String jsonString = HttpUtil.executeHttpGet(url);
		return jsonString;
	}
	
	public static String getMyOrders(String uid){
		String url = RootURL + "users/"+uid+"/myorders";
		String jsonString = HttpUtil.executeHttpGet(url);
		return jsonString;
	}
	
	public static String getScoringOrders(String uid){
		String url = RootURL + "users/"+uid+"/scoringorders";
		String jsonString = HttpUtil.executeHttpGet(url);
		return jsonString;
	}
	
	public static String getCompleteOrders(String uid){
		String url = RootURL + "users/"+uid+"/completedorders";
		String jsonString = HttpUtil.executeHttpGet(url);
		return jsonString;
	}
	
	public static String getOrderByCode(String uid,String code){
		String url = RootURL + "users/"+uid+"/order/"+code;
		String jsonString = HttpUtil.executeHttpGet(url);
		return jsonString;
	}
	
	public static String deleteOrder (String nzdId, int recipeId, int orderid) {
		String urlString = RootURL + "users/" + nzdId
				+ "/recipes/" + String.valueOf(recipeId) + "/order/"+String.valueOf(orderid)+"/delete";
		String result =HttpUtil.executeHttpDelete(urlString);
		return result;
	}
	
	public static String getNyss(String uid,int position){
		
		String urlString ="";
		if(position>0){
			urlString = RootURL + "users/nysm?exid="+uid+"&position="+position;
		}
		else{
			urlString = RootURL + "users/nysm?exid="+uid;
		}
		String result =HttpUtil.executeHttpGet(urlString);
		return result;
		
	}
	
	public static String getZj(String uid,int position){
		
		String urlString ="";
		if(position>0){
			urlString = RootURL + "users/zjm?exid="+uid+"&position="+position;
		}
		else{
			urlString = RootURL + "users/zjm?exid="+uid;
		}
		String result =HttpUtil.executeHttpGet(urlString);
		return result;
		
	}
	
	public static String getFollowids(String uid){
		
		String urlString = RootURL + "users/"+uid+"/followids";
		String result =HttpUtil.executeHttpGet(urlString);
		return result;
		
	}
	
	public static String getFollows(String uid){
		
		String urlString = RootURL + "users/"+uid+"/follows";
		String result =HttpUtil.executeHttpGet(urlString);
		return result;
		
	}
	
	public static String getFans(String uid){
		
		String urlString = RootURL + "users/"+uid+"/fans";
		String result =HttpUtil.executeHttpGet(urlString);
		return result;
		
	}
	
	public static NetResponse follow(String uid,Nys nys){
		String urlString = RootURL + "users/"+uid+"/follow";
		Gson sGson=new Gson();
		String jsonString=sGson.toJson(nys);
		try {
			return HttpUtil.executeHttpPost(urlString, jsonString);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			NetResponse netResponse = new NetResponse();
			netResponse.setStatus(0);
			netResponse.setMessage(e.getMessage());
			return netResponse;
		}
	}
	
	public static NetResponse follow(String uid,User user){
		String urlString = RootURL + "users/"+uid+"/follow";
		Gson sGson=new Gson();
		String jsonString=sGson.toJson(user);
		try {
			return HttpUtil.executeHttpPost(urlString, jsonString);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			NetResponse netResponse = new NetResponse();
			netResponse.setStatus(0);
			netResponse.setMessage(e.getMessage());
			return netResponse;
		}
	}
	
	public static String cancelfollow(String uid,Nys nys){
		String urlString = RootURL + "users/"+uid+"/cancelfollow?tid="+nys.getId();
		String result =HttpUtil.executeHttpDelete(urlString);
		return result;
	}
	
	public static String cancelfollow(String uid,User nys){
		String urlString = RootURL + "users/"+uid+"/cancelfollow?tid="+nys.getId();
		String result =HttpUtil.executeHttpDelete(urlString);
		return result;
	}
	
	public static String getAgrotechnicalCates(){
		
		String urlString = RootURL + "agrotechnicals/cates";
		String result =HttpUtil.executeHttpGet(urlString);
		return result;
		
	}
	
	public static String getAgrotechnical(int cid,int zone){
		
		String urlString = RootURL + "agrotechnicals?cate="+cid;
		if(zone!=0){
			urlString = urlString+"&zone="+zone;
		}
		String result =HttpUtil.executeHttpGet(urlString);
		return result;
		
	}
	
	public static String getMoreAgrotechnical(int fromid,int cid,int zone){
		
		String urlString = RootURL + "agrotechnicals?cate="+cid+"&fromid="+fromid;
		if(zone!=0){
			urlString = urlString+"&zone="+zone;
		}
		String result =HttpUtil.executeHttpGet(urlString);
		return result;
		
	}
	
	public static String getAgrotechnicalById(int id){
		
		String urlString = RootURL + "agrotechnicals/"+String.valueOf(id);
		String result =HttpUtil.executeHttpGet(urlString);
		return result;
		
	}
	
	public static String getAreas(){		
		String urlString = RootURL + "areas";
		String result =HttpUtil.executeHttpGet(urlString);
		return result;	
	}
	
	public static String getCates(){		
		String urlString = RootURL + "nyscate";
		String result =HttpUtil.executeHttpGet(urlString);
		return result;	
	}
	
	public static String getPointrule(){
		
		String urlString = HttpUtil.RootURL+"dics/pointrule";
		String result =HttpUtil.executeHttpGet(urlString);
		return result;
		
	}
	
	public static String getQuansString(String uid) {
		String jsonString = HttpUtil.executeHttpGet(RootURL+"users/"+uid + "/quans/timeline");
		return jsonString;
	}

	public static String getQuansString(String uid,int qid) {
		String jsonString = HttpUtil.executeHttpGet(RootURL+"users/"+uid
				+ "/quans/timeline?fromid=" + qid);
		return jsonString;
	}
	
	public static void sendQuan(String uid,Quan quan) throws Throwable {
		String jsonString = JsonUtil.convertObjectToJson(quan,
				"yyyy-MM-dd HH:mm:ss", new String[] {
						Quan.class.toString(), NetImage.class.toString() });
		HttpUtil.executeHttpPost(RootURL + "users/"+uid+"/quans", jsonString);
	}
	
	public static void pinlunQuan(String uid,int qid,Comment comment) throws Throwable {
		String jsonString = JsonUtil.convertObjectToJson(comment,
				"yyyy-MM-dd HH:mm:ss", new String[] {
						Comment.class.toString(), NetImage.class.toString() });
		HttpUtil.executeHttpPost(RootURL + "users/"+uid+"/quans/"+qid+"/comment", jsonString);
	}
	
	public static Bitmap getPayQRCode(String uid,int currency) {
		Bitmap bitmap = null;
		String urlString = RootURL + "users/" + uid
				+ "/pay?currency="+currency;
		bitmap=HttpUtil.getBitmap(urlString);
		return bitmap;
	}
	
	public static String cancelPay(String uid){
		String urlString = RootURL + "users/"+uid+"/paycancel";
		String result =HttpUtil.executeHttpDelete(urlString);
		return result;
	}
	
	public static String checkPay(String uid){
		String urlString = RootURL + "users/"+uid+"/paycheck";
		String result =HttpUtil.executeHttpGet(urlString);
		return result;
	}
	
	public static String incomeCurrency(final String code,String uid) throws Throwable {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		NameValuePair nameValuePair = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return code;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "code";
			}
		};
		nameValuePairs.add(nameValuePair);
		String resultString = HttpUtil.executeHttpPut(RootURL + "users/"+uid+"/income",
				nameValuePairs);
		return resultString;
	}
	
	public static String getNysQuansString(String uid) {
		String jsonString = HttpUtil.executeHttpGet(RootURL+"users/"+uid + "/quans/my");
		return jsonString;
	}

	public static String getNysQuansString(String uid,int qid) {
		String jsonString = HttpUtil.executeHttpGet(RootURL+"users/"+uid
				+ "/quans/my?fromid=" + qid);
		return jsonString;
	}
	
	public static String getMyrepliedquestions(String uid,int page){
		String jsonString = HttpUtil.executeHttpGet(RootURL+"users/"+uid + "/repliedquestions?page="+page);
		return jsonString;
	}
	
	public static NetResponse modifyPass(final String newpass,String uid) {
		String jsonString = null;
		String urlString = RootURL + "users/" + uid
				+ "/modifypass";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		NameValuePair nameValuePair1 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return newpass;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "newpass";
			}
		};
		
		nameValuePairs.add(nameValuePair1);
		try {
			return HttpUtil.executeHttpPost(urlString, nameValuePairs);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			NetResponse netResponse = new NetResponse();
			netResponse.setStatus(0);
			netResponse.setMessage(e.getMessage());
			return netResponse;
		}
	}
	
	public static String passback(String phone,int n) {
		String jsonString = HttpUtil.executeHttpGet(RootURL+"users/passback?phone="+phone+"&n="+n);
		return jsonString;
	}
	
	public static String getNewSecondOrders(int zone){
		String url = RootURL + "seconds/recentOrder?size=10";
		if(zone!=0){
			url = url +"&zone="+zone;
		}
		String jsonString = HttpUtil.executeHttpGet(url);
		return jsonString;
	}
	public static String getNewZfbtOrders(int zone){
		String url = RootURL + "zfbt/recentOrder?size=10";
		if(zone!=0){
			url = url+"&zone="+zone;
		}
		String jsonString = HttpUtil.executeHttpGet(url);
		return jsonString;
	}
	
	public static String getSeconds(double x,double y,int zone){
		String url = RootURL + "seconds?x="+x+"&y="+y;
		if(zone!=0){
			url = url+"&zone="+zone;
		}
		String jsonString = HttpUtil.executeHttpGet(url);
		return jsonString;
	}
	
	public static String getZfbt(double x,double y,int page,int zone){
		String url = RootURL + "zfbt?x="+x+"&y="+y+"&page="+page;
		if(zone!=0){
			url = url +"&zone="+zone;
		}
		String jsonString = HttpUtil.executeHttpGet(url);
		return jsonString;
	}
	
	public static String getRecipes(double x,double y,int position){
		String url = RootURL + "users/around/recipes?x="+x+"&y="+y+"&position="+position;
		String jsonString = HttpUtil.executeHttpGet(url);
		return jsonString;
	}
	
	public static String getMoreSeconds(double x,double y,int fromid,int zone){
		String url = RootURL + "seconds?x="+x+"&y="+y+"&fromid="+fromid;
		if(zone!=0){
			url = url +"&zone="+zone;
		}
		String jsonString = HttpUtil.executeHttpGet(url);
		return jsonString;
	}
	
	public static NetResponse buySecond(final String uid,int sid){
		
		String jsonString = null;
		String urlString = RootURL + "seconds/"+sid+"/buy";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		NameValuePair nameValuePair1 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return uid;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "uid";
			}
		};
		
		nameValuePairs.add(nameValuePair1);
		try {
			return HttpUtil.executeHttpPost(urlString, nameValuePairs);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			NetResponse netResponse = new NetResponse();
			netResponse.setStatus(0);
			netResponse.setMessage(e.getMessage());
			return netResponse;
		}
	}
	
	public static NetResponse buyZfbt(final String uid,int sid,final int coupon,final String nzdid,final int cid){
		
		String jsonString = null;
		String urlString = RootURL + "zfbt/"+sid+"/buy";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		NameValuePair nameValuePair1 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return uid;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "uid";
			}
		};
		
		nameValuePairs.add(nameValuePair1);
		if(coupon>0){
			NameValuePair nameValuePair2 = new NameValuePair() {

				@Override
				public String getValue() {
					// TODO Auto-generated method stub
					return String.valueOf(coupon);
				}

				@Override
				public String getName() {
					// TODO Auto-generated method stub
					return "coupon";
				}
			};
			
			nameValuePairs.add(nameValuePair2);
		}
		if(nzdid!=null){
			NameValuePair nameValuePair3 = new NameValuePair() {

				@Override
				public String getValue() {
					// TODO Auto-generated method stub
					return nzdid;
				}

				@Override
				public String getName() {
					// TODO Auto-generated method stub
					return "nzd";
				}
			};
			
			nameValuePairs.add(nameValuePair3);
		}
		if(cid!=0){
			NameValuePair nameValuePair4 = new NameValuePair() {

				@Override
				public String getValue() {
					// TODO Auto-generated method stub
					return String.valueOf(cid);
				}

				@Override
				public String getName() {
					// TODO Auto-generated method stub
					return "cid";
				}
			};
			
			nameValuePairs.add(nameValuePair4);
		}
		try {
			return HttpUtil.executeHttpPost(urlString, nameValuePairs);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			NetResponse netResponse = new NetResponse();
			netResponse.setStatus(0);
			netResponse.setMessage(e.getMessage());
			return netResponse;
		}
	}
	
	public static NetResponse confirmZfbtOrder(final String uid,int mid,final int oid){
		
		String jsonString = null;
		String urlString = RootURL + "zfbt/"+mid+"/order/confirm";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		NameValuePair nameValuePair1 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return uid;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "uid";
			}
		};
		
		nameValuePairs.add(nameValuePair1);
		
		NameValuePair nameValuePair2 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return String.valueOf(oid);
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "oid";
			}
		};
		
		nameValuePairs.add(nameValuePair1);
		nameValuePairs.add(nameValuePair2);
		try {
			return HttpUtil.executeHttpPost(urlString, nameValuePairs);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			NetResponse netResponse = new NetResponse();
			netResponse.setStatus(0);
			netResponse.setMessage(e.getMessage());
			return netResponse;
		}
	}
	
	public static Bitmap getSecondQRCode(int sid,String qrCode) {
		Bitmap bitmap = null;
		String urlString = RootURL + "seconds/"+sid+"/order/"+qrCode+"/QR";
		bitmap=HttpUtil.getBitmap(urlString);
		return bitmap;
	}
	
	public static String getMySeconds(String uid){
		String url = RootURL + "users/"+uid+"/myseconds";
		String jsonString = HttpUtil.executeHttpGet(url);
		return jsonString;
	}
	
	public static String getSecondOrderByCode(String uid,String code){
		String url = RootURL + "users/"+uid+"/second/"+code;
		String jsonString = HttpUtil.executeHttpGet(url);
		return jsonString;
	}
	
	public static String getMyPostOrders(String uid){
		String url = RootURL + "users/"+uid+"/mypostzfbt";
		String jsonString = HttpUtil.executeHttpGet(url);
		return jsonString;
	}
	
	public static String deleteSecondOrder (int secondid, int orderid) {
		String urlString = RootURL + "seconds/" + secondid + "/order/"+String.valueOf(orderid)+"/delete";
		String result =HttpUtil.executeHttpDelete(urlString);
		return result;
	}
	
	public static Bitmap getZfbtQRCode(int sid,String qrCode) {
		Bitmap bitmap = null;
		String urlString = RootURL + "zfbt/"+sid+"/order/"+qrCode+"/QR";
		bitmap=HttpUtil.getBitmap(urlString);
		return bitmap;
	}
	
	public static String getMyZfbts(String uid){
		String url = RootURL + "users/"+uid+"/myzfbt";
		String jsonString = HttpUtil.executeHttpGet(url);
		return jsonString;
	}
	
	public static String getServerTime(){
		String url = RootURL + "users/time";
		String jsonString = HttpUtil.executeHttpGet(url);
		return jsonString;
	}
	
	public static String getAppVersion(){
		String url = RootURL + "apps/android";
		String jsonString = HttpUtil.executeHttpGet(url);
		return jsonString;
	}
	
	public static String signIn(String uid){
		String url = RootURL + "users/"+uid+"/signin";
		String jsonString = HttpUtil.executeHttpGet(url);
		return jsonString;
	}
	
	public static String deleteQuestion(int qid){
		String urlString = RootURL + "questions/"+qid+"/delete";
		String result =HttpUtil.executeHttpDelete(urlString);
		return result;
	}
	public static String deleteGossip(int qid){
		String urlString = RootURL + "gossips/"+qid+"/delete";
		String result =HttpUtil.executeHttpDelete(urlString);
		return result;
	}
	public static String deletePlant(int qid){
		String urlString = RootURL + "plantinfo/"+qid+"/delete";
		String result =HttpUtil.executeHttpDelete(urlString);
		return result;
	}
	
	public static String deleteResponse(int qid,int rid){
		String urlString = RootURL + "questions/"+qid+"/responses/"+rid+"/delete";
		String result =HttpUtil.executeHttpDelete(urlString);
		return result;
	}
	
	public static String deleteResponseForGossip(int qid,int rid){
		String urlString = RootURL + "gossips/"+qid+"/responses/"+rid+"/delete";
		String result =HttpUtil.executeHttpDelete(urlString);
		return result;
	}
	public static String deleteResponseForPlant(int qid,int rid){
		String urlString = RootURL + "plantinfo/"+qid+"/responses/"+rid+"/delete";
		String result =HttpUtil.executeHttpDelete(urlString);
		return result;
	}
	
	public static String deleteQuan(String uid,int qid){
		String urlString = RootURL +"users/"+uid+ "/quans/"+qid;
		String result =HttpUtil.executeHttpDelete(urlString);
		return result;
	}
	
	public static String getPtoc(){
		String urlString = RootURL + "dics/ptoc";
		String result =HttpUtil.executeHttpGet(urlString);
		return result;
	}
	
	public static String getScoringdics(){
		String url = RootURL + "dics/scoringdics";
		String jsonString = HttpUtil.executeHttpGet(url);
		return jsonString;
	}
	
	public static NetResponse exchange(final int points,String uid) {
		String jsonString = null;
		String urlString = RootURL + "users/" + uid
				+ "/ptoc";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		NameValuePair nameValuePair1 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return String.valueOf(points);
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "points";
			}
		};
		
		nameValuePairs.add(nameValuePair1);
		try {
			return HttpUtil.executeHttpPost(urlString, nameValuePairs);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			NetResponse netResponse = new NetResponse();
			netResponse.setStatus(0);
			netResponse.setMessage(e.getMessage());
			return netResponse;
		}
	}
	
	/**
	 * 订单评价
	 * @return
	 */
	public static NetResponse sendEvaluate(String nzdid,final int urid,int rid,final int scoring,final String evaluate) {
		String jsonString = null;
		String urlString = RootURL + "users/" + nzdid
				+ "/recipes/"+rid+"/order/evaluate";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		NameValuePair nameValuePair1 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return String.valueOf(urid);
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "oid";
			}
		};
		nameValuePairs.add(nameValuePair1);
		NameValuePair nameValuePair2 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return String.valueOf(scoring);
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "scoring";
			}
		};
		nameValuePairs.add(nameValuePair2);
		NameValuePair nameValuePair3 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return evaluate;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "evaluate";
			}
		};
		nameValuePairs.add(nameValuePair3);
		try {
			return HttpUtil.executeHttpPost(urlString, nameValuePairs);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			NetResponse netResponse = new NetResponse();
			netResponse.setStatus(0);
			netResponse.setMessage(e.getMessage());
			return netResponse;
		}
	}
	
	/**
	 * 举报
	 * @return
	 */
	public static NetResponse sendReport(int status,final String uid,final int qid,int rid,final String report) {
		String urlString;
		if (status==0) {
			urlString = RootURL + "questions/" + qid + "/responses/" + rid
					+ "/report";
		}
		else{
			urlString = RootURL + "gossips/" + qid + "/responses/" + rid
					+ "/report";
		}
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		NameValuePair nameValuePair1 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return uid;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "uid";
			}
		};
		nameValuePairs.add(nameValuePair1);
		NameValuePair nameValuePair2 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return report;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "reason";
			}
		};
		nameValuePairs.add(nameValuePair2);
		
		try {
			return HttpUtil.executeHttpPost(urlString, nameValuePairs);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			NetResponse netResponse = new NetResponse();
			netResponse.setStatus(0);
			netResponse.setMessage(e.getMessage());
			return netResponse;
		}
	}
	
	/**
	 * 获取配方的评价
	 * @param nzdCode
	 * @param rid
	 * @param position
	 * @return
	 */
	public static String getEvaluates(String nzdCode, int rid,int position) {
		String urlString = RootURL + "users/" + nzdCode
				+ "/recipes/" + String.valueOf(rid)+"/evaluates";
		if(position>0){
			urlString+="?position="+position;
		}
		String jsonString = HttpUtil.executeHttpGet(urlString);
		return jsonString;
	}
	
	/**
	 * 获取积分商品
	 * @param position
	 * @return
	 */
	public static String getCommodities(int position,String uid,int zone){
		
		String urlString = RootURL + "commodities?position="+position+"&uid="+uid;
		if(zone!=0){
			urlString=urlString+"&zone="+zone;
		}
		String result =HttpUtil.executeHttpGet(urlString);
		return result;
		
	}
	
	/**
	 * 获取积分商品信息
	 * @param cid
	 * @return
	 */
	public static String getCommodity(int cid){
		
		String urlString = RootURL + "commodities/"+cid;
		String result =HttpUtil.executeHttpGet(urlString);
		return result;
		
	}
	
	/**
	 * 获取收货地址
	 * @param uid
	 * @return
	 */
	public static String getContacts(String uid){
		
		String urlString = RootURL + "contacts/"+uid;
		String result =HttpUtil.executeHttpGet(urlString);
		return result;
		
	}
	
	/**
	 * 添加收货地址
	 * @param newpass
	 * @param uid
	 * @return
	 */
	public static NetResponse addContact(final String uid,
			final String name,
			final String phone,
			final String post,
			final String address) {
		String jsonString = null;
		String urlString = RootURL + "contacts";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		NameValuePair uidValuePair1 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return uid;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "uid";
			}
		};
		
		nameValuePairs.add(uidValuePair1);
		
		NameValuePair nameValuePair = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return name;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "name";
			}
		};
		
		nameValuePairs.add(nameValuePair);
		NameValuePair phoneValuePair = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return phone;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "phone";
			}
		};
		
		nameValuePairs.add(phoneValuePair);
		
		NameValuePair addressValuePair = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return address;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "address";
			}
		};
		
		nameValuePairs.add(addressValuePair);
		
		NameValuePair postValuePair = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return post;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "postnumber";
			}
		};
		
		nameValuePairs.add(postValuePair);
		try {
			return HttpUtil.executeHttpPost(urlString, nameValuePairs);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			NetResponse netResponse = new NetResponse();
			netResponse.setStatus(0);
			netResponse.setMessage(e.getMessage());
			return netResponse;
		}
	}
	
	/**
	 * 编辑收货地址
	 * @param newpass
	 * @param uid
	 * @return
	 */
	public static NetResponse updateContact(final int cid,
			final String name,
			final String phone,
			final String post,
			final String address) {
		String jsonString = null;
		String urlString = RootURL + "contacts/"+cid;
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		
		NameValuePair nameValuePair = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return name;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "name";
			}
		};
		
		nameValuePairs.add(nameValuePair);
		NameValuePair phoneValuePair = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return phone;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "phone";
			}
		};
		
		nameValuePairs.add(phoneValuePair);
		
		NameValuePair addressValuePair = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return address;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "address";
			}
		};
		
		nameValuePairs.add(addressValuePair);
		
		NameValuePair postValuePair = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return post;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "postnumber";
			}
		};
		
		nameValuePairs.add(postValuePair);
		try {
			return HttpUtil.executeHttpPost(urlString, nameValuePairs);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			NetResponse netResponse = new NetResponse();
			netResponse.setStatus(0);
			netResponse.setMessage(e.getMessage());
			return netResponse;
		}
	}
	/**
	 * 删除收货地址
	 * @param cid
	 * @return
	 */
	public static String deleteContact(int cid){
		String urlString = RootURL + "contacts/"+cid;
		String result =HttpUtil.executeHttpDelete(urlString);
		return result;
	}
	
	/**
	 * 积分换商品
	 * @param uid
	 * @param contact
	 * @return
	 */
	public static NetResponse exChangeCommodity(final String uid,
			final int contact,
			final int commodityid) {
		String jsonString = null;
		String urlString = RootURL + "commodities/"+commodityid+"/exchange";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		NameValuePair uidValuePair1 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return uid;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "uid";
			}
		};
		
		nameValuePairs.add(uidValuePair1);
		
		NameValuePair nameValuePair = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return String.valueOf(contact);
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "contact";
			}
		};
		
		nameValuePairs.add(nameValuePair);
		NameValuePair phoneValuePair = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return "1";
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "count";
			}
		};
		
		nameValuePairs.add(phoneValuePair);
		
		try {
			return HttpUtil.executeHttpPost(urlString, nameValuePairs);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			NetResponse netResponse = new NetResponse();
			netResponse.setStatus(0);
			netResponse.setMessage(e.getMessage());
			return netResponse;
		}
	}
	
	/**
	 * 获取用户的积分订单
	 * @param uid
	 * @return
	 */
	public static String getPointOrders(String uid){
		String url = RootURL + "pointorders?uid="+uid;
		String jsonString = HttpUtil.executeHttpGet(url);
		return jsonString;
	}
	
	/**
	 * 结束订单
	 */
	public static String endPointOrder(int poid) throws Throwable {
		
		String resultString = HttpUtil.executeHttpPut(RootURL + "pointorders/"+poid+"/end",
				"");
		return resultString;
	}
	
	/**
	 * 意见反馈
	 */
	public static NetResponse feedBack(final String uid,
			final String content) {
		String jsonString = null;
		String urlString = RootURL + "feedback";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		NameValuePair uidValuePair1 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return uid;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "uid";
			}
		};
		
		nameValuePairs.add(uidValuePair1);
		
		NameValuePair nameValuePair = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return content;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "content";
			}
		};
		
		nameValuePairs.add(nameValuePair);
		
		try {
			return HttpUtil.executeHttpPost(urlString, nameValuePairs);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			NetResponse netResponse = new NetResponse();
			netResponse.setStatus(0);
			netResponse.setMessage(e.getMessage());
			return netResponse;
		}
	}
	
	/**
	 * 获取刮刮乐奖品列表
	 * @return
	 */
	public static String getGuaGua(int zone){
		
		String urlString = RootURL + "guagua/all";
		if(zone!=0){
			urlString = urlString+"?zone="+zone;
		}
		String result =HttpUtil.executeHttpGet(urlString);
		return result;
		
	}
	
	/**
	 * 抽签
	 */
	public static NetResponse qian(final String uid,int zone) {
		String jsonString = null;
		String urlString = RootURL + "guagua/qiang";
		if(zone!=0){
			urlString = urlString+"?zone="+zone;
		}
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		NameValuePair uidValuePair1 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return uid;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "uid";
			}
		};
		
		nameValuePairs.add(uidValuePair1);
		try {
			return HttpUtil.executeHttpPost(urlString, nameValuePairs);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			NetResponse netResponse = new NetResponse();
			netResponse.setStatus(0);
			netResponse.setMessage(e.getMessage());
			return netResponse;
		}
	}
	
	/**
	 * 刮奖确认
	 * @param uid
	 * @param contact
	 * @return
	 */
	public static NetResponse guaConfirm(final String uid,
			final int contact,
			final int oid) {
		String jsonString = null;
		String urlString = RootURL + "guagua/guaConfirm";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		NameValuePair uidValuePair1 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return uid;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "uid";
			}
		};
		
		nameValuePairs.add(uidValuePair1);
		
		NameValuePair nameValuePair = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return String.valueOf(contact);
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "cid";
			}
		};
		
		nameValuePairs.add(nameValuePair);
		NameValuePair phoneValuePair = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return String.valueOf(oid);
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "oid";
			}
		};
		
		nameValuePairs.add(phoneValuePair);
		
		try {
			return HttpUtil.executeHttpPost(urlString, nameValuePairs);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			NetResponse netResponse = new NetResponse();
			netResponse.setStatus(0);
			netResponse.setMessage(e.getMessage());
			return netResponse;
		}
	}
	
	/**
	 * 刮奖取消
	 * @param uid
	 * @param contact
	 * @return
	 */
	public static NetResponse guaCancel(final String uid,
			final int oid) {
		String jsonString = null;
		String urlString = RootURL + "guagua/guaCancel";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		NameValuePair uidValuePair1 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return uid;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "uid";
			}
		};
		
		nameValuePairs.add(uidValuePair1);
		
		NameValuePair phoneValuePair = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return String.valueOf(oid);
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "oid";
			}
		};
		
		nameValuePairs.add(phoneValuePair);
		
		try {
			return HttpUtil.executeHttpPost(urlString, nameValuePairs);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			NetResponse netResponse = new NetResponse();
			netResponse.setStatus(0);
			netResponse.setMessage(e.getMessage());
			return netResponse;
		}
	}
	
	/**
	 * 获取用户的刮奖订单
	 * @param uid
	 * @return
	 */
	public static String getGuaOrders(String uid){
		String url = RootURL + "guagua/orders?uid="+uid;
		String jsonString = HttpUtil.executeHttpGet(url);
		return jsonString;
	}
	
	/**
	 * 刮奖确认收货
	 * @param uid
	 * @param contact
	 * @return
	 */
	public static NetResponse guaOrderConfirm(final String uid,
			final int oid) {
		String jsonString = null;
		String urlString = RootURL + "guagua/orderConfirm";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		NameValuePair uidValuePair1 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return uid;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "uid";
			}
		};
		
		nameValuePairs.add(uidValuePair1);
		
		NameValuePair phoneValuePair = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return String.valueOf(oid);
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "oid";
			}
		};
		
		nameValuePairs.add(phoneValuePair);
		
		try {
			return HttpUtil.executeHttpPost(urlString, nameValuePairs);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			NetResponse netResponse = new NetResponse();
			netResponse.setStatus(0);
			netResponse.setMessage(e.getMessage());
			return netResponse;
		}
	}
	
	/**
	 * 获取刮刮乐奖品列表
	 * @return
	 */
	public static String getRecentOrders(int zone){
		
		String urlString = RootURL + "guagua/recentOrder?size=10";
		if(zone!=0){
			urlString = urlString+"&zone="+zone;
		}
		String result =HttpUtil.executeHttpGet(urlString);
		return result;
		
	}
	
	/**
	 * 根据作物获取病害
	 * @param cid
	 * @return
	 */
	public static String getDiseasesByCrop(int cid){
		String url = RootURL + "diseases/crop?id="+cid;
		String jsonString = HttpUtil.executeHttpGet(url);
		return jsonString;
	}
	
	/**
	 * 添加个人处方
	 * @param newpass
	 * @param uid
	 * @return
	 */
	public static NetResponse addPrescription(final String uid,
			final String title,
			final String content) {
		String jsonString = null;
		String urlString = RootURL + "prescription";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		NameValuePair uidValuePair1 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return uid;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "uid";
			}
		};
		
		nameValuePairs.add(uidValuePair1);
		
		NameValuePair nameValuePair = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return title;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "title";
			}
		};
		
		nameValuePairs.add(nameValuePair);
		NameValuePair phoneValuePair = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return content;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "content";
			}
		};
		
		nameValuePairs.add(phoneValuePair);
		
		try {
			return HttpUtil.executeHttpPost(urlString, nameValuePairs);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			NetResponse netResponse = new NetResponse();
			netResponse.setStatus(0);
			netResponse.setMessage(e.getMessage());
			return netResponse;
		}
	}
	
	/**
	 * 编辑个人处方
	 * @param newpass
	 * @param uid
	 * @return
	 */
	public static NetResponse editPrescription(final int id,
			final String title,
			final String content) {
		String jsonString = null;
		String urlString = RootURL + "prescription/"+id;
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		
		NameValuePair nameValuePair = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return title;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "title";
			}
		};
		
		nameValuePairs.add(nameValuePair);
		NameValuePair phoneValuePair = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return content;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "content";
			}
		};
		
		nameValuePairs.add(phoneValuePair);
		
		try {
			return HttpUtil.executeHttpPost(urlString, nameValuePairs);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			NetResponse netResponse = new NetResponse();
			netResponse.setStatus(0);
			netResponse.setMessage(e.getMessage());
			return netResponse;
		}
	}
	
	public static String getPrescriptions(String uid){
		
		String urlString ="";
		urlString = RootURL + "prescription/my?uid="+uid;
		String result =HttpUtil.executeHttpGet(urlString);
		return result;
	}
	
	public static String deletePrescription(int pid){
		String urlString = RootURL + "prescription/"+pid+"/delete";
		String result =HttpUtil.executeHttpDelete(urlString);
		return result;
	}
	
	public static NetResponse sharePoint(final String uid,final String url) {
		String urlString = RootURL + "share/points";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		NameValuePair uidValuePair1 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return uid;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "uid";
			}
		};
		
		nameValuePairs.add(uidValuePair1);
		
		NameValuePair cotentValuePair1 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return url;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "url";
			}
		};
		
		nameValuePairs.add(cotentValuePair1);
		
		try {
			return HttpUtil.executeHttpPost(urlString, nameValuePairs);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			NetResponse netResponse = new NetResponse();
			netResponse.setStatus(0);
			netResponse.setMessage(e.getMessage());
			return netResponse;
		}
	}
	
	public static NetResponse addForwardcount(final String folder,final int qid) {
		String urlString = RootURL + "share/"+folder;
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		NameValuePair uidValuePair1 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return String.valueOf(qid);
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "id";
			}
		};		
		nameValuePairs.add(uidValuePair1);
		try {
			return HttpUtil.executeHttpPost(urlString, nameValuePairs);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			NetResponse netResponse = new NetResponse();
			netResponse.setStatus(0);
			netResponse.setMessage(e.getMessage());
			return netResponse;
		}
	}
	
	/**
	 * 下载apk
	 * @param path
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public static File getFileFromServer(String downUrl, ProgressBar pd,Handler handler) throws Exception{   
	    //如果相等的话表示当前的sdcard挂载在手机上并且是可用的 
		String id = UUID.randomUUID().toString();
		String path = downUrl+"?id="+id;
	    if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){   
	        URL url = new URL(path);   
	        HttpURLConnection conn =  (HttpURLConnection) url.openConnection();   
	        conn.setConnectTimeout(5000);   
	        //获取到文件的大小   
	        int max = conn.getContentLength();
	        pd.setMax(max);   
	        InputStream is = conn.getInputStream(); 
	        String str[] = downUrl.split("/");
	        String filename = str[str.length-1];
	        File file = new File(Environment.getExternalStorageDirectory(), filename);   
	        FileOutputStream fos = new FileOutputStream(file);   
	        BufferedInputStream bis = new BufferedInputStream(is);   
	        byte[] buffer = new byte[1024];   
	        int len ;   
	        int total=0;   
	        while((len =bis.read(buffer))!=-1){   
	            fos.write(buffer, 0, len);   
	            total+= len;   
	            //获取当前下载量    
	            pd.setProgress(total); 
	            int[] values = {total,max};
	            handler.obtainMessage(1,values).sendToTarget(); 
	        }  
	        fos.close();   
	        bis.close();   
	        is.close();   
	        return file;   
	    }   
	    else{   
	        return null;   
	    }   
	}  
}
