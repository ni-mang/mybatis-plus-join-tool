# MPJTool（mybatis-plus-join-tool）

#### 介绍
基于 [mybatis-plus-join](https://mybatisplusjoin.com/)（1.4.8.1），通过对查询接口中的 Query 类（搜索条件）及 Result 类（结果数据）添加相应注解，实现自动组装 MPJLambdaWrapper 对象

- 根据 Query 类的注解，自动拼接 Where 条件，可自动对参数进行判空，支持一个参数对多个字段的查询
- 根据 Result 类的注解，自动拼接 Select 字段、Join 语句、OrderBy 语句，所需皆所查
- 简化 service 的查询接口，对于没有复杂需求的连表查询，可开放一个统一接口，应对不同查询需求
- 支持分段组装，可单独组装 Select、Join、Where 的部分，方便自行扩展条件
- 具体用法请参考样例项目：https://gitee.com/nimang/mpjtool-demo
#### 前置依赖
    MPJTool 1.1.0  --  mybatis-plus-join 1.4.8.1

#### 安装教程
由于目前没有上传到 Maven 公共仓库，因此需要自行打包
1.  下载项目代码到本地
2.  使用 Maven 工具执行 install 操作，将项目进行打包并加入本地 Maven 库
3.  在需要用到 MPJTool 的项目里添加如下依赖：

        <dependency>
            <groupId>org.nimang</groupId>
            <artifactId>mybatis-plus-join-tool</artifactId>
            <version>1.1.0</version>
        </dependency>
4. 也可以直接复制源码到目标项目中

#### 推荐项目
- [PUPA代码生成器](https://gitee.com/nimang/pupa)
