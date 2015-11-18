package com.suitong.mp.baidu.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
public class BaseDao {
	
	private String driverClassName = "oracle.jdbc.driver.OracleDriver";
    private String dbUrl = "jdbc:oracle:thin:@10.3.2.168:1521:orcl";
    private String dbUsername = "vspuser";
    private String dbPassword = "vsp";
    private DriverManagerDataSource dataSource;
    private JdbcTemplate jdbc;

    public BaseDao(String className, String url, String username, String password){
    	this.driverClassName = className;
    	this.dbUrl = url;
    	this.dbUsername = username;
    	this.dbPassword = password;
    	
    	dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUsername);
        dataSource.setPassword(dbPassword);
        
        jdbc = new JdbcTemplate(dataSource);
    }

	public JdbcTemplate getJdbc() {
		return jdbc;
	}

	public void setJdbc(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public String getDbUrl() {
		return dbUrl;
	}

	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

	public String getDbUsername() {
		return dbUsername;
	}

	public void setDbUsername(String dbUsername) {
		this.dbUsername = dbUsername;
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

	public DriverManagerDataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DriverManagerDataSource dataSource) {
		this.dataSource = dataSource;
	}

}
