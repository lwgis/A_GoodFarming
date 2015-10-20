package com.zhonghaodi.model;

import java.util.Comparator;

public class SortComparator implements Comparator<Crop> {

	@Override
	public int compare(Crop lhs, Crop rhs) {
		// TODO Auto-generated method stub
		return (lhs.getCategory()-rhs.getCategory());
	}

}
