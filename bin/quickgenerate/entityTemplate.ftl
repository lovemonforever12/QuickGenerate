package ${packageName}.entity;


import java.util.List;
import java.util.Map;
import net.sf.json.JSONObject;
import com.jfinal.plugin.activerecord.Model;
import java.util.Date;
import java.math.BigDecimal;
import com.ucg.base.util.TagUtil;
import java.sql.Timestamp;
import java.math.BigInteger;
/**
* 项目名称：中盈小贷 
* 创建人：${createUser}
* 创建时间：${createTime}   
* 功能说明：${chinese}实体[${tableName}]
* 修改人：${createUser}
* 修改时间：${createTime}     
* 修改备注：   
* @version v1.0
*/
public class ${entityName}Vo{

	<#list superColumns as spo>
	private ${spo.fieldType} ${spo.fieldName};  //${spo.comment}
	</#list>

	<#list columns as po>
	private ${po.fieldType} ${po.fieldName};  //${po.comment}
	</#list>
	
	
	<#list superColumns as spo>
	public ${spo.fieldType} get${spo.fieldName?cap_first}(){
		return this.${spo.fieldName};
	}

	public void set${spo.fieldName?cap_first}(${spo.fieldType} ${spo.fieldName}){
		this.${spo.fieldName} = ${spo.fieldName};
	}
	</#list>
	
	
	<#list columns as po>
	public ${po.fieldType} get${po.fieldName?cap_first}(){
		return this.${po.fieldName};
	}

	public void set${po.fieldName?cap_first}(${po.fieldType} ${po.fieldName}){
		this.${po.fieldName} = ${po.fieldName};
	}
	</#list>
	
}
