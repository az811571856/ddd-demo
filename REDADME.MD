>参考
[殷浩详解DDD系列 第一讲 - Domain Primitive](https://developer.aliyun.com/article/715802)

# DDD
DDD不是一套框架，而是一种架构思想。
## 思想
思想一：引入Anti-Corruption Layer（防腐层）以实现核心业务逻辑和外部依赖隔离的机制。
思想二：Bounded Context（限界上下文），明确哪些东西可以被服务化拆分，哪些逻辑需要聚合，以带来最小的维护成本

DDD的目标是提升代码质量、可测试性、安全性、健壮性。

## DDD的构成
### Domain Primitive (DP)
Primitive的定义是：

>不从任何其他事物发展而来
>
>初级的形成或生长的早期阶段

DP由Type和Class构成

比如，User就是一个DP

结论：Domain Primitive是一个在特定领域里，拥有精准定义的、可自我验证的、拥有行为的Value Object。

* DP是一个传统意义上的Value Object，拥有Immutable的特性
* DP是一个完整的概念整体，拥有精准定义
* DP使用业务域中的原生语言
* DP可以是业务域的最小组成部分、也可以构建复杂组合

### Type（数据类型）
比如使用class PhoneNumber去显性的标识电话号这个概念

### Value Object (VO)
VO都是Immutable的。

### Domain Primitive和DDD里Value Object的区别
Value Object更多的是一个非Entity的值对象。
Domain Primitive是Value Object的进阶版，在原始VO的基础上要求每个DP拥有概念的整体，
Domain Primitive在VO的Immutable基础上增加了Validity和行为。当然同样的要求无副作用（side-effect free）。
VO可能也有行为，表达领域粒度相对Domain Primitive较小。

### Data Transfer Object (DTO)
用于数据传输，属于技术细节的东西，不是业务架构的东西。
只是一堆数据放在一起，不一定有关联度。
没有行为。

## 未来会覆盖的内容包括
* 最佳架构实践：六边形应用架构 / Clean架构的核心思想和落地方案
* 持续发现和交付：Event Storming > Context Map > Design Heuristics > Modelling
* 降低架构腐败速度：通过Anti-Corruption Layer集成第三方库的模块化方案
* 标准组件的规范和边界：Entity, Aggregate, Repository, Domain Service, Application Service, Event, DTO Assembler等
* 基于Use Case重定义应用服务的边界
* 基于DDD的微服务化改造及颗粒度控制
* CQRS架构的改造和挑战
* 基于事件驱动的架构的挑战
* 等等

---------------

# 原则
## DP的原则 Make Implicit Concepts Expecit
将 隐性的概念 显性化

## DP的原则 Make Implicit Context Expecit
将 隐性的 上下文 显性化
比如将默认货币这个隐性的上下文概念显性化，并且和金额合并为Money。支付金额 + 支付货币。我们可以把这两个概念组合成为一个独立的完整概念：Money

## DP的原则 Encapsulate Multi-Object Behavior
封装 多对象 行为。
涉及到多个对象的业务逻辑，需要用DP包装掉。
方法可以简单概况为，包裹对象，提取对象的业务逻辑放到DP中，有些对象需要实例化进来，有些对象是行为传递进来，业务上要注意区分。
比如：汇率转换，包裹两个货币类型，汇率。提供对Money对象兑换的行为，但是并没有包裹钱这个对象，使用行为的方式传递进来的。

## 一般来说VO都是Immutable的
比如 private final String number确保PhoneNumber是一个（Immutable）Value Object。

## DP的DRY不重复原则和单一性原则
比如 修改PhoneNumber的校验逻辑，只需要在一个文件里修改即可。
将电话号的概念显性化，内部解决了校验问题，把电话号码对象化可以解决多处代码重复问题，校验使用对象来做比静态方法更能体现一些都是对象，更优雅。

# 使用场景
## 什么情况下应该用Domain Primitive
* 有格式限制的String：比如Name，PhoneNumber，OrderNumber，ZipCode，Address等
* 有限制的Integer：比如OrderId（>0），Percentage（0-100%），Quantity（>=0）等
* 可枚举的int：比如Status（一般不用Enum因为反序列化问题）
* Double或BigDecimal：一般用到的Double或BigDecimal都是有业务含义的，比如Temperature、Money、Amount、ExchangeRate、Rating等
* 复杂的数据结构：比如Map<String, List<Integer>>等，尽量能把Map的所有操作包装掉，仅暴露必要行为

## 重构流程
* 第一步 - 创建Domain Primitive，并收集所有DP行为
所有抽离出来的方法要做到无状态，因为DP是没有中间状态的，换句话说初始值是不会变化的。
* 第二步 - 替换数据校验和无状态逻辑
DP本身带有校验和行为逻辑，可以替换进入已有代码。
这一步可以不修改已有逻辑的方法签名，属于方法内的替换。
* 第三步 - 创建新接口
讲第二步的方法提升到接口层。方法签名会变换。原有代码根据是否有用可以保留也可以删除。
* 第四步 - 修改外部调用

---------------
>参考
[殷浩详解DDD系列 第二讲 - 应用架构](https://developer.aliyun.com/article/715802)
# 应用架构
应用架构，意指软件系统中固定不变的代码结构、设计模式、规范和组件间的通信方式。
固定的架构设计，可以让团队不同开发都能有一个统一的开发规范，降低沟通成本，提升效率和代码质量。

好的架构应该需要实现以下几个目标：
独立于技术框架，独立于UI展示，独立于底层数据源，独立于外部依赖，  
最后一条可测试：无论外部依赖了什么数据库、硬件、UI或者服务，业务的逻辑应该都能够快速被验证正确性。

目前业务研发常见的误区是，更多的会去关注一些宏观的技术物理架构，比如SOA架构、微服务架构，而忽略了应用内部的架构设计，很容易导致代码逻辑混乱，造成代码很难维护。

## Transaction Script（事务脚本）代码的问题
可维护性差、可扩展性差、可测试性差。
1、可维护性差
数据结构（对象）DO的不稳定性，依赖库的升级，第三方服务依赖的不确定性，第三方服务API的接口变化，中间件更换。
2、可扩展性差
新增修改一个功能代码改动的比例来衡量扩展性。
数据来源被固定、数据格式不兼容，业务逻辑无法复用，逻辑和数据存储的相互依赖。
事务脚本式的架构下，一般做第一个需求都非常的快，但是做第N个需求时需要的时间很有可能是呈指数级上升的。
3、可测试性差
运行测试用例所花费的时间 * 要测试用例数。
脚本代码可测试差的表现：
设施搭建困难，运行耗时长，耦合度高。
测试用例复杂度远大于真实代码复杂度。

## 代码违反了什么原则
* 单一性原则（Single Responsibility Principle）

* 依赖反转原则（Dependency Inversion Principle）
没有依赖抽象，而是依赖了实现
* 开放封闭原则（Open Closed Principle）
计算逻辑没有封装

## 基本概念
### 实体（Entity）
是基于领域逻辑的实体类。
一个实体（Entity）是拥有ID的域对象，除了拥有数据之外，同时拥有行为。  
Entity和数据库储存格式无关，在设计中要以该领域的通用严谨语言（Ubiquitous Language）为依据。
Entity字段也不仅仅是String等基础类型，而应该尽可能用Domain Primitive代替，可以避免大量的校验代码。

### 数据类DO (Data Object)
DO是单纯的和数据库表的映射关系。
DO只有数据，没有行为。
DO的作用是对数据库做快速映射，避免直接在代码里写SQL。
业务代码避免直接操作DO

### DAO
DAO对应的是一个特定的数据库类型的操作。
所有操作的对象都是DO类。

### Repository
Entity对象读取储存的抽象，在接口层面做统一，不关注底层实现。
通过 save 保存一个Entity对象，但至于具体是 insert 还是 update 并不关心。
Repository的具体实现类通过调用DAO来实现各种操作。  
Repository通过Builder/Factory对象实现AccountDO 到 Account之间的转化

### Repository和Entity
* 通过Account对象，避免了其他业务逻辑代码和数据库的直接耦合，避免了当数据库字段变化时，大量业务逻辑也跟着变的问题。
* 通过Repository，改变业务代码的思维方式，让业务逻辑不再面向数据库编程，而是面向领域模型编程。
* Account属于一个完整的内存中对象，可以比较容易的做完整的测试覆盖，包含其行为。
* Repository作为一个接口类，可以比较容易的实现Mock或Stub，可以很容易测试。
* AccountRepositoryImpl实现类，由于其职责被单一出来，只需要关注Account到AccountDO的映射关系和Repository方法到DAO方法之间的映射关系，相对于来说更容易测试。

### 事务脚本代码与胶水代码
事务脚本代码是步骤型的，ABC步骤，关注点在步骤上面。
胶水代码是把上个接口的返回转换成下个接口的入参，关注点在两个服务间数据格式的转化。

### 防腐层（ACL）
Anti-Corruption Layer（防腐层或ACL)。
很多时候我们的系统会去依赖其他的系统，而被依赖的系统可能包含不合理的数据结构、API、协议或技术实现，如果对外部系统强依赖，会导致我们的系统被”腐蚀“。
防腐层的作用是，隔离外部依赖和内部逻辑，减少外部依赖变化对内部逻辑的影响。
ACL能够提供更多强大的功能：
适配器：数据转换。
缓存：降低对外部服务压力，把缓存放入防腐层，减少核心代码。
兜底：外部服务不可用的兜底逻辑。  
易于测试和  功能开关。

### Domain Primitive 和 Entity
前者无状态，只有初始化会引起变量改变，行为不改变局部变量（状态）。
后者有状态，改变行为时可能引起局部变量（状态）的变化。

### Application Service（应用服务）
一眼望去，仅仅包含组件编排的代码。没有任何逻辑判断和计算。
应用服务仅仅依赖了一些抽象出来的ACL类和Repository类。
Application Service、Repository、ACL等我们统称为Application Layer（应用层）。

## 重构脚本代码
1、抽象数据存储层
对Data Access层做抽象，降低系统对数据库的直接依赖。
新建实体对象，新建Entity对象储存接口类XXRepository。

2、抽象第三方服务
除了提出第三方服务行为外。
第三方服务的涉及的领域也有必要通过Domain Primitive类进行封装。

3、抽象中间件
比如消息服务的中间件接口通常是String或Byte[] 类型，导致序列化逻辑与业务逻辑混杂在一起。
抽象接口和DP。

4、封装业务逻辑
通过Entity、Domain Primitive和Domain Service封装所有的业务逻辑。
* Domain Primitive封装跟实体无关的无状态计算逻辑
* 用Entity封装单对象的有状态的行为，包括业务校验
* 用Domain Service封装多对象逻辑
跨Entity

## 重构后代码特征
* 业务逻辑清晰，数据存储和业务逻辑完全分隔。
* Entity、Domain Primitive、Domain Service都是独立的对象，没有任何外部依赖，但是却包含了所有核心业务逻辑，可以单独完整测试。
* 原有的服务不再包括任何计算逻辑，仅仅作为组件编排，所有逻辑均delegate到其他组件。这种仅包含Orchestration（编排）的服务叫做Application Service（应用服务）。

### 重构后的依赖关系
Domain Layer（领域层）变成核心：Entity、Domain Primitive和Domain Service，这些对象不依赖任何外部服务和框架，而是纯内存中的数据和操作。
再往外层是负责组件编排的Application Service：Application Service、Repository、ACL等我们统称为Application Layer（应用层）
最外层是具体实现层（框架，所以统称为Infrastructure Layer（基础设施层））：ACL，Repository等的具体实现，包括Web框架里的对象如Controller之类。

