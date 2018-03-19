/**
 * 
 */
package com.suixingpay.datas.manager.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.suixingpay.datas.manager.core.datasource.DataSourceUtil;
import com.suixingpay.datas.manager.core.dto.JDBCVo;
import com.suixingpay.datas.manager.service.DbSelectService;
import com.suixingpay.datas.manager.web.page.Page;

/**
 * @author guohongjian[guo_hj@suixingpay.com]
 *
 */
@Service("dbORACLESelectService")
public class DbOracleSelectService implements DbSelectService{

    @Override
    public List<String> list(JDBCVo jvo, String sql, Map<String, Object> map) {
        List<String> list = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet results = null;
        try {
            connection = DataSourceUtil.getConnection(jvo.getDriverName(), jvo.getUrl(), jvo.getUsername(), jvo.getPassword());
            preparedStatement = connection.prepareStatement(sql);
            results = preparedStatement.executeQuery();
            while (results.next()) {
                String prefixName = results.getString("prefixName");
                list.add(prefixName);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            DataSourceUtil.closed(connection, preparedStatement, results);
        }
        return list;
    }

    @Override
    public Long pageTotal(JDBCVo jvo, String sql, String prefix, String tableName1) {
        Long total = 0l;
        String executeSql = "select count(*) as tatal from ("+sql+") as t where %term";
        StringBuffer termSql = new StringBuffer("1=1");
        if(prefix!=null&&!prefix.equals("")) {
            termSql.append(" and lower(prefixName) = '"+prefix.toLowerCase()+"' ");
        }
        if(tableName1!=null&&tableName1.equals("")) {
            termSql.append(" and lower(tableName) like '%"+tableName1.toLowerCase()+"%' ");
        }
        executeSql = executeSql.replace("%term", termSql);
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet results = null;
        try {
            connection = DataSourceUtil.getConnection(jvo.getDriverName(), jvo.getUrl(), jvo.getUsername(), jvo.getPassword());
            preparedStatement = connection.prepareStatement(executeSql);
            results = preparedStatement.executeQuery();
            while (results.next()) {
                total = results.getLong(1);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            DataSourceUtil.closed(connection, preparedStatement, results);
        }
        return total;
    }

    @Override
    public List<Object> page(JDBCVo jvo, Page<Object> page, String sql, String prefix,String tableName1) {
        int begin = (page.getPageNo()-1)*page.getPageSize();
        int end = page.getPageNo()*page.getPageSize();
        List<Object> list = new ArrayList<Object>();
        String executeSql = "select * from (select t.*,rownum from ("+sql+") as t where %term) where rownum>? and  rownum<=?";
        StringBuffer termSql = new StringBuffer("1=1");
        if(prefix!=null&&!prefix.equals("")) {
            termSql.append(" and lower(prefixName) = '"+prefix.toLowerCase()+"' ");
        }
        if(tableName1!=null&&tableName1.equals("")) {
            termSql.append(" and lower(tableName) like '%"+tableName1.toLowerCase()+"%' ");
        }
        executeSql = executeSql.replace("%term", termSql);
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet results = null;
        try {
            connection = DataSourceUtil.getConnection(jvo.getDriverName(), jvo.getUrl(), jvo.getUsername(), jvo.getPassword());
            preparedStatement = connection.prepareStatement(executeSql);
            preparedStatement.setInt(1, begin);
            preparedStatement.setInt(1, end);
            results = preparedStatement.executeQuery();
            while (results.next()) {
                String prefixName = results.getString("prefixName");
                String tableName = results.getString("tableName");
                String tableAllName = results.getString("tableAllName");
                String[] str = {prefixName,tableName,tableAllName};
                list.add(str);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            DataSourceUtil.closed(connection, preparedStatement, results);
        }
        return list;
    }
}
