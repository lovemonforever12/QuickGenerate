package ${packageName};

import java.util.List;
import java.util.Map;
import net.sf.json.JSONObject;
import com.jfinal.plugin.activerecord.Model;
import java.util.Date;
import java.math.BigDecimal;
import com.ucg.base.util.TagUtil;

/**
* 项目名称：中盈小贷 
* 创建人：${createUser}
* 创建时间：${createTime}   
* 功能说明：自动生成实体
* @param  
* 修改人：${createUser}
* 修改时间：${createTime}     
* 修改备注：   
* @version v1.0
*/
@SuppressWarnings({ "unused", "serial" })
public class ${entityName}  extends Model<${entityName}>{
	public static final ${entityName} dao = new ${entityName}();
	//-------------------------自动生成实体get/set方法--------------------------//
	 <#list columns as po>
       public static final String ${po.column?upper_case}="${po.column}";  //${po.comment}
     </#list>
	
	<#list columns as po>
	public ${po.fieldType} get${po.fieldName?cap_first}(){ //${po.comment}
		return ${po.getMethod}("${po.column}");
	}

	public void set${po.fieldName?cap_first}(${po.fieldType} ${po.fieldName}){
		set("${po.column}", ${po.fieldName});
	}
	</#list>
}
