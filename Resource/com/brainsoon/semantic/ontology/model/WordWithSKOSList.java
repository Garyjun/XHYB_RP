package com.brainsoon.semantic.ontology.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WordWithSKOSList extends BaseObjectList {
	private List<WordWithSKOS> words;
	
	public void addWord(WordWithSKOS word) {
		if(words == null) {
			words = new ArrayList<WordWithSKOS>();
		}
		words.add(word);
	}

	public void setWords(List<WordWithSKOS> words) {
		this.words = words;
	}
	
	public List<WordWithSKOS> getWords() {
		return words;
	}
}
