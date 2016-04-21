package com.brainsoon.rp.support;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class CommonCategory {
	private HashMap<Long, CommonCategoryNode> nodes = new HashMap<Long, CommonCategoryNode>();
	private HashMap<Long, ArrayList<Long>> childrenIndex = new HashMap<Long, ArrayList<Long>>();
	private HashMap<String, Long> nameIndex = new HashMap<String, Long>();
	private HashMap<String, Long> codeIndex = new HashMap<String, Long>();

	public void addNode(CommonCategoryNode nd) {
		nodes.put(nd.getID(), nd);

		ArrayList<Long> list = childrenIndex.get(nd.getParentID());
		if (list == null) {
			ArrayList<Long> newList = new ArrayList<Long>();
			newList.add(nd.getID());
			childrenIndex.put(nd.getParentID(), newList);
		} else {
			list.add(nd.getID());
			childrenIndex.put(nd.getParentID(), list);
		}
		nameIndex.put(nd.getName(), nd.getID());
		codeIndex.put(nd.getCode(), nd.getID());
	}

	public CommonCategoryNode getNode(long id) {
		return (CommonCategoryNode) nodes.get(id);
	}

	public CommonCategoryNode getNodeByName(String name) {
		return (CommonCategoryNode) nodes.get(nameIndex.get(name));
	}

	public CommonCategoryNode getNodeByCode(String code) {
		return (CommonCategoryNode) nodes.get(codeIndex.get(code));
	}

	public ArrayList<Long> getChildrenID(long parentID) {
		return childrenIndex.get(parentID);
	}

	public void loadFromDB(String sql) {
		try {
			Statement stmt = Utils.getConnection().createStatement();

			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				CommonCategoryNode nd = new CommonCategoryNode();
				nd.setID(rs.getLong("ID"));
				nd.setName(rs.getString("Name"));
				nd.setCode(rs.getString("Code"));
				nd.setParentID(rs.getLong("ParentID"));
				addNode(nd);
			}
			stmt.close();
		} catch (SQLException e) {
			// DataProcessLog.getInstance().log(this.getClass().getName(),
			// "[fillWithID] module SQL error[:" + e.getErrorCode() + "] " +
			// e.getMessage() + "\nSQL 语句：\n" + SQL);
			e.printStackTrace();
		}
	}

	public void loadFromText(File f) {
		if (f.exists()) {
			try {
				InputStreamReader read = new InputStreamReader(new FileInputStream(f), "UTF8");// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String line = null;

				while ((line = bufferedReader.readLine()) != null) {
					if (line.trim() != "") {
						String fields[] = line.split("\t");
						CommonCategoryNode nd = new CommonCategoryNode();
						nd.setID(Long.valueOf(fields[0].trim()));
						nd.setName(fields[1].trim());
						nd.setCode(fields[2].trim());
						nd.setParentID(Long.valueOf(fields[3].trim()));
						
						addNode(nd);
					}
				}
				read.close();

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
