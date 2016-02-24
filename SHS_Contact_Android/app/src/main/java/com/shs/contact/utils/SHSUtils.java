package com.shs.contact.utils;

import android.annotation.SuppressLint;


import com.shs.contact.R;
import com.shs.contact.ui.app.SHSApplication;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class SHSUtils {

//	@SuppressLint("SimpleDateFormat")
//	public static SimpleDateFormat mqttSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
//	@SuppressLint("SimpleDateFormat")
//	public static SimpleDateFormat chattingSdf = new SimpleDateFormat("MM-dd HH:mm");
//	@SuppressLint("SimpleDateFormat")
//	public static SimpleDateFormat chatHistorySdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//
//	public static SimpleDateFormat YMD_HMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//	public static SimpleDateFormat YMD = new SimpleDateFormat("yyyy-MM-dd");
//
//	public static SimpleDateFormat YM = new SimpleDateFormat("yyyy-MM");
//
//	public static SimpleDateFormat MD = new SimpleDateFormat("MM-dd");

//	public static Date parseDateTime(String timeStr) {
//		try {
//			return mqttSdf.parse(timeStr);
//		} catch (ParseException e) {
//			return new Date();
//		}
//	}

//	public static String formatDateStr(String dateStr) {
//		try {
//			return chattingSdf.format(mqttSdf.parse(dateStr));
//		} catch (Exception e) {
//			return dateStr;
//		}
//	}
//
//	public static String formatDataChatHistoryList(String dateStr) {
//		try {
//			return chatHistorySdf.format(mqttSdf.parse(dateStr));
//		} catch (Exception e) {
//			try {
//				return chatHistorySdf.format(YMD_HMS.parse(dateStr));
//			} catch (Exception e1) {
//				// TODO Auto-generated catch block
//				return chatHistorySdf.format(new Date(Long.parseLong(dateStr) * 1000L));
//			}
//		}
//	}
//
//	public static String toDateString(Date date) {
//		return mqttSdf.format(date);
//	}

//	/**
//	 * url 加密 先Base64再url编码
//	 * 
//	 * @param url
//	 * @return
//	 */
//	public static String urlEncoding(String url) {
//		try {
//			return URLEncoder.encode(Base64.encodeToString(url.getBytes(), Base64.DEFAULT), "UTF-8");
//		} catch (Exception e) {
//			e.printStackTrace();
//			return url;
//		}
//	}
	/*
	 * urlEncode 编码 
	 */
	public static String toURLEncoded(String paramString) {  
        if (paramString == null || paramString.equals("")) {  
            return "";  
        }  
        try  
        {  
            String str = new String(paramString.getBytes(), "UTF-8");  
            str = URLEncoder.encode(str, "UTF-8");  
            return str;  
        }  
        catch (Exception localException)  
        {  
//            Log.i("--","toURLEncoded error:"+paramString, localException);  
        }  
          
        return "";  
    }  
	
	/*
	 * urlDecode 解码
	 */
	public static String toURLDecoded(String paramString) {  
        if (paramString == null || paramString.equals("")) {  
            return "";  
        }  
          
        try  
        {  
            String str = new String(paramString.getBytes(), "UTF-8");  
            str = URLDecoder.decode(str, "UTF-8");  
            return str;  
        }  
        catch (Exception localException)  
        {  
//        	Log.i("--","toURLDecoded error:"+paramString, localException);  
        }  
          
        return "";  
    }  
	
//	// 使用系统当前日期加以调整作为照片的名称
//    @SuppressLint("SimpleDateFormat")
//    public static String getPhotoFileName() {
//    	//用户id+时间戳
//    	String fileName = UserManager.getInstance().getUser().getUid()+""+System.currentTimeMillis()/1000;
//        return fileName + ".jpg";
//    }
//
    //字符串转换数字
    public static int stringToInt(String string) {
    	
    	if (null == string || string.length() < 1) {
			return 0;
		}
    	
    	int intValue = 0;
    	for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			if ('0' <= c && c <= '9') {
				intValue *= 10;
				int tmpValue = c-'0';
				intValue += tmpValue;
			}else {
				break;
			}
		}
    	
    	return intValue;
    }
    

    public static boolean emptyRetunNone(String content) {
    	if (null != content && content.length() > 0) {
    		return true;
		}
		return false;
	}
    
    //字符串转换全角    
    public static String ToDBC(String input) {  
	   char[] c = input.toCharArray();  
	   for (int i = 0; i< c.length; i++) {  
	       if (c[i] == 12288) {  
	         c[i] = (char) 32;  
	         continue;  
	       }if (c[i]> 65280&& c[i]< 65375)  
	          c[i] = (char) (c[i] - 65248);  
	       }  
	   return new String(c);  
    } 
    
}
