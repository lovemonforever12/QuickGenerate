package com.ucg.util.mail;

import java.util.HashMap;

import org.apache.commons.mail.EmailAttachment;

import com.ucg.util.freemark.FreeMarkerUtil;

public class SendMailMain {
	
	public static void main(String[] args) throws Exception {
		MailerPlugin mailerPlugin = new MailerPlugin();
		mailerPlugin.start();
		//1.发送文本
		Mailer.sendHtml("测试", "<a href='www.dreampie.cn'>Dreampie[单单发送文本]</a>", "644397946@qq.com");
		
		//2.发送附件
		EmailAttachment emailAttachment = new EmailAttachment();
		emailAttachment.setPath("F:\\flow.sql");
		Mailer.sendHtml("测试", "<a href='www.dreampie.cn'>Dreampie</a>",emailAttachment, "644397946@qq.com");
	
		//3.发送模板
		HashMap<String, Object> map=new HashMap<String, Object>();
		map.put("user", "MR.White");
		String content = FreeMarkerUtil.getTemplateContent("mail/ftl/signup_email.ftl", map);
		Mailer.sendHtml("测试", "<a href='www.dreampie.cn'>"+content+"</a>", "644397946@qq.com");
		System.out.println(content);
		
		mailerPlugin.stop();
	}

}
