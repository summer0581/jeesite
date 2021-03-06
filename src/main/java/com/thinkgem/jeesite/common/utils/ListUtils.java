package com.thinkgem.jeesite.common.utils;

import java.util.ArrayList;
import java.util.List;

import com.thinkgem.jeesite.modules.finance.entity.RentMonth;

public class ListUtils {
	
	
	/**
	 * 获取指定下标的字符串
	 * @param list
	 * @param index
	 * @return 
	 * @return
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static <E> E getStringWithIndex(List<E> list,int index,Class<E> s) throws InstantiationException, IllegalAccessException{
		if(null != list && list.size() > index){
			return list.get(index);
		}else{
			if(null == list){
				list = new ArrayList<E>();
			}
			list.add(index, s.newInstance());
			return list.get(index);
		}
	}
	
	/**
	 * 根据下标返回集合中相应的元素，如果集合为空或者集合大小小于下标，则返回为空
	 * @param list
	 * @param index
	 * @return
	 */
	public static <E> E getElementByIndex(List<E> list,int index){
		if(null != list && list.size() > index){
			return list.get(index);
		}else{
			return null;
		}
	}
	public static void main(String[] args) throws InstantiationException, IllegalAccessException {
		List<RentMonth> ss = new ArrayList<RentMonth>();
		RentMonth rentMonth = ListUtils.getStringWithIndex(ss, 0,RentMonth.class);
		System.out.println(rentMonth.getId());
	}
}

