package com.suitong.mp.common.util;

import java.io.*;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 字符串处理基础类
 * 
 * @author Peter - E-mail:liumingjun@tiptech.com.cn
 * @version Jul 8, 2010 4:15:01 PM
 */
public class StringUtil {
	public static final String OAIP = "http://10.1.1.24";

	private static final Log log = LogFactory.getLog(StringUtil.class);

	public static final String NULLSTRING = "NULL";

	public StringUtil() {
	}

	/**
	 * 根据给定的加密方式加密密码
	 * 
	 * @param password
	 *            密码
	 * @param algorithm
	 *            加密方式,如MD5,SHA-1
	 * @return 密文
	 */
	public static String encodePassword(String password, String algorithm) {
		byte unencodedPassword[] = password.getBytes();
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance(algorithm);
		} catch (Exception e) {
			log.error((new StringBuilder("Exception: ")).append(e).toString());
			return password;
		}
		md.reset();
		md.update(unencodedPassword);
		byte encodedPassword[] = md.digest();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < encodedPassword.length; i++) {
			if ((encodedPassword[i] & 0xff) < 16)
				buf.append("0");
			buf.append(Long.toString(encodedPassword[i] & 0xff, 16));
		}
		return buf.toString();
	}

	/**
	 * 判断字符是否为空
	 * 
	 * @param str
	 * @return 是否为空
	 */
	public static Boolean isNotEmpty(String str) {
		if (str != null && str.trim().length() > 0
				&& !"null".equalsIgnoreCase(str)
				&& !"undefined".equalsIgnoreCase(str)) {
			return true;
		}
		return false;
	}

	/**
	 * 获取随机字符串 时间（年月日时分秒毫秒） + 随机数
	 * 
	 * @return
	 */
	public static String getRandomString() {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddkkmmss");
		String sdate = format.format(date);
		Random random = new Random();
		int number = random.nextInt(999999999);
		return sdate + number;
	}

	/**
	 * 将null返回空串
	 * 
	 * @param str
	 * @return 是否为空
	 */
	public static String null2Empty(String str) {
		if (!isNotEmpty(str)) {
			return "";
		}
		return str;
	}

	/**
	 * 统计在指定字符中出现的次数
	 * 
	 * @param content
	 * @param sub
	 * @return
	 */
	private static int countSub(String content, String sub) {
		int p = content.indexOf(sub);
		int count = 0;
		for (; p > -1; p = content.indexOf(sub)) {
			count++;
			content = content.substring(p + sub.length());
		}
		return count;
	}

	/**
	 * 身份证升位
	 * 
	 * @param id15
	 *            15位身份证号
	 * @return 18位身份证号
	 */
	private static String upgradeIDCard(String id15) {
		if (id15 == null)
			throw new IllegalArgumentException(
					"\u8EAB\u4EFD\u8BC1\u53F7\u7801\u975E\u6CD5!");
		int length = id15.length();
		if (length != 15 && length != 18)
			throw new IllegalArgumentException((new StringBuilder(
					"\u8EAB\u4EFD\u8BC1\u53F7\u7801[")).append(id15).append(
					"]\u975E\u6CD5!").toString());
		String id18 = "";
		if (length == 18) {
			id18 = id15.toUpperCase();
		} else {
			int w[] = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1 };
			char A[] = { '1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2' };
			String id17 = (new StringBuilder(String.valueOf(id15
					.substring(0, 6)))).append("19").append(
					id15.substring(6, 15)).toString();
			int id17Array[] = new int[17];
			for (int i = 0; i < 17; i++)
				id17Array[i] = Integer.parseInt(id17.substring(i, i + 1));

			int s = 0;
			for (int i = 0; i < 17; i++)
				s += id17Array[i] * w[i];

			s %= 11;
			id18 = (new StringBuilder(String.valueOf(id17))).append(A[s])
					.toString();
		}
		return id18;
	}

	/**
	 * 将字符串转成数组
	 * 
	 * @param str
	 *            字符串
	 * @param perSize
	 *            分组数
	 * @return 数组
	 */
	private static String[] splitStr(String str, int perSize) {
		if (str == null || str.trim().length() == 0)
			throw new IllegalArgumentException("");
		if (perSize < 1)
			throw new IllegalArgumentException();
		int byteLen = str.getBytes().length;
		if (byteLen <= perSize)
			return (new String[] { str });
		String ret[] = (String[]) null;
		int count = byteLen / perSize;
		if (byteLen % perSize != 0)
			count++;
		ret = new String[count];
		int tempLen = 0;
		String tmpStr = str;
		for (int i = 0; i < count; i++) {
			ret[i] = _splitStr(tmpStr, perSize);
			tempLen += ret[i].length();
			if (tempLen < str.length())
				tmpStr = str.substring(tempLen);
		}

		return ret;
	}

	/**
	 * 将字符串截取指定长度
	 * 
	 * @param str
	 * @param subBytes
	 * @return
	 */
	private static String _splitStr(String str, int subBytes) {
		int i = 0;
		int bytes = 0;
		for (; i < str.length(); i++) {
			if (bytes == subBytes)
				return str.substring(0, i);
			char c = str.charAt(i);
			if (c < '\u0100')
				bytes++;
			else if ((bytes += 2) - subBytes == 1)
				return str.substring(0, i);
		}
		return str;
	}

	/**
	 * 中文编码转UTF-8
	 * 
	 * @param s
	 * @return UTF-8字符
	 */
	private static String chineseToUTF8(String s) {
		String unicode = "";
		char charAry[] = new char[s.length()];
		for (int i = 0; i < charAry.length; i++) {
			charAry[i] = s.charAt(i);
			unicode = (new StringBuilder(String.valueOf(unicode)))
					.append("\\u").append(Integer.toString(charAry[i], 16))
					.toString();
		}

		return unicode;
	}


	/**
	 * 解决中文乱码
	 */
	public static String iso(String temp) {
		if (temp == null)
			return temp;
		try {
			temp = new String(temp.trim().getBytes("ISO8859-1"), ("utf-8"));
		} catch (Exception e) {
			log.error("", e);
		}

		return temp;
	}

	/**
	 * 处理SAP中的WE8DEC编码,转为GBK,再保存就不会乱码了
	 * 
	 * @param WE8DEC
	 *            数据库中的编码字符
	 * @return gbk
	 */
	public static String convert2GBK(String WE8DEC) {
		if (WE8DEC == null)
			return "";
		try {
			WE8DEC = new String(WE8DEC.trim().getBytes("ISO8859-1"), ("GBK"));
		} catch (Exception e) {
			log.error("", e);
		}
		return WE8DEC;
	}

	/**
	 * 将list转换成sql的参数
	 * 
	 * @param list
	 * @return String
	 */
	public static String list2String(List<?> list) {
		if (list != null && list.size() > 0) {
			String retStr = list.toString().replace("[", "").replace("]", "");
			return convertSql(retStr);
		} else {
			return null;
		}
	}

	/**
	 * 将删除的传过来的参数转换为sql格式
	 * 
	 * @param str
	 *            "1,2,3"
	 * @return String
	 */
	public static String convertSql(String str) {
		if (!StringUtil.isNotEmpty(str)) {
			return "'-1'";
		}
		String retStr = "";
		String[] ids = str.split(",");
		if (ids != null && ids.length > 0) {
			for (int i = 0; i < ids.length; i++) {
				retStr += "'" + ids[i].trim() + "',";
			}
		}
		retStr = retStr.substring(0, retStr.length() - 1);
		return retStr;
	}

	/**
	 * 用于字符串html处理后保存到数据库
	 * 
	 * @param theString
	 * @return
	 */
	public static String htmlEncode(String str) {
		return str.replaceAll("\n", "<br/> ");
	}

	/**
	 * 用于字符串html解码
	 * 
	 * @param str
	 * @return
	 */
	public static String htmlDecode(String str) {
		return str.replaceAll("<br/> ", "\n");
	}

	/**
	 * 便于把对象转换为字符串
	 * @param
	 * @return
	 * */
	public static String toString(Object param){
		if(param == null){
			return "";
		}
		return param.toString();
	}
	
	public static void main(String args[]) {
		// List list = new ArrayList();
		// list.add("2");
		// List list1 = new ArrayList();
		// list1.add("3");
		// list.addAll(list1);
		// log.info(list2String(list));
		// String str = "1,2,3";
		// log.info(convertSql(str));
		// log.info(StringUtils.isNotEmpty(" "));
		String a = "${12}3";
		System.out.println(a.replace("${12}", "44"));
	}

	 /**
     * 将多个字符串拼在一起
     *
     * @param strs
     * @return
     */
    public static String merge(Object...strs){
    	StringBuffer buffer = new StringBuffer();
    	for(Object str : strs){
    		buffer.append(str);
    	}
    	return buffer.toString();
    }
}
