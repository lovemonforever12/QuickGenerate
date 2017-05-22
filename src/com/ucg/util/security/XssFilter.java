package com.ucg.util.security;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ucg.base.framework.core.util.ResourceUtil;

/***
 * 
 * XSS 过滤器
 * 李宗灿
 * */
public class XssFilter implements Filter  {
	private List<String> list = new ArrayList<String>();
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		//判断需要过滤的路径
		if(checkUlr(request)){
			XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper((HttpServletRequest) request);  
			chain.doFilter(xssRequest, response);  
		}else
			chain.doFilter(request, response);  
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		list.add("insuranceController.do?toInsurancePage");
		list.add("managementController.do?toManagementPage");
		list.add("managementController.do?saveOrUpdateManagementInfo");
		list.add("ownershipStructureController.do?toAddOwnershipStructurePage");
	}
	
	public boolean checkUlr(ServletRequest sr){
		HttpServletRequest request = (HttpServletRequest) sr;
		String requestPath = ResourceUtil.getRequestPath(request);// 用户访问的资源地址
		if (list.contains(requestPath)) {
			return true;
		}
		boolean flag =false;
		return flag;
	}

}
