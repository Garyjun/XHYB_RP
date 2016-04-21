package com.brainsoon.semantic.ontology.model;

import java.util.List;
import java.util.Map;


public class SmallQuestion{	
	
	private String questionDesc;
	private String optionCount;
	private List<QuestionOption> questionOption;
	public String getQuestionDesc() {
		return questionDesc;
	}
	public void setQuestionDesc(String questionDesc) {
		this.questionDesc = questionDesc;
	}
	public String getOptionCount() {
		return optionCount;
	}
	public void setOptionCount(String optionCount) {
		this.optionCount = optionCount;
	}
	public List<QuestionOption> getQuestionOption() {
		return questionOption;
	}
	public void setQuestionOption(List<QuestionOption> questionOption) {
		this.questionOption = questionOption;
	}
	
	
}
