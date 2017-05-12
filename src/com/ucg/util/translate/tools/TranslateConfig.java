package com.ucg.util.translate.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import com.ucg.util.translate.error.TranslateException;

public class TranslateConfig
{
  private static TranslateConfig config = new TranslateConfig();
  private Properties properties = new Properties();
  
  private TranslateConfig()
  {
    try
    {
      InputStream is = getClass().getResourceAsStream(
        "/translate.properties");
      this.properties.load(new InputStreamReader(is, "UTF-8"));
      is.close();
    }
    catch (IOException e)
    {
      new TranslateException("load src/translate.properties fail!", e);
    }
  }
  
  public String getValue(String name, String defaultVal)
  {
    return this.properties.getProperty(name, defaultVal);
  }
  
  public String getValue(String name)
  {
    return getValue(name, "");
  }
  
  public static TranslateConfig getInstance()
  {
    return config;
  }
}
