package com.ucg.util.translate.error;

public enum MsgType
{
  TEXT_LIMT_LEN(20, "translated text is too long[maxlength,200]!"),  TRANS_FAIL(30, 
    "Cannot be translated effectively!"),  NOT_SUPPORT_LANGUE(40, 
    "Does not support the language types!"),  INVALID_KEY(50, 
    "invalid key!"),  NOT_RESULT(60, "There is no dictionary results!");
  
  private int code;
  private String msg;
  
  private MsgType(int code, String msg)
  {
    this.code = code;
    this.msg = msg;
  }
  
  public static String getMsg(int code)
  {
    for (MsgType type :MsgType.values()) {
      if (type.getCode() == code) {
        return type.getMsg();
      }
    }
    return "errcode " + code + " undefined!";
  }
  
  public int getCode()
  {
    return this.code;
  }
  
  public String getMsg()
  {
    return this.msg;
  }
}
