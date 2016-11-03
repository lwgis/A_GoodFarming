package com.zhonghaodi.goodfarming;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;

import com.zhonghaodi.networking.GFDate;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class ErrorReport implements Thread.UncaughtExceptionHandler {

	private Thread.UncaughtExceptionHandler mDefaultHandler;    
    public static final String TAG = "CatchExcep";  
  //用来存储设备信息和异常信息      
    private Map<String, String> infos = new HashMap<String, String>(); 
    UILApplication application;  
      
    public ErrorReport(UILApplication application){  
         //获取系统默认的UncaughtException处理器    
         mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();  
         this.application = application;  
    }  
      
    @Override  
    public void uncaughtException(Thread thread, Throwable ex) {      
        if(!handleException(ex) && mDefaultHandler != null){   
            //如果用户没有处理则让系统默认的异常处理器来处理    
            mDefaultHandler.uncaughtException(thread, ex);                
        }else{         
            try{    
                Thread.sleep(2000);    
            }catch (InterruptedException e){    
                Log.e(TAG, "error : ", e);    
            }     
            Intent intent = new Intent(application.getApplicationContext(), MainActivity.class);  
            PendingIntent restartIntent = PendingIntent.getActivity(    
                    application.getApplicationContext(), 0, intent,    
                    Intent.FLAG_ACTIVITY_NEW_TASK);                                                 
            //退出程序                                          
            AlarmManager mgr = (AlarmManager)application.getSystemService(Context.ALARM_SERVICE);     
            application.finishActivity();  
        }    
    }  
      
    /**  
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.  
     *   
     * @param ex  
     * @return true:如果处理了该异常信息;否则返回false.  
     */    
    private boolean handleException(Throwable ex) {    
        if (ex == null) {    
            return false;    
        }    
        //使用Toast来显示异常信息    
        new Thread(){    
            @Override    
            public void run() {    
                Looper.prepare();    
                Toast.makeText(application.getApplicationContext(), "很抱歉,程序出现异常,即将退出.",   
                        Toast.LENGTH_SHORT).show();    
                Looper.loop();    
            }   
        }.start();  
        saveCatchInfo2File(ex);
        return true;    
    }
    
    /**   
     * 保存错误信息到文件中   
     *    
     * @param ex   
     * @return  返回文件名称,便于将文件传送到服务器   
     */      
    private String saveCatchInfo2File(Throwable ex) {      
              
        StringBuffer sb = new StringBuffer();      
        for (Map.Entry<String, String> entry : infos.entrySet()) {      
            String key = entry.getKey();      
            String value = entry.getValue();      
            sb.append(key + "=" + value + "\n");      
        }      
              
        Writer writer = new StringWriter();      
        PrintWriter printWriter = new PrintWriter(writer);      
        ex.printStackTrace(printWriter);      
        Throwable cause = ex.getCause();      
        while (cause != null) {      
            cause.printStackTrace(printWriter);      
            cause = cause.getCause();      
        }      
        printWriter.close();      
        String result = writer.toString();      
        sb.append(result);      
        try {      
            long timestamp = System.currentTimeMillis();      
            String time = GFDate.toString(new Date());     
            String fileName = "crash-" + time + "-" + timestamp + ".log";      
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {      
                String path = "/mnt/sdcard/crash/";      
                File dir = new File(path);      
                if (!dir.exists()) {      
                    dir.mkdirs();      
                }      
                FileOutputStream fos = new FileOutputStream(path + fileName);      
                fos.write(sb.toString().getBytes());      
                fos.close();      
            }      
            return fileName;      
        } catch (Exception e) {      
            Log.e(TAG, "an error occured while writing file...", e);      
        }      
        return null;      
    } 
}
