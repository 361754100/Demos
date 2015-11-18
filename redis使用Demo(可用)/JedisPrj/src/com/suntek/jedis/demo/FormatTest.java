package com.suntek.jedis.demo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormatTest {
	
	private static List<String[]> listCollection = new ArrayList<String[]>();
	
	private static int sumCount = 0;
	
	private static int collectionCount = 1;
	
	private static List<Map<String, Object>> base_list = new ArrayList<Map<String, Object>>();
	private static String[] base_items = null;
	
	private static int index, start, end, start_bak, end_bak;
	
	public static void main(String[] args) throws UnsupportedEncodingException, IOException {
		
		List<String> base_list = new ArrayList<String>();
		
		Map<String,Object> itemA = new HashMap<String,Object>();
		itemA.put("C_TERMID", "a");
		
		Map<String,Object> itemB = new HashMap<String,Object>();
		itemB.put("C_TERMID", "b");
		
		Map<String,Object> itemC = new HashMap<String,Object>();
		itemC.put("C_TERMID", "c");
		
		Map<String,Object> itemD = new HashMap<String,Object>();
		itemD.put("C_TERMID", "d");
		
		Map<String,Object> itemE = new HashMap<String,Object>();
		itemE.put("C_TERMID", "e");

		Map<String,Object> itemF = new HashMap<String,Object>();
		itemF.put("C_TERMID", "f");

		Map<String,Object> itemG = new HashMap<String,Object>();
		itemG.put("C_TERMID", "g");

		Map<String,Object> itemH = new HashMap<String,Object>();
		itemH.put("C_TERMID", "h");
		
		Map<String,Object> itemI = new HashMap<String,Object>();
		itemI.put("C_TERMID", "i");

		Map<String,Object> itemJ = new HashMap<String,Object>();
		itemJ.put("C_TERMID", "j");
		
		Map<String,Object> itemK = new HashMap<String,Object>();
		itemK.put("C_TERMID", "k");
		
		Map<String,Object> itemL = new HashMap<String,Object>();
		itemL.put("C_TERMID", "l");
		
		base_list.add("a");
		base_list.add("b");
		base_list.add("c");
		base_list.add("d");
		base_list.add("e");
		base_list.add("f");
		base_list.add("g");
//		base_list.add(itemH);
//		base_list.add(itemI);
//		base_list.add(itemJ);
//		base_list.add(itemK);
//		base_list.add(itemL);
		
		base_items = new String[base_list.size()];
		base_list.toArray(base_items);
		
		for(int i = base_items.length; i>1; i--){
			collectionCount *= i;
		}
//		System.out.println(" collectionCount ------>"+collectionCount);
		
		try {
			if(base_items.length > 2){
				index = 0;
				start = 1;
				end = 2;
				start_bak = 1;
				end_bak = 2;
				sort(base_items);
			}else if(base_items.length == 2){
				index = 0;
				start = 0;
				end = 1;
				start_bak = 0;
				end_bak = 1;
				sort(base_items);
			}else if(base_items.length == 1){
				index = 0;
				start = 0;
				end = 0;
				start_bak = 0;
				end_bak = 0;
				sort(base_items);
			}
			
			while(sum <= collectionCount){
				sort(base_items);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for( String[] itemsList: listCollection){
			for(String item:itemsList){
				System.out.print(item +",");
			}
			System.out.println("");
		}
	}
	
	//记录总共交换了多少次
	private static int sum = 0;
	public static String[] sort(String[] items ) throws Exception{
		//如果交换次数达到组合最大值则结束递归
		if(collectionCount <= sum ){
			return null;
		}
		//如果达到子递归的最大值则以最原始的组合交换对象顺序，再进行递归
		int count1 = collectionCount/items.length-1;
		int count2 = base_items.length-1;
		
		if(count1 == sumCount && index < count2){
			String[] copyArray = new String[base_items.length];
			for(int i = 0; i<base_items.length; i++){
				copyArray[i] = base_items[i];
			}
			
			String beginItem = copyArray[0];
			String tempItem = beginItem;
			String nextItem = copyArray[index+1];
			copyArray[0] = nextItem;
			copyArray[index+1] = tempItem;
			
			base_items = new String[copyArray.length];
			for(int i = 0; i<copyArray.length; i++){
				base_items[i] = copyArray[i];
			}
			
			index++;
			sumCount = 0;
			start = start_bak;
			end = end_bak;
			sum++;
//			sort(copyArray, index, start, end, start_bak, end_bak);
		}
		
		if( end+1 >= items.length ){
			String itemStart = items[start];
			String itemTemp = itemStart;
			String itemEnd = items[end];
			items[start] = itemEnd;
			items[end] = itemTemp;
			String[] copyArray = new String[items.length];
			for(int i = 0; i<items.length; i++){
				copyArray[i] = items[i];
			}
			listCollection.add(copyArray);
			start = start_bak;
			end = end_bak;
			sumCount++;
			sum++;
//			sort(items, index, start, end, start_bak, end_bak);
		}else {
			String itemStart = items[start];
			String itemTemp = itemStart;
			String itemEnd = items[end];
			items[start] = itemEnd;
			items[end] = itemTemp;

			start = start+1;
			end = end+1;
			if(end >= items.length){
				start = start_bak;
				end = start_bak;
			}
			String[] copyArray = new String[items.length];
			for(int i = 0; i<items.length; i++){
				copyArray[i] = items[i];
			}
			listCollection.add(copyArray);
			sumCount++;
			sum++;
//			sort(items, index, start, end, start_bak, end_bak);
		}
		return null;
	}
	
	
}
