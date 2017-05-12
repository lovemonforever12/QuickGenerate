package com.ucg.util.translate.bean;

import java.util.Arrays;

public class TranslateResult
{
  private Integer errorCode;
  private String query;
  private String[] translation;
  
  public Integer getErrorCode()
  {
    return this.errorCode;
  }
  
  public void setErrorCode(Integer errorCode)
  {
    this.errorCode = errorCode;
  }
  
  public String getQuery()
  {
    return this.query;
  }
  
  public void setQuery(String query)
  {
    this.query = query;
  }
  
  public String[] getTranslation()
  {
    return this.translation;
  }
  
  public void setTranslation(String[] translation)
  {
    this.translation = translation;
  }
  
  public String toString()
  {
    return 
    

      new StringBuilder("TranslateResult:{query:\"").append(this.query).append("\"").toString() + new StringBuilder(",errorCode:").append(this.errorCode).toString() + new StringBuilder(",translation:").append(Arrays.toString(this.translation)).append("}").toString();
  }
}
