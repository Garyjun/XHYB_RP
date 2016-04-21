package com.brainsoon.rp.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.brainsoon.rp.model.annotation.Annotation;
import com.brainsoon.rp.model.annotation.Range;
import com.brainsoon.rp.service.IAnnotatorService;
import com.brainsoon.rp.support.TreeNode;

/**
 * <dl>
 * <dt>AnnotatorService.java</dt>
 * <dd>Function: {!!一句话描述功能}</dd>
 * <dd>Description:{详细描述该类的功能}</dd>
 * <dd>Copyright: Copyright (C) 2010</dd>
 * <dd>Company: 北京博云易迅技术有限公司</dd>
 * <dd>CreateUser: xujie</dd>
 * <dd>CreateDate: 2016年3月7日</dd>
 * </dl>
 */
@Service
public class AnnotatorService implements IAnnotatorService {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.brainsoon.rp.service.IAnnotatorService#create(com.brainsoon.rp.model
	 * .annotation.Annotation)
	 */
	@Override
	public void create(Annotation a) {
		String sql = "insert into rp_annotation ("
					+ "text, "
					+ "quote, "
					+ "resId, "
					+ "resType, "
					+ "uri, "
					+ "start, "
					+ "startOffset, "
					+ "end, "
					+ "endOffset, "
					+ "share, "
					+ "userId, "
					+ "created, "
					+ "updated) " 
				+ "values('" 
					+ a.getText() + "'," + "'" 
					+ a.getQuote() + "','" 
					+ a.getResId() + "','" 
					+ a.getResType() + "','" 
					+ a.getUri() + "','" 
					+ a.getRanges().get(0).getStart() + "','" 
					+ a.getRanges().get(0).getStartOffset() + "','" 
					+ a.getRanges().get(0).getEnd() + "','" 
					+ a.getRanges().get(0).getEndOffset() + "',"
					+ "NULL,"
					+ "NULL," 
					+ a.getCreated()+ ","
					+ a.getUpdated() + ")";
		jdbcTemplate.execute(sql);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.brainsoon.rp.service.IAnnotatorService#findLimitAnnotations(int,
	 * java.lang.String)
	 */
	@Override
	public List<Annotation> findAnnotations(int limit, String uri) {
		List<Annotation> annotations = this.jdbcTemplate.query("SELECT * FROM rp_annotation WHERE uri = '" + uri + "'", new RowMapper<Annotation>() {
			public Annotation mapRow(ResultSet rs, int rowNum) throws SQLException {
				Annotation annotation = populateAnnotation(rs);
				return annotation;
			}

			public Annotation populateAnnotation(ResultSet rs) throws SQLException {
				Annotation annotation = new Annotation();
				Range range = new Range();
				range.setStart(rs.getString("start")); 
				range.setStartOffset(rs.getInt("startOffset"));
				range.setEnd(rs.getString("end"));
				range.setEndOffset(rs.getInt("endOffset"));
				List<Range> ranges = new ArrayList<Range>();
				ranges.add(range);
				annotation.setRanges(ranges);
				annotation.setQuote(rs.getString("quote"));
				annotation.setText(rs.getString("text"));
				annotation.setId(rs.getInt("id"));
				annotation.setUri(rs.getString("uri"));
				annotation.setResId(rs.getString("resId"));
				annotation.setResType(rs.getString("resType"));
				//annotation.setUpdated(rs.getTimestamp("updated"));
				//annotation.setCreated(rs.getTimestamp("created"));
				return annotation;
			}
		});
		return annotations;
	}

}
