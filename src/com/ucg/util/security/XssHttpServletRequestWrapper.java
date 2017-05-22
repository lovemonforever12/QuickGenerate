package com.ucg.util.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class XssHttpServletRequestWrapper  extends HttpServletRequestWrapper {
	HttpServletRequest orgRequest = null; 
	public XssHttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
		orgRequest=request;
	}
	
	@Override  
	public String getParameter(String name) {  
		String value = super.getParameter(xssEncode(name));  
		if (value != null) {  
		value = xssEncode(value);  
		}  
		return value;  
	}  
	
	@Override  
	public String getHeader(String name) {  
		String value = super.getHeader(xssEncode(name));  
		if (value != null) {  
			value = xssEncode(value);  
		}  
		return value;  
	}  
	
	private static String xssEncode(String s) {  
		 return Snippet.encodeHtml(s);
		} 
	
	public HttpServletRequest getOrgRequest() {  
		return orgRequest;  
		}  
	
	public static HttpServletRequest getOrgRequest(HttpServletRequest req) {  
		if(req instanceof XssHttpServletRequestWrapper){  
		return ((XssHttpServletRequestWrapper)req).getOrgRequest();  
		}  
		return req;  
	}  

	

}
