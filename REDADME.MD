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

### Domain Service
面向领域的业务逻辑。
可以依赖抽象出来其它服务，存储服务。

### Domain Service 和 Application Service（应用服务）
后者调用前者来完成服务的编排。
前者行为定义和后者编排之间的边界是我们要思考的。

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

## DDD的六边形架构
### 代码组织结构
在Java中我们通过POM Module和POM依赖处理层与层之间的关系。
### Types模块
Types是保存Domain Primitives的地方。  
因为Domain Primitives本身是无状态的，所以可以对外暴露。  
单独成为模块，被包含在对外的API接口中。  
Types模块不依赖任何类库，纯POJO。

举例：
封装数字的账户编号AccountNumber，包含货币单位和数量的Money，包含源和目标货币源货币数量的汇率ExchangeRate。
以上都是无状态的，初始化后不需要再变更。
但账户就不属于以上这种，账户里有余额，初始化会再次变更。
###  Domain模块
Domain包含Entity、领域服务Domain Service（和其实现）、以及各种外部依赖的接口类（如Repository、ACL、中间件等)。  
模块内部的子包有：domain(子包有entity,service,service/impl,types),external(里面放的interface),repository(里面放的interface)  
该模块是核心业务逻辑的集中地。
Domain模块仅依赖Types模块，也是纯POJO。
注意一点，Domain Service包括跨Entity的操作，也包含对其它接口类的调用。

### Application模块
Application Service的包有service和service/impl

### Infrastructure模块
Infrastructure模块包含了Persistence、Messaging、External等模块。
Persistence模块包含数据库DAO的实现，包含Data Object、ORM Mapper、Entity到DO的转化类等。
Persistence模块要依赖具体的ORM类库，比如MyBatis。
Persistence会依赖Domain模块的。

###  Web模块
Web模块包含Controller等相关代码。  
如果用SpringMVC则需要依赖Spring。
### Start模块
Start模块是SpringBoot的启动类。

## 测试
对不同模块的测试。
Types，Domain模块单元测试。
Application模块的代码依赖外部抽象类。使用Mock抽象实现的方式依然可以测试。
Infrastructure模块，每个Infrastructure模块相互独立，变化不大，虽然有IO操作，但一劳永逸。
Web模块，把Controller依赖的服务类都Mock掉，因为逻辑都在Application Service中，其中的逻辑较少。使用Spring的MockMVC测试，或者通过HttpClient调用接口测试。
Start模块，写的是集成测试。单元测试都能100%覆盖后，集成测试用来验证整体链路的真实性。

## 代码的演进/变化速度
DDD中不同模块的代码的演进速度是不一样的：
* Domain层属于核心业务逻辑
需求变化代码经常被修改。Entity管理单个对象的变化，Domain Service解决多个对象间的业务逻辑变更。
* Application层属于Use Case（业务用例）
业务用例是大方向的需求，业务相对稳定。
新添加业务用例可以通过新添加接口实现。
* Infrastructure
变更比较低频。
外部依赖的变更频率一般远低于业务逻辑的变更频率。

领域“驱动”的核心思想：越外层的代码越稳定，越内层的代码演进越快。领域就像汽车的发动机。

-----------------
>参考  
> [领域层设计规范](https://mp.weixin.qq.com/s/w1zqhWGuDPsCayiOgfxk6w)
# 领域层设计规范
领域层设计的挑战：业务规则放在哪儿的权衡，放在Entity、ValueObject 还是 DomainService。
设计目标： 避免未来的扩展性差，又要确保不会过度设计导致复杂性。
## 游戏案例
###  背景和规则
除了角色外还有一些复杂的攻击规则
玩家（Player）：战士（Fighter）、法师（Mage）、龙骑（Dragoon）
怪物（Monster）：可以是兽人（Orc）、精灵（Elf）、龙（Dragon），怪物有血量
武器（Weapon）：可以是剑（Sword）、法杖（Staff），武器有攻击力
装备行为：玩家可以装备一个武器
攻击行为：武器攻击可以是物理类型（0），火（1），冰（2）等，武器类型决定伤害类型

攻击规则：
兽人对物理攻击伤害减半
精灵对魔法攻击伤害减半
龙对物理和魔法攻击免疫，除非玩家是龙骑，则伤害加倍
### 面向对象OOP实现
父类，和子类继承，规则代码写到角色对象里。

#### OOP缺陷
1、业务变更时软件设计违反开闭原则
新加装备的规则:战士只能装备剑，法师只能装备法杖，战士和法师都能装备匕首（dagger）
继承虽然可以通过子类扩展新的行为，但因为子类可能直接依赖父类的实现，导致一个变更可能会影响所有对象。在这个例子里，如果增加任意一种类型的玩家、怪物或武器，或增加一种规则，都有可能需要修改从父类到子类的所有方法。
比如增加一个武器类型：狙击枪，能够无视所有防御一击必杀，需要修改的代码包括：Weapon，Player和所有的子类（是否能装备某个武器的判断），Monster和所有的子类（伤害计算逻辑）。

现有逻辑的变更可能会影响一些原有的代码，导致一些无法预见的影响。
实际开发中，单元测试很难保障单测的覆盖率。

继承有有点也有缺陷：
继承虽然能Open for extension，但很难做到Closed for modification。所以今天解决OCP的主要方法是通过Composition-over-inheritance，即通过组合来做到扩展性，而不是通过继承。

2、Player.attack(monster) 还是 Monster.receiveDamage(Weapon, Player)？
如果是Player拿着一把双刃剑会同时伤害自己这种写法有就问题，所以引入一个问题：代码写哪里的原则是什么

3、新引入相同行为时的代码重复问题
Player和Monster类中都增加移动行为。
引入一个行为可以通过父类来解决，当引入多个行为时一个父类就不能很好的解决，要支持受限于java语言是不支持多继承的。
所以这种情况需要写重复代码了。

问题总结：从直觉来看OOP的逻辑很简单，但如果你的业务比较复杂，未来会有大量的业务规则变更时，简单的OOP代码会在后期变成复杂的一团浆糊，逻辑分散在各地，缺少全局视角，各种规则的叠加会触发bug。

电商体系里的优惠、交易等链路经常会碰到类似的坑。

这类问题的本质在于：
业务规则的归属到底是对象的“行为”还是独立的”规则对象“？
业务规则之间的关系如何处理？
通用“行为”应该如何复用和维护？


### Entity-Component-System（ECS）架构简介
在讲DDD的解法前，我们先去看看一套游戏里最近比较火的架构设计，Entity-Component-System（ECS）是如何实现的。

#### ECS介绍
##### 游戏代码的核心问题
性能：高的渲染率（60FPS），也就是说整个游戏世界需要在1/60s（大概16ms）内完整更新一次（包括物理引擎、游戏状态、渲染、AI等）。而在一个游戏中，通常有大量的（万级、十万级）游戏对象需要更新状态，除了渲染可以依赖GPU之外，其他的逻辑都需要由CPU完成，甚至绝大部分只能由单线程完成，导致绝大部分时间复杂场景下CPU（主要是内存到CPU的带宽）会成为瓶颈。在CPU单核速度几乎不再增加的时代，如何能让CPU处理的效率提升，是提升游戏性能的核心。
代码组织：如同第一章讲的案例一样，当我们用传统OOP的模式进行游戏开发时，很容易就会陷入代码组织上的问题，最终导致代码难以阅读，维护和优化。
可扩展性：这个跟上一条类似，但更多的是游戏的特性导致：需要快速更新，加入新的元素。一个游戏的架构需要能通过低代码、甚至0代码的方式增加游戏元素，从而通过快速更新而留住用户。如果每次变更都需要开发新的代码，测试，然后让用户重新下载客户端，可想而知这种游戏很难在现在的竞争环境下活下来。

##### ECS架构
Entity：用来代表任何一个游戏对象，但是在ECS里一个Entity最重要的仅仅是他的EntityID，一个Entity里包含多个Component
Component：是真正的数据，ECS架构把一个个的实体对象拆分为更加细化的组件，比如位置、素材、状态等，也就是说一个Entity实际上只是一个Bag of Components。
System（或者ComponentSystem，组件系统）：是真正的行为，一个游戏里可以有很多个不同的组件系统，每个组件系统都只负责一件事，可以依次处理大量的相同组件，而不需要去理解具体的Entity。所以一个ComponentSystem理论上可以有更加高效的组件处理效率，甚至可以实现并行处理，从而提升CPU利用率。

ECS的一些核心性能优化包括将同类型组件放在同一个Array中，然后Entity仅保留到各自组件的pointer，这样能更好的利用CPU的缓存，减少数据的加载成本，以及SIMD的优化等。


##### 分析

组件化：  
对象的数据和行为进行拆分多个组件和组件系统

行为抽离：  
面向对象的方式是行为放在角色类里，比如移动代码、战斗代码、渲染代码、AI代码。  
ECS是把这些不同的代码放到单独的System类里。
行为抽离伴随着一些依赖的抽离。面向对象是对象直接依赖其他对象，现在变为System类依赖其他对象。
所以专门的System行为对象接管了多个对象之间有交互的行为。

数据驱动：
参数驱动了行为。
参数组合可以改变对象的行为和玩法。
数据驱动+组件化架构，所有物品的配置最终就是一张表，修改也极其简单。这个也是组合胜于继承原则的一次体现。

##### ECS的缺陷
游戏界已经开始崭露头角，未在大型商业应用中被使用过。
程序员工作方式：写逻辑脚本到写组件。
缺陷：最大的优点是为了提升性能做的数据/状态（State）和行为（Behaivor）分离，并且为了降低GC成本，直接操作数据。
商业应用的数据正确一致，程序健壮性优于性能，ECS带来的好处小于带来的改变的弊端。

但不妨碍我们在DDD中借鉴组件化、跨对象行为的抽离、以及数据驱动模式这些突破的思维方式。

### DDD架构
#### 领域对象
##### 实体类
实体类包含Player、Monster和Weapon。
以上实体类包含ID和内部状态。
注意：Player是实体类不是聚合根，所以不能直接引用Weapon，但可以引用WeaponId。

##### 值对象的组件化
通过接口的方式对领域对象进行组件化处理。
比如Player实现Moveable接口。
Moveable接口里包含了对组件数据的获取和行为：行为操作的是组件数据。
注意：  
1、Moveable接口不能直接有Setter，因为Player是实体，实体不能直接改变属性。

#### 装备行为
领域服务（Domain Service），对跨实体的行为进行统一处理。
注意：  
实体Player只能保存自己的状态，不能把领域服务EquipmentService当做自己的属性，否则会破坏Entity的Invariance（个人理解非多样）。
但可以通过方法参数传入的方式引用实体和领域服务，确保不污染Player自有状态。

##### 策略模式完成装备规则的判断
代码组织：
策略父类：两个方法，匹配方法（留给子类实现，判断传入的玩家是否和子类对应，子类名字固定的前提下判断玩家类别即可）和装备范围方法
策略子类：一类玩家一个策略子类。
策略管理类：保存策略子类的列表，循环策略类判断传入的玩家是否能装备某个武器。

步骤：
1、判断某个策略是否能"应用"到某个玩家
2、如果第一步成个，则判断该玩家的武器范围

总结：
规则增加只需要添加新的Policy类。实现了OCP。

#### 攻击行为
领域服务（Domain Service），CombatService。
该服务有以下，攻击的方法，参数是Player和Monster；

服务方法步骤：
1、查询武器
2、伤害策略管理类计算伤害
3、对伤害进行扣减

伤害策略代码组织：
策略父类，两个方法，一个方法是传入的参数是否适用子类策略（子类实现），另一个方法是计算参数的伤害值。
策略子类，有多少种伤害，写多少个子类
策略管理类，策略子类的保存，策略子类的遍历执行返回伤害值。

CombatService领域服务和装备行为中的EquipmentService领域服务区别：
EquipmentService只影响一个实体，可以通过参数传入被影响的实体。
CombatService跨了多个实体，不能简单传入某个实体了。

##### 单元测试
要素：目标某一种CASE， Given 给定原数据， When 发生行为， Then 结果断言

#### 移动系统
MovementSystem类似于ECS一样的System。
组件化管理移动。把所有可移动对象放入List，进行批量更新。

注意：这也相当于一个领域服务，也没有依赖于外部服务或存储层。

### DDD领域层的一些设计规范
基于领域对象 + 领域服务的DDD架构相比 OOP 和 ECS 它的设计规则是最复杂的。
#### 实体类（Entity）
创建即一致：强校验的构造器。
使用Factory模式来降低调用方复杂度。
避免public setter：建议修改方法名字。
通过聚合根保证主子实体的一致性：子实体不能单独存在，不能直接被引用；子实体没有单独的Repository；多个子实体之间的状态需要聚合根来保障。
不强依赖其他聚合根实体或领域服务：不容易单测，以及无法保证外部实体对本实体的一致性影响。建议依赖外部实体的强类型ID；"无副作用"的外部依赖，通过方法入参的方式传入；有副作用的依赖通过引入Domain Service解决。
任何实体的行为只能直接影响到本实体（和其子实体）：确保代码可读性、可理解的原则；减少未知变更

#### 领域服务（Domain Service）
几种类型：
1、单对象策略型
2、跨对象事务型
多个实体
3、通用组件型
提供了组件化的行为，但本身又不直接绑死在一种实体类上。

####  策略对象（Domain Policy）
策略模式在DDD架构中核心作用是用来封装领域规则。

为了降低一个Policy的可测试性和复杂度：Policy不应该直接操作对象（修改对象里的值），而是通过返回计算后的值，在Domain Service里对对象进行操作。

## 副作用的处理方法 - 领域事件
核心领域模型状态变更后，同步或者异步对另一个对象的影响或行为。

比如：怪兽被打死后，玩家增加经验值、升级、更新排行榜等一系列后续事件的代码该如何维护？

### 领域事件介绍
流水代码复杂问题：反应代码和触发条件直接耦合，并且是隐性耦合。
领域事件解决的就是将隐性副作用显性化。

### 领域事件实现
EventBus： 事件注册器（内部列表）， 事件分发器，事件分发方法(单个event，所有分发器来处理)，注册监听器方法（进入事件注册器内部）
### 领域事件的缺陷和展望
EventBus是单例的，不容易被单测。
显性去处理事件。

## 总结
对象行为的影响面：
是仅影响单一对象还是多个对象，
规则未来的拓展性、灵活性。
性能要求，
副作用的处理，等等。
好的设计是多因素的取舍，所以平时要有积累，理解框架逻辑优缺点，才能选出最平衡的方案。

---------
[如何避免写流水账代码](https://mp.weixin.qq.com/s/1rdnkROdcNw5ro4ct99SqQ)
# 如何避免写流水账代码
## 流水账代码的问题
代码所在的方法违背了SRP（Single Responsbility Principle）单一职责原则。
代码里有很多功能：业务计算，校验，通信协议（controller里）等。

## DDD分层思想重构代码
### 案例
下单链路。假设我们在做一个checkout接口，需要做各种校验、查询商品信息、调用库存服务扣库存、然后生成订单
### Interface接口层

单独抽取接口层，单独作为对外的"门户"，将网络协议和业务逻辑解耦。

另外接口层还包括业务逻辑的接口，提供给Application使用，"门户"这一层接口层是对外部的。

注意区分上面两种Interface。
#### 接口层的组成
网络协议转化（技术框架已经做了这个）、鉴权、Session、限流（保护下游服务）、缓存（只读特殊情形）、异常（避免暴露给调用端）、日志（debug或者统计用）。

网关：网关与接口层的功能有相同之处，但独立的接口层在DDD设计中依然有必要。

#### 返回值和异常
规范：  
Interface层的HTTP和RPC接口，返回值为Result，捕捉所有异常之后再返回。
Application层的所有接口返回值为DTO，不负责处理异常。

Interface层异常处理：AOP统一处理。

#### 接口的数量和业务间的隔离
此处表示核心业务接口，此处的接口由编排服务Application调用。
矛盾：一个领域服务的所有方法放在一个Controller中  VS 上游业务比较多方法和方法参数膨胀
规范：Interface层的类应该是“小而美”的，应该是面向“一个单一的业务”或“一类同样需求的业务”，需要尽量避免用同一个类承接不同类型业务的需求。
结论：所以把接口按照可预见的方式适当的拆分可以避免过度膨胀的问题，业务需求是快速变化的，所以接口层也要跟着快速变化，通过独立的接口层可以避免业务间相互影响，但我们希望相对稳定的是Application层的逻辑。


### Application层
#### 组成
ApplicationService应用服务：最核心的类，负责业务流程的编排，但本身不负责任何业务逻辑。
DTO Assembler：负责将内部领域模型转化为可对外的DTO。
Command、Query、Event对象：作为ApplicationService的入参。
返回的DTO：作为ApplicationService的出参。

#### CQE
Command、Query、Event对象。
都是Value Object，语义有差别：
C：服务指令，写操作，有返回值；Q：查询操作，读操作，有返回；E：发生过的既有事件，一般是写操作，没有返回值。


CQE相比多个分开的方法参数：是有"语意"的
```
public interface CheckoutService {
    OrderDTO checkout(@Valid CheckoutCommand cmd);
    List<OrderDTO> query(OrderQuery query);
    OrderDTO getOrder(Long orderId); // 注意单一ID查询可以不用Query
}
```

ApplicationService的入参是CQE对象，但是出参却是一个DTO:
CQE有意图，DTO仅仅为了与外部交互。
CQE的校验可以使用Spring Validation。

针对于不同语意的指令，要避免CQE对象的复用：比如新增和更新命令分开。

#### ApplicationService
负责了业务流程的编排，是将原有业务流水账代码剥离了校验逻辑、领域计算、持久化等逻辑之后剩余的流程，是“胶水层”代码。

ApplicationService的组织形态：
1、一个类包含完整的业务流（多个用例），代码较多
2、独立Handler类，通过注入到AppService的方式使用
3、通过CommandBus、EventBus：XXEvent -> MQ -> Event -> EventBus -> Handler。这种方式代码解耦，但不方便读懂代码。

##### 如何区分业务流程封装和业务逻辑代码
1、判断是否业务流程的几个点
1.1、没有if/else分支逻辑。Cyclomatic Complexity（循环复杂度）应该尽量等于1， throw代码除外。  
业务逻辑封装在DomainService或者Entity里。
1.2、不要有任何计算
1.3、数据转换交给其他对象

2、ApplicationService“套路”
2.1、AppService通常不做任何决策（Precondition除外）
决策代码在DomainService或者Entity里。
2.2、过程，准备数据->执行内存操作->持久化
准备数据，数据来源为外部服务持久化源的Entity，VO
执行操作，新对象的创建、复制，调用领域对象
持久化，持久化或者操作外部系统或者发送异步消息

#### DTO Assembler
ApplicationService应该永远返回DTO而不是Entity。
好处：
1、POJO边界
2、降低外部依赖
3、降低成本 字段内存开销的意思？

DTO Assembler通常不建议有反操作，也就是不会从DTO到Entity，因为通常一个DTO转化为Entity时是无法保证Entity的准确性的。

#### Result vs Exception

在Application层、Domain层，以及Infrastructure层，遇到错误直接抛异常是最合理的方法：降低了Result.isSuccess代码判断。
Exception由对外的Interface那一层内部统一处理。

#### Anti-Corruption Layer防腐层
无防腐层：Application Service直接调用外部服务。
有防腐层：二者之间家里一层Facade接口和实现以及数据转化逻辑。

防止因为下游服务字段的变更直接影响到AppService。

### Orchestration vs Choreography
复杂的业务流程里，我们通常面临以上两种模式。
在Orchestration模式中，所有的流程都是由一个节点或服务触发的。我们常见的业务流程代码，包括调用外部服务，就是Orchestration，由我们的服务统一触发。
Choreography：每个服务都是独立的个体，可能会响应外部的一些事件，但整个系统是一个整体。

####  模式的区别和选择
Orchestration：涉及到一个服务调用到另外的服务，对于调用方来说，是强依赖的服务提供方。
Choreography：每一个服务只是做好自己的事，然后通过事件触发其他的服务，服务之间没有直接调用上的依赖。但要注意的是下游还是会依赖上游的代码（比如事件类），所以可以认为是下游对上游有依赖。

代码灵活性：Choreography比Orchestration灵活。

调用链路：前者Command-Driven指令驱，后者是Event-Driven事件驱动的。

业务职责：前者，主动调用方为整个业务和结果负责，后者不关心整个业务，只关心自己的。

如何选择是用Orchestration还是Choreography：
1、明确依赖方向
从下游的角度，上游是否要感知自己决定指令还是事件驱动；从上游的角度，是否需要对下游强依赖决定指令还是事件驱动。
2、找出业务中的"负责人"
从单一职责的角度考虑，当前服务是否应该作为在整个场景中的下游某个场景的"负责"人。

如果遇到不确定的情况，可以在实际中进行权衡。

#### DDD分层架构
O & C属于Interface内部的事情，而不是AppService的事情。











