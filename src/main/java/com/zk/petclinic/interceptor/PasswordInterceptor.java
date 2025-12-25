package com.zk.petclinic.interceptor;



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
import java.util.List;


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
        Object target = invocation.getTarget();

        // 处理写入时的密码加密
        if (target instanceof ParameterHandler) {
            ParameterHandler parameterHandler = (ParameterHandler) target;
            Object parameterObject = parameterHandler.getParameterObject();
            if (parameterObject instanceof SysUser) {
                SysUser user = (SysUser) parameterObject;
                // 只有密码不为空时才加密
                if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                    user.setPassword(AESUtil.encrypt(user.getPassword()));
                    logger.debug("密码加密成功，用户: {}", user.getUsername());
                }
            }
            // 加密完成后执行原方法
            return invocation.proceed();
        }

        // 处理读取时的密码解密
        if (target instanceof ResultSetHandler) {
            // 先执行原方法获取结果（只调用一次！）
            Object result = invocation.proceed();

            // 处理结果集中的密码解密
            // MyBatis的handleResultSets返回的是List，即使只有一条记录
            if (result instanceof List) {
                List<?> resultList = (List<?>) result;
                for (Object obj : resultList) {
                    if (obj instanceof SysUser) {
                        SysUser user = (SysUser) obj;
                        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                            try {
                                user.setPassword(AESUtil.decrypt(user.getPassword()));
                                logger.debug("密码解密成功，用户: {}", user.getUsername());
                            } catch (Exception e) {
                                // 捕获解密失败，可能是未加密的旧数据
                                logger.warn("密码解密失败，用户: {}，可能是未加密的旧数据", user.getUsername());
                            }
                        }
                    }
                }
            }
            return result;
        }

        // 其他情况直接执行
        return invocation.proceed();
    }
}
