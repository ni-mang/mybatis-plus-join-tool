# MPJTool（mybatis-plus-join-tool）
### 联系邮箱
　　362682205@qq.com
### 介绍
　　基于 [mybatis-plus-join](https://mybatisplusjoin.com/)（1.4.8.1），通过对查询接口中的 Query 查询参数类（搜索条件）及 Result 结果返回类（结果数据）添加相应注解，实现自动组装 MPJLambdaWrapper 对象

- 根据 Query 类的注解，自动拼接 Where 条件，可自动对参数进行判空，支持一个参数对多个字段的查询
- 根据 Result 类的注解，自动拼接 Select 字段、Join 语句、OrderBy 语句，所查皆所需
- 简化 service 的查询接口，对于没有复杂需求的连表查询，可开放一个统一接口，应对不同查询需求
- 支持分段组装，可单独组装 Select、Join、Where 的部分，方便自行扩展条件
- 具体用法请参考样例项目：https://gitee.com/nimang/mpjtool-demo

### 简单样例
　　以员工表 demo_staff 为主表，先左连接中间表 demo_staff_post，再右连接职位表 demo_post，使用 StaffQuery 携带的参数进行查询，并将结果数据封装为 StaffWithPostVO 返回；

Query 查询参数类
```java
@Data
public class StaffQuery implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String no;

    /** 姓名 */
    @MPWhere(rule = RuleKey.LIKE)
    private String name;

    /** 手机号 */
    @MPWhere(rule = RuleKey.LIKE_RIGHT)
    private String mobile;

    /** 年龄 */
    private Integer age;

    /** 薪资下限 */
    @MPWhere(field = "wages", rule = RuleKey.BETWEEN, priority = PriorityKey.BEFORE)
    private BigDecimal wagesMin;

    /** 薪资上限 */
    @MPWhere(field = "wages", rule = RuleKey.BETWEEN, priority = PriorityKey.AFTER)
    private BigDecimal wagesMax;

    /** 是否在职 */
    private Boolean onJob;

    /** 加入时间起始 */
    @MPWhere(field = "joinTime", rule = RuleKey.GE)
    private LocalDateTime joinTimeBegin;

    /** 加入时间截止 */
    @MPWhere(field = "joinTime", rule = RuleKey.LE)
    private LocalDateTime joinTimeEnd;

}
```
Result 结果返回类
```java
@Data
@MPJoin(leftClass = StaffPost.class, ons = {
        @MPOn(leftField = "staffId", rightField = "id")
})
@MPJoin(leftClass = Post.class, join = JoinKey.RIGHT_JOIN, ons = {
        @MPOn(leftField = "id", rightClass = StaffPost.class, rightField = "postId"),
        @MPOn(leftField = "type", val = "1", rule = RuleKey.NE)
})
public class StaffWithPostVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** ID */
    private Integer id;

    /** 编号 */
    private String no;

    /** 姓名 */
    private String name;

    /** 职位名称 */
    @MPSelect(targetClass = Post.class, field = "name")
    private String postName;

    /** 职位ID */
    @MPSelect(targetClass = Post.class, field = "id")
    private Integer postId;

    /** 职位类型 */
    @MPSelect(targetClass = Post.class, field = "type")
    @MPOrderBy(order = OrderKey.ASC, priority = 2)
    private Integer postType;

    /** 职位类型描述 */
    @MPSelect(targetClass = Post.class, field = "type",
            enums = @MPEnums(enumClass = PostTypeEnums.class, val = "code", msg = "msg"))
    private String postTypeDesc;

    /** 加入时间 */
    @MPOrderBy(order = OrderKey.DESC, priority = 1)
    private LocalDateTime joinTime;
}
```
PostTypeEnums枚举类
```java
public enum PostTypeEnums {
    //管理
    PT1(1, "管理"),
    //技术
    PT2(2, "技术"),
    //协办
    PT3(3, "协办"),
    //普职
    PT4(4, "普职"),
    ;

    private Integer code;
    private String msg;
}
```
Service执行方法
```java
    public List<StaffWithPostVO> querySingle(StaffQuery query) {
        MPJLambdaWrapper<Staff> wrapper = MPJUtil.build(Staff.class, query, StaffWithPostVO.class);
        return baseMapper.selectJoinList(StaffWithPostVO.class, wrapper);
    }
```
执行Sql
```sql
SELECT
	t.`id` AS id,
	t.`no` AS NO,
	t.`name` AS NAME,
	t2.`name` AS postName,
	t2.`id` AS postId,
	t2.`type` AS postType,
	CASE
		t2.`type` 
		WHEN 1 THEN
		'管理' 
		WHEN 2 THEN
		'技术' 
		WHEN 3 THEN
		'协办' 
		WHEN 4 THEN
		'普职' ELSE '' 
	END AS postTypeDesc,
	t.`join_time` AS joinTime 
FROM
	demo_staff t
	LEFT JOIN demo_staff_post t1 ON ( t1.`staff_id` = t.`id` )
	RIGHT JOIN demo_post t2 ON ( t2.`id` = t1.`post_id` AND t2.`type` <> 1 ) 
WHERE
	(
		t.`no` = '100' 
		AND t.`name` LIKE '%陈%' 
		AND t.`mobile` LIKE '13%' 
		AND t.`age` = 25 
		AND t.`wages` BETWEEN '500' AND '10000' 
		AND t.`on_job` = TRUE 
		AND t.`join_time` >= '2023-11-01T00:00' 
		AND t.`join_time` <= '2023-12-30T23:59:59' 
	) 
ORDER BY
	t.`join_time` DESC,
    t2.`type` ASC
```

### 安装教程
　　由于目前没有上传到 Maven 公共仓库，因此需要自行打包
1.  下载项目代码到本地
2.  使用 Maven 工具执行 install 操作，将项目进行打包并加入本地 Maven 库
3.  在需要用到 MPJTool 的项目里添加如下依赖：

```xml
        <dependency>
            <groupId>org.nimang</groupId>
            <artifactId>mybatis-plus-join-tool</artifactId>
            <version>1.2.0</version>
        </dependency>
```
        
4. 也可以直接复制源码到目标项目中

### 使用文档
-  主类：用于本次搜索的wrapper初始化时的泛型类，如 `MPJLambdaWrapper<Staff>` 的主类为 `Staff`
-  调用 MPJUtil 工具类 `build`、`buildSelect`、`buildJoin`、`buildWhere` 等方法组装 `MPJLambdaWrapper`，即可用于查询

#### @MPWhere
<table>
<tr><td rowspan=7>@MPWhere</td><td>作用域：<a>字段</a></td><td colspan= 3>标注 Query 查询参数类中用于搜索条件的字段，如该字段在主类且字段名相同，可省略</td></tr>
<tr><td align="center"><b>属性</b></td><td align="center"><b>类型</b></td><td colspan= 2 align="center"><b>说明</b></td></tr>
<tr><td><a>targetClass</a></td><td>Class</td><td colspan= 2>目标类class：<i>搜索字段所在的类，不设置则默认为主类</i></td></tr>
<tr><td><a>alias</a></td><td>String</td><td colspan= 2>别名：<i>不设置则按默认别名，主表为“t”，连接表分别按“t1,t2,t3...”顺序命名</i></td></tr>
<tr><td><a>field</a></td><td>String</td><td colspan= 2>字段名：<i>搜索字段名，不设置则同当前字段名</i></td></tr>
<tr><td><a>rule</a></td><td>RuleKey</td><td colspan= 2>搜索规则：<i>默认为 RuleKey.EQ</i></td></tr>
<tr><td><a>priority</a></td><td>PriorityKey</td><td colspan= 2>优先级：<i>当 rule 为 BETWEEN 或 NOT_BETWEEN 时，需指定优先级，默认为 PriorityKey.BEFORE</i></td></tr>
<tr><td colspan= 5>

```java
/**
 * 电话
 * 未添加注解，默认以当前字段名在主类中使用eq规则进行搜索
 * sql样例: AND t.`phone` = '10086' 
 */
private String phone;

/**
 * 姓名
 * 如当前字段不为空，则对主表中的name使用like规则进行搜索
 * sql样例: AND t.`name` LIKE '%张%' 
 */
@MPWhere(rule = RuleKey.LIKE)
private String name;

/**
 * 机构名称
 * 如当前字段不为空，则对Org表中的name使用like规则进行搜索
 * sql样例: AND org.`name` LIKE '技术%' 
 */
@MPWhere(targetClass = Org.class, field = "name",rule = RuleKey.LIKE_RIGHT)
private String orgName;
```
</td></tr>
</table>

#### @MPWheres
<table>
<tr><td rowspan=4>@MPWheres</td><td>作用域：<a>字段</a></td><td colspan= 3>当需要使用同一个参数对不同字段进行搜索时（如使用 loginNmae 搜索匹配 userName 或 mobileNo），用于配置多个条件</td></tr>
<tr><td align="center"><b>属性</b></td><td align="center"><b>类型</b></td><td colspan= 2 align="center"><b>说明</b></td></tr>
<tr><td><a>wheres</a></td><td>MPWhere[ ]</td><td colspan= 2>查询规则组：<i>配置的 @MPWhere 组合</i></td></tr>
<tr><td><a>logic</a></td><td>LogicKey</td><td colspan= 2>逻辑规则：<i>多个条件之间的逻辑关系，默认为LogicKey.OR</i></td></tr>
<tr><td colspan= 5>

```java
/**
 * 登录名，no或mobile
 * 如当前字段不为空，则对主表中的no或mobile使用like规则进行搜索
 * sql样例: AND ((t.`no` LIKE '%16%' ) OR ( t.`mobile` LIKE '%16%' ))  
 */
@MPWheres(wheres = {
        @MPWhere(field = "no", rule = RuleKey.LIKE),
        @MPWhere(field = "mobile", rule = RuleKey.LIKE)
}, logic = LogicKey.OR)
private String loginName;
```
</td></tr>
</table>

#### @MPJoin
<table>
<tr><td rowspan=6>@MPJoin</td><td>作用域：<a>类</a></td><td colspan= 3>在 Result 结果返回类中标注连接规则</td></tr>
<tr><td align="center"><b>属性</b></td><td align="center"><b>类型</b></td><td colspan= 2 align="center"><b>说明</b></td></tr>
<tr><td><a>leftClass</a></td><td>Class</td><td colspan= 2>左表类：<i>进行 join 操作的类，不设置则默认为主类</i></td></tr>
<tr><td><a>leftAlias</a></td><td>String</td><td colspan= 2>左表类别名：<i>如存在重复连接同一张表的情况，为避免取值错误，强烈建议设置别名，不设置则分别按“t1,t2,t3...”顺序命名“</i></td></tr>
<tr><td><a>join</a></td><td>JoinKey</td><td colspan= 2>连接规则：<i>不设置则默认为 JoinKey.LEFT_JOIN</i></td></tr>
<tr><td><a>ons</a></td><td>MPOn[ ]</td><td colspan= 2>on规则组：<i>必须设置，指定连接时的 ON 条件</i></td></tr>
<tr><td colspan= 5>

```java
/**
 * 使用 LEFT JOIN 规则，左表类为 Staff.class，别名为 leaderStaff，右表类为 Staff.class，ON 条件为 id = leaderId
 * 使用 LEFT JOIN 规则，左表类为 StaffPost.class，右表类为 Staff.class，右表别名(主表默认别名)为 t， ON 条件为 id = staffId
 * sql样例：
 * LEFT JOIN demo_staff leaderStaff 
 *  ON ( leaderStaff.`id` = t.`leader_id` )
 */
@MPJoin(leftClass = Staff.class, leftAlias = "leaderStaff", join = JoinKey.LEFT_JOIN, ons = {
        @MPOn(leftField = "id", rightClass = Staff.class, rightField = "leaderId")
})
@MPJoin(leftClass = StaffPost.class, join = JoinKey.LEFT_JOIN, ons = {
        @MPOn(leftField = "staffId", rightClass = Staff.class, leftAlias = "t", rightField = "id")
})
public class StaffWithLeaderVO implements Serializable {
    ......
}

/**
 * 以上样例中，主类为 Staff.class，规则为 LEFT JOIN，可简化为以下形式
 */
@MPJoin(leftAlias = "leaderStaff", ons = {
        @MPOn(leftField = "id", rightField = "leaderId")
})
@MPJoin(leftClass = StaffPost.class, ons = {
        @MPOn(leftField = "staffId", rightField = "id")
})
public class StaffWithLeaderVO implements Serializable {
    ......
}
```
</td></tr>
</table>

#### @MPOn
<table>
<tr><td rowspan=8>@MPOn</td><td>作用域：<a>类</a></td><td colspan= 3>ON规则，依赖于@MPJoin，可指定左表字段与右表字段的连接关系，也可直接指定左表字段与具体数值的关系</td></tr>
<tr><td align="center"><b>属性</b></td><td align="center"><b>类型</b></td><td colspan= 2 align="center"><b>说明</b></td></tr>
<tr><td><a>leftField</a></td><td>String</td><td colspan= 2>左表连接字段名：<i>必须设置</i></td></tr>
<tr><td><a>rightClass</a></td><td>Class</td><td colspan= 2>右表类：<i>不设置则默认为主类</i></td></tr>
<tr><td><a>rightAlias</a></td><td>String</td><td colspan= 2>右表类别名：<i>如右表此前有设置别名，此项必须设置，否则可不设置</i></td></tr>
<tr><td><a>rightField</a></td><td>String</td><td colspan= 2>右表连接字段名：<i>指定左表字段与右表字段的连接关系时必须设置</i></td></tr>
<tr><td><a>val</a></td><td>String[ ]</td><td colspan= 2>值：<i>直接指定左表字段与具体数值的关系
<br>1. 以字符串形式传入具体值，如 "陈"，"10","3.14"，"2023-12-22 10:29:34" 等，MPJTool 将自动转型
<br>2. 设置有效值后，rightClass、rightAlias、rightField 将失效
<br>3. 当 rule 为 IS_NULL、IS_NOT_NULL 时，此项不必设置
<br>4. 当 rule 为 IN、NOT_IN 时，此项必须设置，以数组形式传入多个值 
<br>5. 当 rule 为 BETWEEN、NOT_BETWEEN时，此项必须设置，至少传入两个值，且只使用到前两个值 
<br>6. 当 rule 为 非上述规则的其它规则时，此项必须设置，只使用到第一个值 </i>
</td></tr>
<tr><td><a>rule</a></td><td>RuleKey</td><td colspan= 2>规则：<i>默认为 RuleKey.EQ</i></td></tr>
<tr><td colspan= 5>

```java
/**
 * sql样例：
 * LEFT JOIN demo_staff leader ON (
 *  leader.`id` = staff.`leader_id` 
 *  AND leader.`age` IN (20,21,25,26,30) 
 *  AND leader.`wages` BETWEEN '2000' 
 *  AND '10000.01' AND leader.`leader_id` IS NOT NULL 
 *  AND leader.`no` LIKE '10%' 
 *  AND leader.`join_time` >= '2023-11-01T00:00'
 * )
 
 */
@MPJoin(leftClass= Staff.class, leftAlias = "leader", join = JoinKey.LEFT_JOIN, ons = {// 自连接查询当前员工的领导信息
        @MPOn(leftField = "id", rightClass = Staff.class, rightAlias = "staff", rightField = "leaderId"),
        @MPOn(leftField = "age", rule = RuleKey.IN, val = {"20","21","25","26","30"}),
        @MPOn(leftField = "wages", rule = RuleKey.BETWEEN, val = {"2000","10000.01"}),
        @MPOn(leftField = "leaderId", rule = RuleKey.IS_NOT_NULL),
        @MPOn(leftField = "no", rule = RuleKey.LIKE_RIGHT, val = "10"),
        @MPOn(leftField = "joinTime", rule = RuleKey.GE, val = "2023-11-01 00:00:00")
})
```
</td></tr>
</table>

#### @MPSelect
<table>
<tr><td rowspan=7>@MPSelect</td><td>作用域：<a>字段</a></td><td colspan= 3>标注 Result 结果返回类中用于 select 的字段，如该字段在主类且字段名相同，可省略</td></tr>
<tr><td align="center"><b>属性</b></td><td align="center"><b>类型</b></td><td colspan= 2 align="center"><b>说明</b></td></tr>
<tr><td><a>targetClass</a></td><td>Class</td><td colspan= 2>目标类class：<i>返回字段所在的类，不设置则默认为主类</i></td></tr>
<tr><td><a>alias</a></td><td>String</td><td colspan= 2>别名：<i>不设置则按默认别名，主表为“t”，连接表分别按“t1,t2,t3...”顺序命名</i></td></tr>
<tr><td><a>field</a></td><td>String</td><td colspan= 2>字段名：<i>返回字段名，不设置则同当前字段名</i></td></tr>
<tr><td><a>enums</a></td><td>MPEnums</td><td colspan= 2>枚举注解：<i>设置当前字段与指定枚举类的数据转换</i></td></tr>
<tr><td colspan= 5>

```java
/**
 * 姓名
 * 未添加注解，默认以当前字段进行查询并返回
 * sql样例: SELECT t.`user_name` AS userName
 */
private String userName;

/**
 * 机构名称
 * sql样例: SELECT org.`name` AS orgName
 */
@MPSelect(targetClass = Org.class, alias = "org", field = "name")
private String orgName;

```
</td></tr>
</table>

#### @MPOrderBy
<table>
<tr><td rowspan=4>@MPOrderBy</td><td>作用域：<a>字段</a></td><td colspan= 3>排序，设置当前字段排序规则</td></tr>
<tr><td align="center"><b>属性</b></td><td align="center"><b>类型</b></td><td colspan= 2 align="center"><b>说明</b></td></tr>
<tr><td><a>order</a></td><td>OrderKey</td><td colspan= 2>排列规则：<i>默认正序 OrderKey.ASC</i></td></tr>
<tr><td><a>priority</a></td><td>int</td><td colspan= 2>排列条件优先级：<i>默认为0，数值越低，优先级越高</i></td></tr>
<tr><td colspan= 5>

```java
/**
 * 职位类型
 * sql样例: SELECT t1.`type` AS postType ... ORDER BY t1.`type` DESC,
 */
@MPSelect(targetClass = Post.class, field = "type")
@MPOrderBy(order = OrderKey.DESC, priority = 1)
private Integer postType;

```
</td></tr>
</table>

#### @MPEnums
<table>
<tr><td rowspan=5>@MPEnums</td><td>作用域：<a>字段</a></td><td colspan= 3>枚举值转换，设置当前字段与指定枚举类的数据转换</td></tr>
<tr><td align="center"><b>属性</b></td><td align="center"><b>类型</b></td><td colspan= 2 align="center"><b>说明</b></td></tr>
<tr><td><a>enumClass</a></td><td>Class</td><td colspan= 2>枚举类：<i>必须，枚举类型</i></td></tr>
<tr><td><a>val</a></td><td>String</td><td colspan= 2>值属性名：<i>枚举类中表示值的字段名，默认“code”</i></td></tr>
<tr><td><a>msg</a></td><td>String</td><td colspan= 2>描述属性名：<i>枚举类中表示注释描述的字段名，默认“msg”</i></td></tr>
<tr><td colspan= 5>

```java
/**
 * 职位类型
 * sql样例: 
 * SELECT 
 *  CASE t1.`type` 
 *    WHEN 1 THEN '管理' 
 *    WHEN 2 THEN '技术' 
 *    WHEN 3 THEN '协办' 
 *    WHEN 4 THEN '普职' 
 *    ELSE '' 
 *  END AS postTypeDesc,
 *  ...
 */
@MPSelect(targetClass = Post.class, field = "type")
@MPEnums(enumClass = PostTypeEnums.class, val = "code", msg = "msg")
private String postTypeDesc;

```
</td></tr>
</table>

#### @MPIgnore
<table>
<tr><td >@MPIgnore</td><td>作用域：<a>字段</a></td><td colspan= 3>忽略标注，被标注的非静态字段将不参与sql的构造，如类中含有的属性不存在于数据库表，务必使用 @MPIgnore 进行标注，否则执行生成的sql时将导致异常</td></tr>
<tr><td colspan= 5>

```java
/**
 * 页码
 */
@MPIgnore
private Integer pageNum;

/**
 * 单页数据量
 */
@MPIgnore
private Integer pageSize;

```
</td></tr>
</table>

### 须知
　　本工具仅支持常用的 where 语句（如：eq、like、ge...）及 join、orderBy，不支持 full join、union、group、func 等操作，如需执行此类复杂查询，可使用分段组装后，使用 mybatis-plus-join 拼接相应语句

### 推荐项目
- [PUPA代码生成器](https://gitee.com/nimang/pupa)
