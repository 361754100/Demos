package com.suitong.mp.baidu.service;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String modifyTime = "440600304533,0300A495,440600104117,440600304481,440600304483,36020100,36022740,0300A545,40P1,36020652,0300A561,0300A546,440600602050,66060100,20131128,20131129,20131130,0300A473,0300A486,0300A411,66060075,0300A516,36022827,440600104139,89038,10000401,0300A540,440600104141,36022800,40A2,440600304922,66060084,84014,0300A549,440600103888,440600305036,440600103063,20142068,20140078,20140079,20140115,20140121,20140122,20140124,20140112,32929,36021051,440600602576,36021688,20135044,36020548,36020859,402U,0300A143,20141075,0300A232,0300A233A,B041,36022530,36022531,B033,20137096,0300A260,B029,02000124A,10000173,0300A269,0300A291,02000017A,20142087,20135132,20140068,20140008,20137100,36022398,20140072,36022419,440600102286,B032";		String[] modifyArr = modifyTime.split(",");
		String str = "33,4085A";
		int idx = str.lastIndexOf(",");
		System.out.println(" modifyArr --->"+ idx + "  substr --->"+ str.substring(idx+1, str.length()) );
	}

}