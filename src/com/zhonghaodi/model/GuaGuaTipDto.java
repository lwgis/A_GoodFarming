package com.zhonghaodi.model;

import java.io.Serializable;

public class GuaGuaTipDto implements Serializable {

	private int points;
	private int numbers;
	
	public GuaGuaTipDto(){
		
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public int getNumbers() {
		return numbers;
	}

	public void setNumbers(int numbers) {
		this.numbers = numbers;
	}
	
}
