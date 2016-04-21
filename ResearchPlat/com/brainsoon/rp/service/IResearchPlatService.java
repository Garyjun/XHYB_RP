package com.brainsoon.rp.service;

import java.util.List;

import com.brainsoon.rp.support.SearchParam;
import com.brainsoon.rp.support.DataTableResult;
import com.brainsoon.rp.support.TreeNode;

public interface IResearchPlatService {

	List<TreeNode> generateNavTreeFromMysql(String pid);

	List<TreeNode> generateTreeNodesFromXML(String id, String value, String pValue);

	DataTableResult queryResList(SearchParam searchParam);
}
