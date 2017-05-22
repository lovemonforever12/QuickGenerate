package ${packageName}.entity;


import java.util.Date;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


public class ${entityName}{

	<#list columns as po>
	private ${po.fieldType} ${po.fieldName};  //${po.comment}
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
