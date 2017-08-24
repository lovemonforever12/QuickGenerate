package ${packageName}.web;

import java.util.Map;

import com.jfinal.core.Controller;
import com.jfinal.kit.AjaxJson;
import ${packageName}.model.${entityName};
import com.ucg.util.common.CommonUtil;
import com.ucg.util.common.StringUtil;
import com.ucg.util.common.DateUtils;
import java.util.HashMap;

/**
* 项目名称：中盈小贷 
* 创建人：${createUser}
* 创建时间：${createTime}   
* 功能说明：${chinese}控制器
* 修改人：${createUser}
* 修改时间：${createTime}     
* 修改备注：   
* @version v1.0
*/
public class ${entityName}Controller extends Controller {

	private static final ${entityName} service = ${entityName}.dao;
	
	/**     
	* 创建人：${createUser} 
	* 创建时间：${createTime}  
	* 功能说明：首页    
	*/
	public void index(){
		String url = "${entityName?lower_case}.jsp";
		String id = id();
		if(StringUtil.isNotEmpty(id)){
			${entityName} obj = service.findById(id);
			setAttr("obj", obj);
		}
		render(url);
	}
	
	
	/**     
	* 创建人：${createUser} 
	* 创建时间：${createTime}  
	* 功能说明：获取列表  
	* 访问地址：${acessUrl}${entityName?lower_case}/list?field=${field}&but=&sort=create_time&order=desc&page=1&rows=10
	*/
	public void list(){
		Map<String, String> para = para();
		String currentTime = DateUtils.getCurrentTime();
		CommonUtil.get().addPara(para,"sort", "create_time").addPara(para,"order", "asc").addPara(para,"linkInfo_or", "a,b").addPara(para, "create_time_le",currentTime );//修改查询的参数
		Map<String, String> pageInclude = paraInclude("");    para.putAll(pageInclude);//只显示的列名
		Map<String, String> paraExclude = paraExclude("");    para.putAll(paraExclude);//不显示的列名
		renderJson(service.pageResult(para));
	}
	
	/**     
	* 创建人：${createUser} 
	* 创建时间：${createTime}  
	* 功能说明：保存
	* 访问地址：${acessUrl}${entityName?lower_case}/saveOrUpdate
	*/
	public void saveOrUpdate() throws Exception{
		AjaxJson j = new AjaxJson();
		${entityName} ${entityName?lower_case} = getModel(${entityName}.class);
		try {
			${entityName?lower_case}.saveOrUpdate();
			j.setObj(${entityName?lower_case}.getId());
		} catch (Exception e) {
			throw new MyException(e);
		}
		renderJson(j);
	}
	
	 /**     
	* 创建人：${createUser} 
	* 创建时间：${createTime}  
	* 功能说明：根据ID获取
	* 访问地址：${acessUrl}${entityName?lower_case}/get?id=xxxx
	* @throws Exception 
	*/
	public void get() throws Exception{
		AjaxJson j = new AjaxJson();
		Map<String, Object> attributes=new HashMap<String, Object>();
		try {
			${entityName} ${entityName?lower_case}=service.findById(id());
			attributes.put("${entityName?lower_case}", ${entityName?lower_case});
			j.setAttributes(attributes);
		} catch (Exception e) {
			throw new MyException(e);
		}
		renderJson(j);
	}
	
	
	 /**     
	* 创建人：${createUser} 
	* 创建时间：${createTime}  
	* 功能说明：删除
	* 访问地址：${acessUrl}${entityName?lower_case}/delete?ids=xxxx,xxxx
	*/
	public void delete() throws Exception{
		AjaxJson j = new AjaxJson();
		String ids = getPara("ids");
		try {
			if(StringUtil.isNotEmpty(ids)){
				String[] idArr = ids.split(",");
				for(String id:idArr){
					service.deleteById(id);
				}
			}
		} catch (Exception e) {
			throw new MyException(e);
		}
		renderJson(j);
	}

}
