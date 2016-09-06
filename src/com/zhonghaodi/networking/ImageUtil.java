package com.zhonghaodi.networking;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.easemob.util.EMLog;
import com.easemob.util.PathUtil;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

public class ImageUtil {
	static final String imageUrl=HttpUtil.RootURL+"files/addimage";
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
		      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
              Log.d("size", String.valueOf(baos.size()/1024));
		      InputStream is1 = new ByteArrayInputStream(baos.toByteArray());
		      byte[] buffer = new byte[1024];
		      int length = -1;  		      
		      while ((length = is1.read(buffer)) != -1) {
		    	  
		    	  dos.write(buffer);
		    	  
		    	}
		      
		      is1.close();
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

		      result = sb.toString().trim();
		      Log.d("ImageUtil", result);
		      dos.close();
		      is.close();

		    } catch (Exception e)
		    {
		      e.printStackTrace();
		    }
		return result;
	}
	
	public static String uploadHead(Bitmap bitmap,String folder)throws Throwable {
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
		      double maxSize =100.00; 
		      ByteArrayOutputStream baos = new ByteArrayOutputStream();
		      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		      byte[] b = baos.toByteArray();
		      //将字节换成KB 
              double mid = b.length/1024; 
              Log.d("size", String.valueOf(mid));
              //判断bitmap占用空间是否大于允许最大空间  如果大于则压缩 小于则不压缩 
              if (mid > maxSize) { 
                      //获取bitmap大小 是允许最大大小的多少倍 
                      double i = mid / maxSize; 
                      //开始压缩  此处用到平方根 将宽带和高度压缩掉对应的平方根倍 （1.保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小） 
                      bitmap = zoomImage(bitmap, bitmap.getWidth() / Math.sqrt(i), 
                    		  bitmap.getHeight() / Math.sqrt(i)); 
              }
              baos.reset();
              bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
              Log.d("size", String.valueOf(baos.size()/1024));
		      InputStream is1 = new ByteArrayInputStream(baos.toByteArray());
		      byte[] buffer = new byte[1024];
		      int length = -1;  
		      
		      while ((length = is1.read(buffer)) != -1) {
		    	  
		    	  dos.write(buffer);
		    	  
		    	}
		      
//		      dos.write(baos.toByteArray());
		      is1.close();
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

		      result = sb.toString().trim();
		      Log.d("ImageUtil", result);
		      dos.close();
		      is.close();

		    } catch (Exception e)
		    {
		      e.printStackTrace();
		    }
		return result;
	}
	
	
	public static String getImagePath(String remoteUrl)
	{
		String imageName= remoteUrl.substring(remoteUrl.lastIndexOf("/") + 1, remoteUrl.length());
		String path =PathUtil.getInstance().getImagePath()+"/"+ imageName;
        EMLog.d("msg", "image path:" + path);
        return path;
		
	}
	
	
	public static String getThumbnailImagePath(String thumbRemoteUrl) {
		String thumbImageName= thumbRemoteUrl.substring(thumbRemoteUrl.lastIndexOf("/") + 1, thumbRemoteUrl.length());
		String path =PathUtil.getInstance().getImagePath()+"/"+ "th"+thumbImageName;
        EMLog.d("msg", "thum image path:" + path);
        return path;
    }
	
	/***
     * 图片的缩放方法
     *
     * @param bgimage
     *            ：源图片资源
     * @param newWidth
     *            ：缩放后宽度
     * @param newHeight
     *            ：缩放后高度
     * @return
     */ 
    public static Bitmap zoomImage(Bitmap bgimage, double newWidth, 
                    double newHeight) { 
            // 获取这个图片的宽和高 
            float width = bgimage.getWidth(); 
            float height = bgimage.getHeight(); 
            // 创建操作图片用的matrix对象 
            Matrix matrix = new Matrix(); 
            // 计算宽高缩放率 
            float scaleWidth = ((float) newWidth) / width; 
            float scaleHeight = ((float) newHeight) / height; 
            // 缩放图片动作 
            matrix.postScale(scaleWidth, scaleHeight); 
            Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width, 
                            (int) height, matrix, true); 
            return bitmap; 
    } 
	
}
