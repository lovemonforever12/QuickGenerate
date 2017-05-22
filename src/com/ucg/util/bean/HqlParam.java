package com.ucg.util.bean;

 /**    
* 项目名称：UCG_OSS     
* 创建人：王郎郎
* 创建时间：2016-3-23 下午05:04:04   
* 功能说明：hql和查询条件VO
* 修改人： 王郎郎
* 修改时间：2016-3-23 下午05:04:04    
* 修改备注：   
* @version       
*/
public class HqlParam {
	
	private String hql;			//组装后的hql
	private Object[] param;		//组装后的查询条件
	private String cql;			//统计的sql
	private String field;		//查询的字段
	
	private BeanField beanField;	//字段
	
	public String getHql() {
		return hql;
	}
	public void setHql(String hql) {
		this.hql = hql;
	}
	public Object[] getParam() {
		return param;
	}
	public void setParam(Object[] param) {
		this.param = param;
	}
	public String getCql() {
		return cql;
	}
	public void setCql(String cql) {
		this.cql = cql;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public BeanField getBeanField() {
		return beanField;
	}
	public void setBeanField(BeanField beanField) {
		this.beanField = beanField;
	}
	
}
