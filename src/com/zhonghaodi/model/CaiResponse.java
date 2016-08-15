package com.zhonghaodi.model;

import java.io.Serializable;

public class CaiResponse extends CaiComment implements Serializable {

	private boolean correct;
	
	public CaiResponse(){
		super();
	}

	public boolean isCorrect() {
		return correct;
	}

	public void setCorrect(boolean correct) {
		this.correct = correct;
	}

	@Override
	public String toString() {
		return "CaiResponse [correct=" + correct + "]";
	}
	
	
}
