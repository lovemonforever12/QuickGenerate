package com.ucg.util.translate.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.MessageFormat;

import net.sf.json.JSONObject;

import com.ucg.util.translate.bean.TranslateResult;
import com.ucg.util.translate.error.TranslateException;

public class TranslateTools
{
  private static final String T_URL = "http://fanyi.youdao.com/openapi.do?keyfrom={0}&key={1}&type=data&doctype=json&only=translate&version=1.1&q={2}";
  private static final String ENCODING = "UTF-8";
  private static TranslateConfig config =TranslateConfig.getInstance() ;
  
  public static TranslateResult translateText(String text)
    throws TranslateException
  {
    InputStream is = null;
    TranslateResult result = null;
    try
    {
      if ((text == null) || (text.trim().length() == 0)) {
        return result;
      }
      if (text.length() > 200) {
        throw new TranslateException(20);
      }
      String keyfrom = config.getValue("keyfrom");
      String key = config.getValue("key");
      URL url = new URL(MessageFormat.format("http://fanyi.youdao.com/openapi.do?keyfrom={0}&key={1}&type=data&doctype=json&only=translate&version=1.1&q={2}", new Object[] { keyfrom, key, 
        URLEncoder.encode(text, "UTF-8") }));
      HttpURLConnection conn = (HttpURLConnection)url.openConnection();
      conn.setRequestProperty("Accept-Charset", "UTF-8");
      conn.setDoInput(true);
      conn.setDoOutput(true);
      conn.setConnectTimeout(5000);
      conn.setUseCaches(true);
      conn.setRequestMethod("GET");
      conn.connect();
      is = conn.getInputStream();
      InputStreamReader reader = new InputStreamReader(is, "UTF-8");
      BufferedReader bufferedReader = new BufferedReader(reader);
      StringBuffer buff = new StringBuffer();
      String str = null;
      while ((str = bufferedReader.readLine()) != null) {
        buff.append(str);
      }
      JSONObject object = JSONObject.fromObject(buff.toString());
      result = (TranslateResult)JSONObject.toBean(object, TranslateResult.class);
      if (result.getErrorCode().intValue() != 0) {
        throw new TranslateException(result.getErrorCode().intValue());
      }
    }
    catch (Exception e)
    {
      throw new TranslateException(e);
    }
    finally
    {
      try
      {
        if (is != null) {
          is.close();
        }
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
    try
    {
      if (is != null) {
        is.close();
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    return result;
  }
}
