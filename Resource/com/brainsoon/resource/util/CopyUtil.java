package com.brainsoon.resource.util;
import com.brainsoon.semantic.ontology.model.DoFileHistory;
import com.brainsoon.semantic.ontology.model.DoFileQueue;

public class CopyUtil {
	public static void Copy(DoFileQueue queue, DoFileHistory history) throws Exception {
		history.setActionConveredfileUrl(queue.getActionConveredfileUrl());
		history.setActionImageUrl(queue.getActionImageUrl());
		history.setActionMatedataUrl(queue.getActionMatedataUrl());
		history.setActionTxtUrl(queue.getActionTxtUrl());
		history.setAlpha(queue.getAlpha());
		history.setColor(queue.getColor());
		history.setCreateTime(queue.getCreateTime());
		history.setFileFormat(queue.getFileFormat());
		history.setFileId(queue.getFileId());
		history.setFontName(queue.getFontName());
		history.setFontSize(queue.getFontSize());
		history.setImageWH(queue.getImageWH());
		history.setIsBold(queue.getIsBold());
		history.setPendingType(queue.getPendingType());
		history.setPlatformId(queue.getPlatformId());
		history.setPosition(queue.getPosition());
		history.setPriority(queue.getPriority());
		history.setResId(queue.getResId());
		history.setResultConveredfilePath(queue.getResultConveredfilePath());
		history.setResultImagePath(queue.getResultImagePath());
		history.setResultMatedata(queue.getResultMatedata());
		history.setResultTxt(queue.getResultTxt());
		history.setRetryNum(queue.getRetryNum());
		history.setSrcPath(queue.getSrcPath());
		history.setStatusConvered(queue.getStatusConvered());
		history.setStautsExtractImage(queue.getStautsExtractImage());
		history.setStautsExtractMatedata(queue.getStautsExtractMatedata());
		history.setStautsExtractTxt(queue.getStautsExtractTxt());
		history.setSyncStautsConvered(queue.getSyncStautsConvered());
		history.setSyncStautsImage(queue.getSyncStautsImage());
		history.setSyncStautsMatedata(queue.getSyncStautsMatedata());
		history.setSyncStautsTxt(queue.getSyncStautsTxt());
		history.setTimestamp(queue.getTimestamp());
		history.setTxtName(queue.getTxtName());
		history.setUpdateTime(queue.getUpdateTime());
		history.setWmImage(queue.getWmImage());
		history.setObjectId(queue.getObjectId());
	}
	
	public static void CopyToQueue(DoFileHistory history, DoFileQueue queue) throws Exception {
		queue.setActionConveredfileUrl(history.getActionConveredfileUrl());
		queue.setActionImageUrl(history.getActionImageUrl());
		queue.setActionMatedataUrl(history.getActionMatedataUrl());
		queue.setActionTxtUrl(history.getActionTxtUrl());
		queue.setAlpha(history.getAlpha());
		queue.setColor(history.getColor());
		queue.setCreateTime(history.getCreateTime());
		queue.setFileFormat(history.getFileFormat());
		queue.setFileId(history.getFileId());
		queue.setFontName(history.getFontName());
		queue.setFontSize(history.getFontSize());
		queue.setImageWH(history.getImageWH());
		queue.setIsBold(history.getIsBold());
		queue.setPendingType(history.getPendingType());
		queue.setPlatformId(history.getPlatformId());
		queue.setPosition(history.getPosition());
		queue.setPriority(history.getPriority());
		queue.setResId(history.getResId());
		queue.setResultConveredfilePath(history.getResultConveredfilePath());
		queue.setResultImagePath(history.getResultImagePath());
		queue.setResultMatedata(history.getResultMatedata());
		queue.setResultTxt(history.getResultTxt());
		queue.setRetryNum(history.getRetryNum());
		queue.setSrcPath(history.getSrcPath());
		queue.setStatusConvered(history.getStatusConvered());
		queue.setStautsExtractImage(history.getStautsExtractImage());
		queue.setStautsExtractMatedata(history.getStautsExtractMatedata());
		queue.setStautsExtractTxt(history.getStautsExtractTxt());
		queue.setSyncStautsConvered(history.getSyncStautsConvered());
		queue.setSyncStautsImage(history.getSyncStautsImage());
		queue.setSyncStautsMatedata(history.getSyncStautsMatedata());
		queue.setSyncStautsTxt(history.getSyncStautsTxt());
		queue.setTimestamp(history.getTimestamp());
		queue.setTxtName(history.getTxtName());
		queue.setUpdateTime(history.getUpdateTime());
		queue.setWmImage(history.getWmImage());
		queue.setObjectId(history.getObjectId());
	}
}