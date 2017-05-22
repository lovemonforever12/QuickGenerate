package com.ucg.util.treeUtil;

import java.util.List;

/**
 * description : EasyUI所使用的树节点类
 *
 * @version 1.0
 * @author xiaqiang
 * @createtime : 2014年5月12日 下午9:05:32
 *
 */

public class MyTreeNode implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private String id; // 节点ID
	private String text; // 节点名
	private String type;// 0分类  1产品
	
	private List<MyTreeNode> children;// 子节点

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	


	public List<MyTreeNode> getChildren() {
		return children;
	}

	public void setChildren(List<MyTreeNode> children) {
		this.children = children;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	

}
