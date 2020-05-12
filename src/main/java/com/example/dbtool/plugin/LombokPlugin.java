package com.example.dbtool.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * LombokPlugin
 *
 * @author tianqi
 * @date 2020-4-28
 */
public class LombokPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        //添加domain的import
        topLevelClass.addImportedType("lombok.Data");
        topLevelClass.addImportedType("lombok.EqualsAndHashCode");
        topLevelClass.addImportedType("lombok.experimental.Accessors");
//        topLevelClass.addImportedType("lombok.AllArgsConstructor");


        //添加domain的注解
        topLevelClass.addAnnotation("@Data");
        topLevelClass.addAnnotation("@EqualsAndHashCode(callSuper = true)");
        topLevelClass.addAnnotation("@Accessors(chain = true)");

        //添加domain的注释
//        topLevelClass.addJavaDocLine("/**");
//        topLevelClass.addJavaDocLine("* Created by Mybatis Generator on " + date2Str(new Date()));
//        topLevelClass.addJavaDocLine("*/");

        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        //添加Mapper的import
//        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Mapper"));
        //添加Mapper的注解
//        interfaze.addAnnotation("@Mapper");

        //Mapper文件的注释
        interfaze.addJavaDocLine("/**");
        interfaze.addJavaDocLine(" * Created by Mybatis Generator");
        interfaze.addJavaDocLine(" *");
        interfaze.addJavaDocLine(" * @author " + "Mybatis Generator");
        interfaze.addJavaDocLine(" * @date " + date2Str(new Date()));
        interfaze.addJavaDocLine(" */");

        return true;
    }

    @Override
    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        //不生成getter
        return false;
    }

    @Override
    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        //不生成setter
        return false;
    }

    private String date2Str(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
}