package com.appublisher.quizbank.model.richtext;

import android.content.Context;

import java.util.regex.Pattern;

/**
 * 富文本正则表达式解析器构造类。
 * @author Whiz
 *
 */
public class Patterns {
	private static Patterns sInstance;
	static Patterns getInstance() { return sInstance; }
	public static void init(Context context) {
		sInstance = new Patterns(context);
	}

	private Patterns(Context context) {
		mWeburlPattern = Pattern.compile(WEBURL_REG);
		mImagePattern = Pattern.compile(IMAGE_REG);
		mHyperlinkPattern = Pattern.compile(HYPERLINK_REG);
	}
	
	public final Pattern mWeburlPattern;
	public final Pattern mImagePattern;
	public final Pattern mHyperlinkPattern;

	private static final String WEBURL_REG = getUrlReg();
	private static final String IMAGE_REG = getImageReg();
	private static final String HYPERLINK_REG = getHyperlinkReg();

	// 构造 web url 正则式。
	private static String getUrlReg() {
		StringBuilder builder = new StringBuilder();
		
		return
		builder.append("((https|http|ftp|rtsp|mms)?://)")
			   .append("?(([0-9a-zA-Z_!~*'().&=+$%-]+: )?[0-9a-zA-Z_!~*'().&=+$%-]+@)?")
			   .append("(([0-9]{1,3}\\.){3}[0-9]{1,3}")
			   .append("|")
			   .append("([0-9a-zA-Z_!~*'()-]+\\.)*")
			   .append("([0-9a-zA-Z][0-9a-zA-Z-]{0,61})?[0-9a-zA-Z]\\.")
			   .append("[a-zA-Z]{2,6})")
			   .append("(:[0-9]{1,4})?")
			   .append("(((/[0-9a-zA-Z_!~*'().;?:@&=+$,%#-]+)+/?)|")
			   .append("(/?))")
			   .toString();
	}
	
	// 构造图片正则式
	private static String getImageReg() {
		StringBuilder builder = new StringBuilder();

		return
		builder.append("<img=")
			   .append(WEBURL_REG)
			   .append("></img>")
			   .toString();
	}
	
	// 投资超链接正则式。
	private static String getHyperlinkReg() {
		StringBuilder builder = new StringBuilder();
		
		return
		builder.append("<url=")
			   .append(WEBURL_REG)
			   .append(">(.*?)</url>")
			   .toString();
	}
}
