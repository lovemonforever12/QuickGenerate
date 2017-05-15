1.在配置路由configRoute方法中添加一下的一句话
	me.add("/${entityName?lower_case}", ${entityName}Controller.class);//${chinese}控制器

2.在配置插件中configPlugin方法中添加以下的一句话
	arp.addMapping("${tableName}",${entityName}.class); //${chinese}表
