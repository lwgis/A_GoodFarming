package com.zhonghaodi.utils;

public class PublicHelper {

	public static String TrimRight(String sString){
        String sResult = "";
         
        if (sString.startsWith(" ")){
            sResult = sString.substring(0,sString.indexOf(sString.trim().substring(0, 1))
                    +sString.trim().length());
        }
        else    sResult = sString.trim();
         
        return sResult;
    }
	
}
