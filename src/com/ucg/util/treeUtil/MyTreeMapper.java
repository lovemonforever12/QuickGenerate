package com.ucg.util.treeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ucg.base.framework.core.util.ReflectHelper;

/**
 * description :构造树形节点
 *
 * @version 1.0
 * @author xiehs
 * @createtime : 2014年5月13日 上午9:11:22
 * 
 */
public class MyTreeMapper {
	/** 属性类型. */
	public static enum PropertyType {
		ID("id"),

		TEXT("text"),

		TYPE("type"),

		CHILDREN("children");

		
		/**
		
		使用方法:
		List<ProductManager> list = this.generalDao.findByProperty(ProductManager.class, "parentId", "-1");
	        Map map = new HashMap();
	        map.put(MyTreeMapper.PropertyType.ID.getValue(), "productCode");
	        map.put(MyTreeMapper.PropertyType.TEXT.getValue(), "productName");
	        map.put(MyTreeMapper.PropertyType.TYPE.getValue(), "typeCode");
	        map.put(MyTreeMapper.PropertyType.CHILDREN.getValue(), "list");
	        List treeList = MyTreeMapper.buildJsonTree(list, map);
	        TagUtil.tree(response, treeList);
			return treeList;
		
		**/

		private String property;

		private PropertyType(String property) {
			this.property = property;
		}

		public String getValue() {
			return property;
		}

	}

	/**
	 * 
	 * @author xiehs
	 * @createtime 2014年5月13日 上午9:48:03
	 * @Decription 构造普通树
	 * @param srcs
	 * @param propertyMapping
	 * @param parentId
	 * @return
	 */
	public static <T> List<MyTreeNode> buildJsonTree(
			List<T> srcs, final Map<String, String> propertyMapping) {
		return buildJsonTree(srcs, propertyMapping, null);
	}

	/**
	 * @author xiehs
	 * @createtime 2014年5月13日 上午9:48:10
	 * @Decription 构造带复选框的树
	 * @param srcs
	 * @param propertyMapping
	 * @param parentId
	 * @param checkedIds
	 * @return
	 */
	public static <T> List<MyTreeNode> buildJsonTree(
			List<T> srcs, final Map<String, String> propertyMapping,
			final List<String> checkedIds) {

		if (srcs == null || srcs.size() == 0 || propertyMapping == null
				|| propertyMapping.size() == 0) {
			return null;
		}
		List<MyTreeNode> trees = new ArrayList<MyTreeNode>();
		MyTreeNode treeNode = null;
		for (T src : srcs) {
			try {
				treeNode = new MyTreeNode();
				// 遍历转换类中的属性
				for (String treeProperty : propertyMapping.keySet()) {
					// 获得对象中属性名
					String srcProperty = propertyMapping.get(treeProperty);

					Object srcValue = null;

					
						// 获得对象中属性值
						srcValue = ReflectHelper
								.getFieldValue(src, srcProperty);
						// 构造叶子其他固有属性
						if (srcValue != null) {
							if (PropertyType.CHILDREN.getValue().equals(// 构造孩子节点
									treeProperty)) {
								List<T> entityList = (List<T>) srcValue;
								if (entityList != null && !entityList.isEmpty()) {
									List<MyTreeNode> childTrees = new ArrayList<MyTreeNode>();
									String pid = (String) ReflectHelper
											.getFieldValue(src,
													PropertyType.ID.getValue());
									childTrees = buildJsonTree(entityList,propertyMapping, checkedIds);
									treeNode.setChildren(childTrees);
								}
							} else {
								ReflectHelper.setFieldValue(treeNode,
										treeProperty, srcValue);
							}
						}

				}
				// 构造选中树节点
				if (checkedIds != null && checkedIds.size() > 0) {
					for (String checkedId : checkedIds) {
						if (treeNode.getId().equals(checkedId)) {
							
						}
					}
					trees.add(treeNode);
				}
				if (!trees.contains(treeNode)) {
					trees.add(treeNode);
				}
			} catch (Exception e) {
				throw ReflectHelper.convertReflectionExceptionToUnchecked(e);
			}
		}
		return trees;
	}
}
