package com.zhonghaodi.networking;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;

public class ImageUtil {
	static final String imageUrl="http://121.40.62.120:8080/dfyy/rest/files/addimage";
	public static String uploadImage(Bitmap bitmap,String folder)throws Throwable {
		String result="";
		 String end = "\r\n";
		    String twoHyphens = "--";
		    String boundary = "******";
		    try
		    {
		      URL url = new URL(imageUrl);
		      HttpURLConnection httpURLConnection = (HttpURLConnection) url
		          .openConnection();
		      httpURLConnection.setDoInput(true);
		      httpURLConnection.setDoOutput(true);
		      httpURLConnection.setUseCaches(false);
		      httpURLConnection.setRequestMethod("POST");
		      httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
		      httpURLConnection.setRequestProperty("Charset", "UTF-8");
		      httpURLConnection.setRequestProperty("Content-Type",
		          "multipart/form-data;boundary=" + boundary);
		      httpURLConnection.setRequestProperty("folder", folder);

		      DataOutputStream dos = new DataOutputStream(
		          httpURLConnection.getOutputStream());
		      dos.writeBytes(twoHyphens + boundary + end);
		      dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\""
		          + "123.jpg" + "\"" + end);
		      dos.writeBytes(end);

		      ByteArrayOutputStream baos = new ByteArrayOutputStream();
		      bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		      dos.write(baos.toByteArray());
		      // fis.close();

		      dos.writeBytes(end);
		      dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
		      dos.flush();

		      InputStream is = httpURLConnection.getInputStream();
		      InputStreamReader isr = new InputStreamReader(is, "utf-8");
		      BufferedReader br = new BufferedReader(isr);

		      int dd = httpURLConnection.getResponseCode();

		      StringBuilder sb = new StringBuilder();

		      if (dd != 200)
		      {

		        sb.append("error");

		      } else
		      {
		        String line = null;
		        try
		        {
		          while ((line = br.readLine()) != null)
		          {
		            sb.append(line + "\n");
		          }
		        } catch (IOException e)
		        {
		          e.printStackTrace();
		        } finally
		        {
		          try
		          {
		            is.close();
		          } catch (IOException e)
		          {
		            e.printStackTrace();
		          }
		        }
		      }

		      result = sb.toString();
		      dos.close();
		      is.close();

		    } catch (Exception e)
		    {
		      e.printStackTrace();
		    }
		return result;
	}
}
