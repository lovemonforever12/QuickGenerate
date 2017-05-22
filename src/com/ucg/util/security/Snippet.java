package com.ucg.util.security;

public class Snippet {
	
	
	  public static  String encodeHtml(String input) {
	           if (input == null) {
	                  return null;
	           }
	           StringBuffer out = new StringBuffer();
	           for (int i = 0; i < input.length(); i++) {
	                  char c = input.charAt(i);
	                  if (c == '<') {
	                         out.append("&lt;");
	                  } else if (c == '>') {
	                         out.append("&gt;");
	                  } else if (c == '\"') {
	                         out.append("&quot;");
	                  } else if (c == '&') {
	                         out.append("&amp;");
	                  } else if (c > 0x20 && c < 0x126) {
	                         out.append(c);
	                  } else {
	                         out.append("&#" + (int) c + ";");
	                  }
	           }
	           return out.toString();
	    }
	  
	  public static void main(String[] args) {
		String str="/picc/WebRoot/webpage/public/approvelTemplate.jsp";
		System.out.println(encodeHtml(str));
	}
}

