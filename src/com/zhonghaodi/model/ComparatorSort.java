package com.zhonghaodi.model;

import java.util.Comparator;

public class ComparatorSort implements Comparator<Category_disease> {

	@Override
	public int compare(Category_disease lhs, Category_disease rhs) {
		// TODO Auto-generated method stub
		return (lhs.getId()-rhs.getId());
	}

}
