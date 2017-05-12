package com.ucg.util.translate.start;

import com.ucg.util.translate.bean.TranslateResult;
import com.ucg.util.translate.tools.TranslateTools;

public class StartTransLate {
	public static void main(String[] args) {
		TranslateResult translateText = TranslateTools.translateText("创建人");
		System.out.println(translateText);
	}
}
