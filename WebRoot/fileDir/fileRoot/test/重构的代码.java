功能：加工管理-》添加资源-》添加全部页（有可能前面添加了一些，所以要考虑查重）

后台Action：

	@RequestMapping(value = baseUrl + "saveAllResByCondition")
	@ResponseBody
	public String saveAllResByCondition(Long taskId, String conditions){
		taskProcessService.saveAllResByCondition(taskId, conditions);
		return "sucess";
	}
	
后台Service：

	@Override
	public void saveAllResByCondition(Long taskId, String conditions){
		//第一步：查询加工任务详细信息
		TaskProcess taskProcess = getTaskProcessInfo(taskId);
		if(taskProcess != null){
			//第二步：查询所有的资源信息，返回所有的资源id json串
			JSONArray resArr = getResourcesByCondition(conditions, taskProcess.getResType());
			if(resArr!= null){
				//第三步：找所本加工任务下的所有已经存在的资源集合
				List<String> resIdList = getAllSysResDirectoryList(taskId);
				//第四步：循环遍历资源id信息，不存在的则加入（创建），已存在的则过滤掉
				Gson gson = new Gson();
				for(int i=0;i<resArr.size();i++){//循环资源
					Ca ca = gson.fromJson(resArr.get(i).toString(), Ca.class);//资源Ca
					addResource(ca.getObjectId(),resIdList,taskProcess);
				}
			}
		}
	}
	
	/**
	 * 通过taskProcessId获取加工任务详细信息
	 * @param taskProcessId
	 * @return
	 */
	public TaskProcess getTaskProcessInfo(Long taskProcessId){
		if(taskProcessId == null && taskProcessId.longValue() ==0l){
			return null;
		}
		return (TaskProcess) getByPk(TaskProcess.class, taskProcessId);
	}
	
	/**
	 * 根据查询条件、资源类型查询该资源的所有的资源信息，返回所有的资源id json串
	 * @param conditions
	 * @param resType
	 * @return
	 */
	private JSONArray getResourcesByCondition(String conditions, String resType) {
		HttpClientUtil http = new HttpClientUtil();
		String url = WebappConfigUtil.getParameter("PUBLISH_QUERY_URL") + "?queryType=1&page=1&size=10000000&publishType=" + resType;
		if(conditions!=null&&!"''".equals(conditions)){
			try {
				conditions = URLEncoder.encode(conditions, "UTF-8");
				url += "&metadataMap=" + conditions;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		String userIds = LoginUserUtil.getLoginUser().getDeptUserIds();
		int isPrivate = LoginUserUtil.getLoginUser().getIsPrivate();
		if (isPrivate == 1) {
				userIds = LoginUserUtil.getLoginUser().getUserId()+",";
			}
		if (StringUtils.isNotBlank(userIds)) {
			if(userIds.endsWith(",")){
				userIds = userIds.substring(0,userIds.length()-1);
			}
			url+="&creator="+userIds;
		}else{
			userIds = LoginUserUtil.getLoginUser().getUserId()+",";
			url+="&creator="+userIds;
		}
		String resources = http.executeGet(url);
		JSONObject allJson = JSONObject.fromObject(resources);
		JSONArray allResult = (JSONArray) allJson.get("rows");
		return allResult;
	}
	
	/**
	 * 通过taskProcessId 查询本加工任务下的所有的资源元数据
	 * @param taskProcessId
	 * @return
	 */
	public List<String> getAllSysResDirectoryList(Long taskProcessId){
		List<String> resIdList = null;
		if(taskProcessId != null && taskProcessId.longValue() !=0l){
			List<TaskDetail> detailList = query(" from TaskDetail where taskProcess.id=" + taskProcessId);
			if(detailList!=null && detailList.size()>0){
				resIdList = new ArrayList<String>();
				for(TaskDetail detail: detailList){
					List<TaskResRelation> tempResList = query(" from TaskResRelation where taskDetail.id=" + detail.getId());
					if(tempResList!=null&&tempResList.size()>0){
						resIdList.add(tempResList.get(0).getSysResDirectoryId());
					}
				}
			}
		}
		return resIdList;
	}
	
	
	/**
	 *通过taskProcessId获取加工任务详细信息
	 * @param resourceIds 本次要添加到加工任务的资源Ids
	 * @param resIdList 本次动作前 本任务中已有的资源Ids
	 * @param taskProcessId 本次加工任务信息
	 * @return 
	 */
	public void addResource(String resourceIds,List<String> resIdList,TaskProcess taskProcess){
		String[] ids = resourceIds.split(",");
		for(int i=0; i<ids.length;i++){
			if(resIdList!=null){
				if(resIdList.contains(ids[i])){
					continue;
				}
			}
			HttpClientUtil http = new HttpClientUtil();
			String json = http.executeGet(url + "?id=" + ids[i]);
			TaskDetail taskDetail = new TaskDetail();
			taskDetail.setTaskProcess(taskProcess);
			taskDetail.setDescription(taskProcess.getDescription());
			taskDetail.setStartTime(taskProcess.getStartTime());
			taskDetail.setEndTime(taskProcess.getEndTime());
			taskDetail.setStatus(0);
			create(taskDetail);
			
			TaskResRelation taskResRelation = new TaskResRelation();
			taskResRelation.setTaskDetail(taskDetail);
			taskResRelation.setSysResDirectoryId(ids[i]);
			JSONObject jo = JSONObject.fromObject(json);
			JSONObject object = jo.getJSONObject("metadataMap");
			Object publishType = jo.get("publishType");
			if(publishType!=null){
				taskResRelation.setPublishType(publishType.toString());
			}
			if(object!=null){
				Object obj = object.get("title");
				if(obj!=null){
					taskResRelation.setResName(obj.toString());
				}
			}
			taskResRelation.setStatus(0);
			create(taskResRelation);
		}
	}