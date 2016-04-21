package com.brainsoon.resource.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.resource.po.Area;
import com.brainsoon.resource.po.City;
import com.brainsoon.resource.po.Province;
import com.brainsoon.resource.service.IAreaService;

/**
 * 获取省市地区
 * @author zuo
 */
@Service
@SuppressWarnings("rawtypes")
public class AreaService extends BaseService implements IAreaService{
	
	public List getProvince(){
		String hql = "SELECT new Province(c.code,c.name) FROM Province c";
		return query(hql);
	}
	
	public List getCity(String code){
		String hql = "SELECT new City(c.code,c.name) FROM City c WHERE c.provincecode = :provincecode";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("provincecode", code);
		return query(hql, parameters);
	}
	
	public List getArea(String code){
		String hql = "SELECT new Area(c.code,c.name) FROM  Area c WHERE c.citycode = :citycode";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("citycode", code);
		return query(hql, parameters);
	}
	
	public Province getProvinceByProvCode(String provCode){
		String hql = "SELECT new Province(c.code,c.name) FROM Province c WHERE c.code = :provCode";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("provCode", provCode);
		Province province=null;
		List<Province> provinces=query(hql, parameters);
		if(provinces.size()>0){
			province=provinces.get(0);
		}
		return province;
	}
	
	public City getCityByCityCode(String cityCode){
		String hql = "SELECT new City(c.code,c.name) FROM City c WHERE c.code = :cityCode";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("cityCode", cityCode);
		List<City> citys=query(hql, parameters);
		City city=null;
		if(citys.size()>0){
			city=citys.get(0);
		}
		return city;
		
	}
	
	public Area getAreaByAreaCode(String areaCode){
		String hql = "SELECT new Area(c.code,c.name) FROM  Area c WHERE c.code = :areaCode";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("areaCode", areaCode);
		List<Area> areas=query(hql, parameters);
		Area area=null;
		if(areas.size()>0){
			area=areas.get(0);
		}
		return area;
		
	}
	/**
	 * 取得所属地区信息
	 * @param provCode
	 * @param cityCode
	 * @param areaCode
	 * @return
	 */
	public String getRegionInfo(String region ){
		String regionInfo="";
		String[] regions=region.split(",");
		if(regions.length==0||StringUtils.isBlank(regions[0])){
			return regionInfo;
		}
		String provCode=regions[0];
		String cityCode=regions[1];
		String areaCode=regions[2];
		Province province =getProvinceByProvCode(provCode);
		City city=getCityByCityCode(cityCode);
		Area area=getAreaByAreaCode(areaCode);
		if(province!=null){
			regionInfo=regionInfo+province.getName()+",";
		}
		if(city!=null){
			regionInfo=regionInfo+city.getName()+",";
		}
		if(area!=null){
			regionInfo=regionInfo+area.getName()+",";
		}
		if(regionInfo.length()>0){
			regionInfo=regionInfo.substring(0,regionInfo.length()-1);
		}
		return regionInfo;
	}
	
}
