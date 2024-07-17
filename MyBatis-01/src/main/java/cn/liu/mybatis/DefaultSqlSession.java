package cn.liu.mybatis;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.Date;
import java.util.*;

public class DefaultSqlSession implements SqlSession {

    private Connection connection;
    public Map<String, XNode> mapperElement;

    public DefaultSqlSession(Connection connection, Map<String, XNode> mapperElement) {
        this.connection = connection;
        this.mapperElement = mapperElement;
    }

    @Override
    public <T> T selectOne(String statement) {
        try {
            XNode xNode = mapperElement.get(statement);
            PreparedStatement preparedStatement = connection.prepareStatement(xNode.getSql());
            ResultSet resultSet = preparedStatement.executeQuery();
            List<T> objects = resultSet2Obj(resultSet, Class.forName(xNode.getResultType()));
            return objects.get(0);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        XNode xNode = mapperElement.get(statement);
        Map<Integer, String> parameterMap = xNode.getParameter();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(xNode.getSql());
            buildParameter(preparedStatement, parameter, parameterMap);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<T> objects = resultSet2Obj(resultSet, Class.forName(xNode.getResultType()));
            return objects.get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> List<T> selectList(String statement) {
        XNode xNode = mapperElement.get(statement);
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(xNode.getSql());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet2Obj(resultSet, Class.forName(xNode.getResultType()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> List<T> selectList(String statement, Object parameter) {
        XNode xNode = mapperElement.get(statement);
        Map<Integer, String> parameterMap = xNode.getParameter();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(xNode.getSql());
            buildParameter(preparedStatement, parameter, parameterMap);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet2Obj(resultSet, Class.forName(xNode.getResultType()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void close() {
        if (null == connection) return;
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //根据参数的类型的预定义的参数映射，设置PreparedStatement的参数值
    private void buildParameter(PreparedStatement preparedStatement, Object parameter, Map<Integer, String> parameterMap) throws SQLException, IllegalAccessException {
        int size = parameterMap.size();//获取参数的数量

        //如果参数是个值
        if (parameter instanceof Long) {
            for (int i = 1; i <= size; i++) {
                preparedStatement.setLong(i, Long.parseLong(parameter.toString()));
            }
            return;
        }

        if (parameter instanceof Integer) {
            for (int i = 1; i <= size; i++) {
                preparedStatement.setInt(i, Integer.parseInt(parameter.toString()));
            }
            return;
        }

        if (parameter instanceof String) {
            for (int i = 1; i <= size; i++) {
                preparedStatement.setString(i, parameter.toString());
            }
            return;
        }

        //如果参数是对象
        HashMap<String, Object> fieldMap = new HashMap<>();
        Field[] declaredFields = parameter.getClass().getDeclaredFields();//获取对象的所有属性
        for (Field field : declaredFields) {
            String fieldName = field.getName();
            field.setAccessible(true);
            Object obj = field.get(parameter);//获取属性值
            field.setAccessible(false);
            fieldMap.put(fieldName, obj);//将属性名和值存入fieldMap
        }

        for (int i = 1; i <= size; i++) {
            String parameterDefine = parameterMap.get(i);//获取预定义的参数名称
            Object obj = fieldMap.get(parameterDefine);//根据参数名称获取对应的属性值

            // 根据属性类型设置 PreparedStatement 的参数值
            if (obj instanceof Short) {
                preparedStatement.setShort(i, Short.parseShort(obj.toString()));
            } else if (obj instanceof Integer) {
                preparedStatement.setInt(i, Integer.parseInt(obj.toString()));
            } else if (obj instanceof Long) {
                preparedStatement.setLong(i, Long.parseLong(obj.toString()));
            } else if (obj instanceof String) {
                preparedStatement.setString(i, obj.toString());
            } else if (obj instanceof Date) {
                preparedStatement.setDate(i, (java.sql.Date) obj);
            }
        }
    }

    //将ResultSet转换为Java的对象列表中
    private <T> List<T> resultSet2Obj(ResultSet resultSet, Class<?> clazz) {
        List<T> result = new ArrayList<>();
        try {
            //获取ResultSet的元数据，包括列的数量等信息
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            //遍历ResultSet的每一行数据
            while (resultSet.next()) {
                T obj = (T) clazz.newInstance();
                //遍历每一列，获取列名和值，并设置到对象的属性中去
                for (int i = 1; i <= columnCount; i++) {
                    Object value = resultSet.getObject(i);
                    String columnName = metaData.getColumnName(i);
                    //根据列名构造对应的setter方法名称
                    String setterMethod = "set" + columnName.substring(0, 1).toUpperCase() + columnName.substring(1);

                    //根据属性类型选择合适的setter方法
                    Method setter = clazz.getMethod(setterMethod, value.getClass());
                    if (value instanceof Timestamp) {
                        setter.invoke(obj, new Date(((Timestamp) value).getTime()));
                    } else {
                        setter.invoke(obj, value);
                    }
                }

                result.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
