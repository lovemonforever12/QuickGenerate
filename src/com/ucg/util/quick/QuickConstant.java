package com.ucg.util.quick;

import java.util.HashMap;
import java.util.Map;

public class QuickConstant {
	public static String flag="//-------------------------自动生成实体get/set方法--------------------------//";
	public static final Map<String, String> templateType = new HashMap<String, String>(){
		private static final long serialVersionUID = 1L;
		{
			put("1","model.ftl");//model		
			put("2","Controller.ftl");//获取列表	
			put("3","interface.ftl");//获取接口
			put("4","Appconfig.ftl");//获取列表
			
		}
	};
	
	public static final Map<String, String> controllerMothod = new HashMap<String, String>(){
		private static final long serialVersionUID = 1L;
		{
			put("1","multipList");//多条件查询		
			put("2","controller.ftl");//获取列表			
		}
	};
}
