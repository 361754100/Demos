package com.suntek.jedis.demo;

public class StringTest {

	public static void main(String[] args) {
		String AA = "AA";
		String BB = AA;
		
		BB = "cc";
		
		System.out.println(" AA --->"+AA);
		System.out.println(" BB --->"+BB);
	}

}
