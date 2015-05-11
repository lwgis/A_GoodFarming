package com.zhonghaodi.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
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




import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.NetImage;
import com.zhonghaodi.model.Question;
import com.zhonghaodi.model.Response;
import com.zhonghaodi.model.User;

/**
 * @author Administrator
 *
 */
public class HttpUtil {
	private static final String RootURL = "http://121.40.62.120:8080/dfyy/rest/";

	public static String executeHttpGet(String urlString) {
		StringBuffer sb = new StringBuffer();
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(urlString);
		get.addHeader("Content-type", "application/json;charset=UTF-8");
		get.addHeader("Accept-Charset", "utf-8");

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

	public static String executeHttpGetNoHead(String urlString) {
		StringBuffer sb = new StringBuffer();
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(urlString);
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
		// get.addHeader("Content-type", "application/json;charset=UTF-8");
		// get.addHeader("Accept-Charset", "utf-8");
		try {
			HttpResponse response = client.execute(delete);
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

	public static String executeHttpPost(String serviceUrl, String informjson)
			throws Throwable {
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
			return sb.toString();
		} else {
			throw new Exception("错误");
		}
	}

	public static String executeHttpPost(String serviceUrl,
			List<NameValuePair> paramList) throws Throwable {
		StringBuffer sb = new StringBuffer();
		HttpPost post = new HttpPost(serviceUrl);
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
			return sb.toString();
		} else {
			throw new Exception("错误");
		}
	}

	public static String executeHttpPut(String urlString, String informjson)
			throws Throwable {
		StringBuffer sb = new StringBuffer();
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPut put = new HttpPut(urlString);
		put.addHeader("Content-Type", "application/json;charset=UTF-8");
		put.addHeader("Accept-Charset", "utf-8");
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

	public static String getQuestionsString() {
		String jsonString = HttpUtil.executeHttpGet(RootURL + "questions");
		return jsonString;
	}

	public static String getQuestionsString(int qid) {
		String jsonString = HttpUtil.executeHttpGet(RootURL
				+ "questions?fromid=" + qid);
		return jsonString;
	}

	public static String getSingleQuestion(int qid) {
		String urlString = RootURL + "questions/" + String.valueOf(qid);
		String jsonString = HttpUtil.executeHttpGet(urlString);
		return jsonString;
	}

	public static String getAllCropsString() {
		String jsonString = HttpUtil.executeHttpGet(RootURL + "crops");
		return jsonString;
	}

	public static void sendQuestion(Question question) throws Throwable {
		String jsonString = JsonUtil.convertObjectToJson(question,
				"yyyy-MM-dd HH:mm:ss", new String[] {
						Question.class.toString(), NetImage.class.toString() });
		HttpUtil.executeHttpPost(RootURL + "questions", jsonString);
	}

	public static void sendResponse(Response response, int qid)
			throws Throwable {
		String urlString = RootURL + "questions/" + String.valueOf(qid)
				+ "/reply";
		String jsonString = JsonUtil.convertObjectToJson(response,
				"yyyy-MM-dd HH:mm:ss",
				new String[] { Response.class.toString(), });
		HttpUtil.executeHttpPost(urlString, jsonString);
	}

	public static String getDisease(int diseaseId) {
		String urlString = RootURL + "diseases/" + String.valueOf(diseaseId);
		String jsonString = HttpUtil.executeHttpGet(urlString);
		return jsonString;
	}

	public static String checkUserIsExist(String phone) throws Throwable {
		User user = new User();
		user.setPhone(phone);
		String jsonString = JsonUtil.convertObjectToJson(user);
		String resultString = HttpUtil.executeHttpPut(RootURL + "users/check",
				jsonString);
		return resultString;
	}

	public static String login(String phone, String password) throws Throwable {
		User user = new User();
		user.setPhone(phone);
		user.setPassword(password);
		String jsonString = JsonUtil.convertObjectToJson(user,
				"yyyy-MM-dd HH:mm:ss", new String[] { User.class.toString() });
		return HttpUtil.executeHttpPost(RootURL + "users/login", jsonString);
	}

	/**
	 * @param user
	 * @return
	 * @throws Throwable
	 */
	public static String registerUser(User user) throws Throwable {
		String urlString = RootURL + "users";
		String jsonString = JsonUtil.convertObjectToJson(user,
				"yyyy-MM-dd HH:mm:ss", new String[] { User.class.toString() });
		return HttpUtil.executeHttpPost(urlString, jsonString);
	}

	public static String getSmsCheckNum(String phone) {
		String urlString = RootURL + "users/checkcode?phone=" + phone;
		String jsonString = HttpUtil.executeHttpGetNoHead(urlString);
		return jsonString.trim();
	}

	public static void zanResponse(int qid, int rid) {
		String urlString = RootURL + "questions/" + String.valueOf(qid)
				+ "/responses/" + String.valueOf(rid) + "/zan";
		String jsonString = "{\"id\":"
				+ String.valueOf(GFUserDictionary.getUserId()) + "}";
		try {
			HttpUtil.executeHttpPut(urlString, jsonString);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void cancelZanResponse(int qid, int rid) {
		String urlString = RootURL + "questions/" + String.valueOf(qid)
				+ "/responses/" + String.valueOf(rid) + "/cancelzan?uid="
				+ String.valueOf(GFUserDictionary.getUserId());
		try {
			HttpUtil.executeHttpDelete(urlString);
		} catch (Throwable e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public static String getRecipe(int uid, int rid) {
		String urlString = RootURL + "users/" + String.valueOf(uid)
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
	public static String orderRecipe(int nzdid, int rid, final int uid, final int count) {
		String jsonString = null;
		String urlString = RootURL + "users/" + String.valueOf(nzdid)
				+ "/recipes/" + String.valueOf(rid) + "/order";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		NameValuePair nameValuePair1 = new NameValuePair() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return String.valueOf(uid);
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
			jsonString = HttpUtil.executeHttpPost(urlString, nameValuePairs);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return jsonString;
	}

	public static String getRecipeQRCode(int nzdId, int recipeId, int userId,
			String qrCode) {
		String jsonString = null;
		String urlString = RootURL + "users/" + String.valueOf(nzdId)
				+ "/recipes/" + String.valueOf(recipeId) + "/order/"+"de2cc4eb-50ff-4897-bbbf-ff1894c43098"+"/QR";
		jsonString=HttpUtil.executeHttpGetNoHead(urlString);
		return jsonString;
	}

	public static String getUser(int userId) {
		String jsonString=HttpUtil.executeHttpGet(RootURL + "users/" +String.valueOf(userId));
		return jsonString;
	}

}
