package com.hro.core.common.util;


/**
 * Replace with @see com.suntek.util.StringUtil
 */
public class StringUtil {

	/**
	 * ˵��:
	 * 		�� numToString( 123456789, 4 )
	 * 		�� 125369 ת��Ϊ 5369
	 * 
	 * 		�� numToString( 89, 4 )
	 * 		�� 89 ת��Ϊ 0089
	 * 
	 * 		������ұߵ�4λ,�������0����.
	 * 
	 * @param num
	 * 		Ҫ����ת������
	 * @param len 
	 * 		Ҫ���������λ��
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
	 * �ж��Ƿ��ǿմ���"   \r   \n  \r\n" Ҳ���ǿմ�
	 */
	public static boolean isEmpty(String input)
	{
		boolean isEmpty = true;		//Ĭ���ǿմ�
		
		if(input!=null)
		{
			for(int i=0;i<input.length();i++)	//ֻҪ��һ���ַ����� ' '��'\r','\n'���������Ƿǿյ�
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
	 * ˵��: ��
	 * 			"20080612"
	 * 		�������ַ�����Ϊ
	 * 			"2008-06-12"
	 * 		ֻ�Ǽ� "-" ����
	 */
	public static String strToDate(String input)
	{
		String rs = input.substring(0, 4)+"-"+input.substring(4, 6)+"-"+input.substring(6, 8);
		return rs; 
	}
	
	/**
	 * ˵��: ��
	 * 			"210338"
	 * 		�������ַ�����Ϊ
	 * 			"21:03:38"
	 * 		ֻ�Ǽ� ":" ����
	 */
	public static String strToTime(String input)
	{
		String rs = input.substring(0, 2)+":"+input.substring(2, 4)+":"+input.substring(4, 6);
		return rs; 
	}
	
	/**
	 * 
	 * author : �º���
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
	 * ˵��:
	 * 		��ss����������ַ�����������
	 * 		���ӷ�����s
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
	
	// ������ת��ΪString
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
