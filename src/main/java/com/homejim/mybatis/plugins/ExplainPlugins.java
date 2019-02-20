package com.homejim.mybatis.plugins;

import java.sql.Connection;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.util.Properties;
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class,Integer.class })})
public class ExplainPlugins implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        System.out.println("-----------------------------------------------------我是插件-----------------------------------");

        StatementHandler stmtHandler = (StatementHandler) invocation.getTarget();

//        BoundSql boundSql = stmtHandler.getBoundSql();
//        String sqlt = boundSql.getSql();
//        System.out.println("----------------------------------------ExplainPlugins.intercept sql=" + sqlt);
        MetaObject metaStmtHandler = SystemMetaObject.forObject(stmtHandler);
        while (metaStmtHandler.hasGetter("h")) {
            Object obejct = metaStmtHandler.getValue("h");
            metaStmtHandler = SystemMetaObject.forObject(obejct);
        }
        // 分离最后一个代理对象的目标类
        while (metaStmtHandler.hasGetter("target")) {
            Object object = metaStmtHandler.getValue("target");
            metaStmtHandler =SystemMetaObject.forObject(object);
        }
        // 取出即将执行的SQL
        String sql = (String) metaStmtHandler.getValue("delegate.boundSql.sql");
        sql = sql.trim();
        // 将参数写入SQL
        String newSQL = "explain " + sql;
        metaStmtHandler.setValue("delegate.boundSql.sql", newSQL);
        System.out.println("newSQL="+newSQL);
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object o) {
        System.out.println("------------------------------生产代理对象------------------------");
        return Plugin.wrap(o, this);
    }

    @Override
    public void setProperties(Properties properties) {
        System.out.println("------------------------------设置属性------------------------");
    }
}
