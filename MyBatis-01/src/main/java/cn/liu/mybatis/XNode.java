package cn.liu.mybatis;

import java.util.Map;

public class XNode {
    //命名空间，用于表示XML配置文件中的一个XML节点的属性
    private String namespace;
    //标识一个SQL语句或者一个SQL映射
    private String id;
    //表示输入参数的类型，通常是一个Java类的全限定名
    private String parameterType;
    //表示输出结果的类型，通常是一个Java类的全限名
    private String resultType;
    //表示一个SQL语句
    private String sql;
    //标识SQL语句中的参数，Integer表示参数的位置，String表示参数的名称
    private Map<Integer, String> parameter;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParameterType() {
        return parameterType;
    }

    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {this.resultType = resultType;}

    public String getSql() {return sql;}

    public void setSql(String sql) {this.sql = sql;}

    public Map<Integer, String> getParameter() {return parameter;}

    public void setParameter(Map<Integer, String> parameter) {this.parameter = parameter;}
}
