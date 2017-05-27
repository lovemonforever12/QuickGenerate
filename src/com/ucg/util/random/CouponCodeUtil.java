package com.ucg.util.random;

import java.util.Random;

public class CouponCodeUtil {
    //生成随机数字和字母,  
    public static String getStringRandom(int length) {  
        String val = "";
        Random random = new Random();  
        //参数length，表示生成几位随机数  
        for(int i = 0; i < length; i++) {  
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";  
            //输出字母还是数字  
            if( "char".equalsIgnoreCase(charOrNum) ) {  
                //输出是大写字母还是小写字母  
               // int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;  
            	char ranStr=(char)(random.nextInt(26) + 65);
            	for(;;){//去掉O
            		if(ranStr!='O'){
            			break;
            		}else{
            			ranStr=(char)(random.nextInt(26) + 65);
            		}
            	}
                val += ranStr;  
            } else if( "num".equalsIgnoreCase(charOrNum) ) { 
            	int ranInt=random.nextInt(10);
            	for(;;){//去掉0
            		if(ranInt!=0){
            			break;
            		}else{
            			ranInt=random.nextInt(10);
            		}
            	}
                val += String.valueOf(ranInt);  
            }
        }  
        return val;
    }  
      
    public static void  main(String[] args) {  
        System.out.println(CouponCodeUtil.getStringRandom(8));
    }  
}  
