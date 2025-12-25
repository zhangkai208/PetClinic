package com.zk.petclinic.intereptor;



import com.zk.petclinic.domain.SysUser;
import com.zk.petclinic.util.AESUtil;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.Statement;


@Intercepts({
// 拦截setParameters方法（写入时加密）
        @Signature(
                type = ParameterHandler.class,
                method = "setParameters",
                args = {PreparedStatement.class}
        ),
        // 拦截handleResultSets方法（读取时解密）
        @Signature(
                type = ResultSetHandler.class,
                method = "handleResultSets",
                args = {Statement.class}
        )

})
public class PasswordInterceptor implements Interceptor {
    private static final Logger logger = LoggerFactory.getLogger(PasswordInterceptor.class);
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        //获取拦截信息
        Object target = invocation.getTarget();
        if(target instanceof ParameterHandler){
            ParameterHandler parameterHandler = (ParameterHandler) target;
            //获取参数
            Object parameterObject = parameterHandler.getParameterObject();
            if(parameterObject instanceof SysUser){
                SysUser user = (SysUser) parameterObject;
                //加密
                user.setPassword(AESUtil.encrypt(user.getPassword()));
            }
        }else if(target instanceof ResultSetHandler){
            ResultSetHandler resultSetHandler = (ResultSetHandler) target;
            //解密
            Object resultObject = resultSetHandler.handleResultSets((Statement) invocation.getArgs()[0]);
            if(resultObject instanceof SysUser){
                SysUser user = (SysUser) resultObject;
                user.setPassword(AESUtil.decrypt(user.getPassword()));
            }
        }
        Object result = invocation.proceed();//返回结果
        return result;
    }
}
