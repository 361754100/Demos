package com.hro.core.common.util;


/**
 * Replace with @see com.suntek.util.StringUtil
 */
public class StringUtil {

	/**
	 * 说明:
	 * 		若 numToString( 123456789, 4 )
	 * 		将 125369 转化为 5369
	 * 
	 * 		若 numToString( 89, 4 )
	 * 		将 89 转化为 0089
	 * 
	 * 		获得最右边的4位,不足的用0来补.
	 * 
	 * @param num
	 * 		要进行转化的数
	 * @param len 
	 * 		要输出的数的位数
	 * @return
	 */
	public static String numToString(Number num, int len)
	{
		String rts = num.toString();
		
		if(rts.length()>len)
		{
			return rts.substring(rts.length()-len);
		}
		
		if(rts.length()<len)
		{
			int count = len-rts.length();
			for(int i=0;i<count;i++)
			{
				rts = "0" + rts;
			}
			return rts;
		}
		
		return rts;
	}
	
	
	/**
	 * 判断是否是空串，"   \r   \n  \r\n" 也算是空串
	 */
	public static boolean isEmpty(String input)
	{
		boolean isEmpty = true;		//默认是空串
		
		if(input!=null)
		{
			for(int i=0;i<input.length();i++)	//只要有一个字符不是 ' '，'\r','\n'，那它就是非空的
			{
				char c = input.charAt(i); 
				if( c!=' ' && c!='\r' && c!='\n' )
				{
					return false;			
				}
			}
		}
		
		return isEmpty;
	}
	
	/**
	 * 说明: 将
	 * 			"20080612"
	 * 		这样的字符串变为
	 * 			"2008-06-12"
	 * 		只是加 "-" 而已
	 */
	public static String strToDate(String input)
	{
		String rs = input.substring(0, 4)+"-"+input.substring(4, 6)+"-"+input.substring(6, 8);
		return rs; 
	}
	
	/**
	 * 说明: 将
	 * 			"210338"
	 * 		这样的字符串变为
	 * 			"21:03:38"
	 * 		只是加 ":" 而已
	 */
	public static String strToTime(String input)
	{
		String rs = input.substring(0, 2)+":"+input.substring(2, 4)+":"+input.substring(4, 6);
		return rs; 
	}
	
	/**
	 * 
	 * author : 陈鸿圳
	 * date : 2008-06-04
	 */
	public static String showLimitedString(String input, int len)
	{
		if(input.length() > len)
		{
			return input.substring(0, len-1)+"..";
		}
		else
		{
			return input;
		}
	}
	
	/**
	 * 说明:
	 * 		将ss里面的所有字符串连接起来
	 * 		连接符就是s
	 * 
	 * @param ss
	 * @param s
	 * @return
	 */
	public static String join(String[] ss, String s)
	{
		StringBuffer sb = new StringBuffer();
		
		if(ss.length>0)
		{
			sb.append(ss[0]);
		}
		
		for(int i=1;i<ss.length;i++)
		{
			sb.append(s).append(ss[i]);
		}
		
		return sb.toString();
	}
	
	/**
	 * 
	 */
	public static String trim(String str)
	{
		if(str==null)
		{
			return null;
		}
		else
		{
			return str.trim();
		}
	}
	
	// 将对象转换为String
	public static String toString(Object obj) {
		String rtStr = "";
		if (obj == null) {
			return rtStr;
		} else {
			rtStr = obj.toString();
		}
		return rtStr.trim();
	}
	/**
	 * 
	 */
	public static void main(String[] args)
	{
		System.out.println(numToString(1,4));
	}
}
