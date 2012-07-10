package com.richitec.donkey.mvc.model;

import javax.sql.DataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class ApplicationDAO {
	
	private JdbcTemplate jdbc;
	
	public void setDataSource(DataSource dataSource){
		jdbc = new JdbcTemplate(dataSource);
	}
	
	public int getApplicationCount() {
		return jdbc.queryForInt("SELECT COUNT(*) FROM t_supplier_info");
	}
	
	public List<JSONObject> getApplications(int from, int to){
		return jdbc.query(
				"SELECT * FROM t_supplier_info LIMIT ?, ?", 
				new Object[] {from, to}, 
				new RowMapper<JSONObject>() {
					@Override
					public JSONObject mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						JSONObject app = new JSONObject();
						try {
							app.put("id", rs.getString("id"));
							app.put("name", rs.getString("name"));
							app.put("appid", rs.getString("appid"));
							app.put("appkey", rs.getString("skey"));
							app.put("callbackurl", rs.getString("callbackurl"));
						} catch (JSONException e) {
							e.printStackTrace();
						}
						return app;
					}
				}
			);
	}
	
	public int addApplication(String name, String callbackURL, String skey, String appId) {
		return jdbc.update(
				"INSERT INTO t_supplier_info (name,callbackurl,skey,appid) VALUES (?,?,?,?)", 
				name, callbackURL, skey, appId);
	}
	
	public int delApplicationByAppId(String appid) {
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
