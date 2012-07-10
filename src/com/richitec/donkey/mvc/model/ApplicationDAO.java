package com.richitec.donkey.mvc.model;

import javax.sql.DataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.ivyinfo.donkey.db.supplier.SupplierInfoBean;

public class ApplicationDAO {
	
	private JdbcTemplate jdbc;
	
	public void setDataSource(DataSource dataSource){
		jdbc = new JdbcTemplate(dataSource);
	}
	
	public int getAllSupplierInfoCount() {
		return jdbc.queryForInt("SELECT COUNT(*) FROM t_supplier_info");
	}
	
	public List<SupplierInfoBean> getSupplierInfos(int from, int to){
		return jdbc.query(
				"SELECT * FROM t_supplier_info LIMIT ?, ?", 
				new Object[] {from, to}, 
				new RowMapper<SupplierInfoBean>() {
					@Override
					public SupplierInfoBean mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						SupplierInfoBean bean = new SupplierInfoBean();
						bean.setId(rs.getString("id"));
						bean.setAppid(rs.getString("appid"));
						bean.setCallbackurl(rs.getString("callbackurl"));
						bean.setName(rs.getString("name"));
						bean.setSkey(rs.getString("skey"));
						return bean;
					}
				}
			);
	}
	
	public int addSupplierInfo(SupplierInfoBean bean) {
		return jdbc.update(
				"INSERT INTO t_supplier_info (name,callbackurl,skey,appid) VALUES (?,?,?,?)", 
				bean.getName(),
				bean.getCallbackurl(),
				bean.getSkey(),
				bean.getAppid());
	}
	
	public int deleteSupplierInfoByAppid(String appid) {
		return jdbc.update("DELETE FROM t_supplier_info WHERE appid=?", appid);
	}
	
	public int updateCallBackURL(String id, String newCallbackURL) {
		return jdbc.update("UPDATE t_supplier_info SET callbackurl=? WHERE id=?",
				newCallbackURL, id);
	}
	
	public String getCallbackURL(String appId){
		return jdbc.queryForObject(
				"SELECT callbackurl FROM t_supplier_info WHERE appid=?", 
				new Object[] {appId},
				String.class);
	}
}
