package com.ucg.util.bean;

 /**    
* 项目名称：UCG_OSS     
* 创建人：王郎郎
* 创建时间：2016-3-24 上午11:50:04   
* 功能说明：获取类的字段
* 修改人： 王郎郎
* 修改时间：2016-3-24 上午11:50:04    
* 修改备注：   
* @version       
*/
public class BeanField {

	private String allfield;	//所有字段
	private String fields;		//当前字段
	private String pfields;		//父字段
	private Class<?> pc;		//父类
	private String ppfields;	//父父字段
	private Class<?> ppc;		//父父类
	private String pppfields;	//父父父字段
	private Class<?> pppc;		//父父父类
	
	public String getAllfield() {
		return allfield;
	}
	public void setAllfield(String allfield) {
		this.allfield = allfield;
	}
	public String getFields() {
		return fields;
	}
	public void setFields(String fields) {
		this.fields = fields;
	}
	public String getPfields() {
		return pfields;
	}
	public void setPfields(String pfields) {
		this.pfields = pfields;
	}
	public Class<?> getPc() {
		return pc;
	}
	public void setPc(Class<?> pc) {
		this.pc = pc;
	}
	public String getPpfields() {
		return ppfields;
	}
	public void setPpfields(String ppfields) {
		this.ppfields = ppfields;
	}
	public Class<?> getPpc() {
		return ppc;
	}
	public void setPpc(Class<?> ppc) {
		this.ppc = ppc;
	}
	public String getPppfields() {
		return pppfields;
	}
	public void setPppfields(String pppfields) {
		this.pppfields = pppfields;
	}
	public Class<?> getPppc() {
		return pppc;
	}
	public void setPppc(Class<?> pppc) {
		this.pppc = pppc;
	}
	
}
