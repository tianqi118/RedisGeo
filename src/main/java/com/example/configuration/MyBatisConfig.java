package com.example.configuration;

import com.example.common.MyBatisInterceptor;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.plugin.Interceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * MyBatis Config
 *
 * @author tianqi
 * @date 2020-4-30
 */
@Configuration
public class MyBatisConfig {
    /**
     * 分页对象实列化
     *
     * @return
     */
    @Bean
    public PageHelper pageHelper() {
        PageHelper pageHelper = new PageHelper();
        Properties p = new Properties();
        p.setProperty("offsetAsPageNum", "true");
        p.setProperty("rowBoundsWithCount", "true");
        p.setProperty("reasonable", "true");
        p.setProperty("dialect", "mysql");
        pageHelper.setProperties(p);
        return pageHelper;
    }

    /**
     * mybatis 自定义拦截器
     */
    @Bean
    public Interceptor getInterceptor() {
        return new MyBatisInterceptor();
    }
}
