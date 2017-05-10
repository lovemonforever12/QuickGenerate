package com.ucg.util.quick;

import java.util.HashMap;
import java.util.Map;

public class Constant {
	public static String flag="//-------------------------自动生成实体get/set方法--------------------------//";
	public static final Map<String, String> templateType = new HashMap<String, String>(){
		private static final long serialVersionUID = 1L;
		{
			put("1","entityTemplate.ftl");//实体		
			put("2","getGridReturn.ftl");//获取列表			
			put("3","ListMethod.ftl");//列表			
			put("4","查询是否存在");	
		}
	};
}
