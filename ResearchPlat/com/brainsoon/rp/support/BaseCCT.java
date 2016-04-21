package com.brainsoon.rp.support;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/*
 *  Chinese Subject Label Word List
 *  中国分类主题词表——中分表
 *  Version 待定
 */
public class BaseCCT extends CommonThesaurus {
	private static BaseCCT cct = null;

	private HashMap<Long, ArrayList<Long>> clc2wordIndex = new HashMap<Long, ArrayList<Long>>();
	private HashMap<Long, ArrayList<Long>> word2clcIndex = new HashMap<Long, ArrayList<Long>>();

	public static BaseCCT getInstance() {
		if (cct == null) {
			cct = new BaseCCT();
		}
		return cct;
	}

	public void addCCTItem(String Code, String Word) {
		BaseCLC clc = BaseCLC.getInstance();
		if (clc.getNodeByCode(Code) == null)
			return;
		Long clcID = clc.getNodeByCode(Code).getID();
		Long wordID = super.getWordID(Word);
		addCCTItem(clcID, wordID);
	}

	public void addCCTItem(Long clcID, Long wordID) {
		ArrayList<Long> list = null;

		list = clc2wordIndex.get(clcID);
		if (list == null)
			list = new ArrayList<Long>();
		if (!list.contains(wordID)) {
			list.add(wordID);
			clc2wordIndex.put(clcID, list);
		}

		list = word2clcIndex.get(wordID);
		if (list == null)
			list = new ArrayList<Long>();
		if (!list.contains(clcID)) {
			list.add(clcID);
			word2clcIndex.put(wordID, list);
		}
	}

	public ArrayList<Long> getCLCByWord(Long wordID) {
		return word2clcIndex.get(wordID);
	}

	public ArrayList<Long> getWordByCLC(Long clcID) {
		return clc2wordIndex.get(clcID);
	}

	public void export() {
		try {
			Iterator iter = wordList.entrySet().iterator();
			FileWriter fw = new FileWriter("baseData/CCTWordList.txt");
			while (iter.hasNext()) {
				Map.Entry entry = (Entry) iter.next();
				Long id = (Long) entry.getKey();
				String word = (String) entry.getValue();
				fw.write(id + "\t" + word + "\n");
			}
			fw.close();

			iter = relatedIndex.entrySet().iterator();
			fw = new FileWriter("baseData/CCTWordRel.txt");
			while (iter.hasNext()) {
				Map.Entry entry = (Entry) iter.next();
				Long subID = (Long) entry.getKey();
				ArrayList<Long> list = (ArrayList<Long>) entry.getValue();
				for (int i = 0; i < list.size(); i++) {
					fw.write(subID + "\t" + (Long) list.get(i) + "\n");
				}
			}
			fw.close();

			iter = clc2wordIndex.entrySet().iterator();
			fw = new FileWriter("baseData/CLC2CCTWord.txt");
			while (iter.hasNext()) {
				Map.Entry entry = (Entry) iter.next();
				Long clcID = (Long) entry.getKey();
				ArrayList<Long> list = (ArrayList<Long>) entry.getValue();
				for (int i = 0; list != null && i < list.size(); i++) {
					fw.write(clcID + "\t" + (Long) list.get(i) + "\n");
				}
			}
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean loadFromText(String path) {
		String line = null;

		try {
			File f = new File(path + "/CCTWordList.txt");
			if (!f.exists())
				return false;

			InputStreamReader read = new InputStreamReader(new FileInputStream(f), "UTF-8");// 考虑到编码格式
			BufferedReader bufferedReader = new BufferedReader(read);

			while ((line = bufferedReader.readLine()) != null) {
				if (line.trim() != "") {
					String fields[] = line.split("\t");
					Long id = Long.valueOf(fields[0].trim());
					String word = "";
					if (fields.length == 2)
						word = fields[1].trim();
					super.addWordWithID(id, word);
				}
			}
			read.close();

			f = new File(path + "/CCTWordRel.txt");
			if (!f.exists())
				return false;
			read = new InputStreamReader(new FileInputStream(f), "GBK");// 考虑到编码格式
			bufferedReader = new BufferedReader(read);

			while ((line = bufferedReader.readLine()) != null) {
				if (line.trim() != "") {
					String fields[] = line.split("\t");
					Long subjectID = Long.valueOf(fields[0].trim());
					Long relatedID = Long.valueOf(fields[1].trim());
					super.addThesaurus(subjectID, relatedID);
				}
			}
			read.close();

			f = new File(path + "/CLC2CCTWord.txt");
			if (!f.exists())
				return false;
			read = new InputStreamReader(new FileInputStream(f), "GBK");// 考虑到编码格式
			bufferedReader = new BufferedReader(read);

			while ((line = bufferedReader.readLine()) != null) {
				if (line.trim() != "") {
					String fields[] = line.split("\t");
					Long clcID = Long.valueOf(fields[0].trim());
					Long wordID = Long.valueOf(fields[1].trim());
					this.addCCTItem(clcID, wordID);
				}
			}
			read.close();
		} catch (Exception ex) {
			System.err.println("Error Line:" + line);
			ex.printStackTrace();
		}
		return true;
	}
}
