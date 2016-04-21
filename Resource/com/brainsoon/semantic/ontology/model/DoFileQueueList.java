package com.brainsoon.semantic.ontology.model;

import java.util.ArrayList;
import java.util.List;

public class DoFileQueueList {
	private List<DoFileQueue> doFileQueueList = new ArrayList<DoFileQueue>();

	public List<DoFileQueue> getDoFileQueueList() {
		return doFileQueueList;
	}

	public void setDoFileQueueList(List<DoFileQueue> doFileQueueList) {
		this.doFileQueueList = doFileQueueList;
	}
	
	public void addDoFileQueue(DoFileQueue doFileQueue){
		doFileQueueList.add(doFileQueue);
	}
}
