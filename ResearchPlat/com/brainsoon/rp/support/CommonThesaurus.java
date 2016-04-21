package com.brainsoon.rp.support;

import java.util.ArrayList;
import java.util.HashMap;

public class CommonThesaurus {
	private static long buildIDIndex = 0;
	protected HashMap<Long, String> wordList = new HashMap<Long, String>();
	private HashMap<String, Long> wordIndex = new HashMap<String, Long>();
	protected HashMap<Long, ArrayList<Long>> relatedIndex = new HashMap<Long, ArrayList<Long>>();
	private HashMap<Long, ArrayList<Long>> subjectIndex = new HashMap<Long, ArrayList<Long>>();

	public void addThesaurus(String subjectWord, String relatedWord) {
		Long subjectID = wordIndex.get(subjectWord);
		if (subjectID == null) {
			buildIDIndex++;
			subjectID = buildIDIndex;
			wordList.put(subjectID, subjectWord);
			wordIndex.put(subjectWord, subjectID);
		}
		Long relatedID = wordIndex.get(relatedWord);
		if (relatedID == null) {
			buildIDIndex++;
			relatedID = buildIDIndex;
			wordList.put(relatedID, relatedWord);
			wordIndex.put(relatedWord, relatedID);
		}
		addThesaurus(subjectID, relatedID);
	}

	public void addWordWithID(Long id, String Word) {
		wordList.put(id, Word);
		wordIndex.put(Word, id);
	}

	public void addThesaurus(Long subjectID, Long relatedID) {
		ArrayList<Long> list = null;

		list = relatedIndex.get(subjectID);
		if (list == null)
			list = new ArrayList<Long>();
		if (!list.contains(relatedID)) {
			list.add(relatedID);
			relatedIndex.put(subjectID, list);
		}

		list = subjectIndex.get(relatedID);
		if (list == null)
			list = new ArrayList<Long>();
		if (!list.contains(subjectID)) {
			list.add(subjectID);
			subjectIndex.put(relatedID, list);
		}
	}

	public String getWord(Long wordID) {
		return wordList.get(wordID);
	}

	public Long getWordID(String word) {
		return wordIndex.get(word);
	}

	public ArrayList<Long> getRelatedWordList(Long subjectID) {
		return relatedIndex.get(subjectID);
	}

	public ArrayList<Long> getSubjectWordList(Long relatedID) {
		return subjectIndex.get(relatedID);
	}
}
