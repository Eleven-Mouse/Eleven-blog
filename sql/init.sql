-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: eleven_blog
-- ------------------------------------------------------
-- Server version	8.0.42

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `article`
--
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `article`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文章ID',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文章标题',
  `summary` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文章摘要',
  `content` longtext COLLATE utf8mb4_unicode_ci COMMENT '文章内容（Markdown格式）',
  `category_id` bigint DEFAULT NULL COMMENT '分类ID',
  `view_count` int DEFAULT '0' COMMENT '浏览次数',
  `is_comment` tinyint DEFAULT '1' COMMENT '是否允许评论：0-否，1-是',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-草稿，1-已发布，2-已删除',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `cover_image` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tags` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  KEY `idx_publish_time` (`publish_time`)
) ENGINE=InnoDB AUTO_INCREMENT=76 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `article`
--

LOCK TABLES `article` WRITE;
/*!40000 ALTER TABLE `article` DISABLE KEYS */;
INSERT INTO `article` VALUES (64,'el-autocomplete的基本使用',' el-autocomplete的基本使用\r\n\r\n\r\n.....','### el-autocomplete的基本使用\r\n\r\n\r\n',6,0,1,1,'2026-01-01 01:57:25','2026-01-01 01:57:25','2026-01-01 01:57:25','','6,5'),(65,'Spring的原理',' Spring的底层原理：\r\n\r\n 一，Spring的概念：\r\n\r\nSpring框架是一个开源的Java应用程序框架，主要用于管理Java对象的生命周期和依赖关系，简化开发过程。\r\n\r\n\r\n\r\n 核心底层逻辑：IoC 与 AOP\r\n\r\nSpring 框架的基石是两个核心概念：\r\n\r\n1......','# Spring的底层原理：\r\n\r\n## 一，Spring的概念：\r\n\r\nSpring框架是一个开源的Java应用程序框架，主要用于管理Java对象的生命周期和依赖关系，简化开发过程。\r\n\r\n\r\n\r\n### 核心底层逻辑：IoC 与 AOP\r\n\r\nSpring 框架的基石是两个核心概念：\r\n\r\n1. **控制反转 (IoC)**\r\n\r\n2. **面向切面编程 (AOP)**\r\n\r\n#### 1. 控制反转 (IoC) 与依赖注入 (DI)\r\n\r\n- **逻辑（是什么和为什么）：**\r\n  \r\n  - **传统方式：** 在传统的程序设计中，对象A如果需要使用对象B，会由A内部通过 `new` 关键字主动创建B的实例。这意味着A对B有绝对的**控制权**，两者紧密**耦合**在一起。\r\n  \r\n  - **IoC 方式：** IoC 将创建和组装对象的控制权从应用程序代码中“反转”到了一个外部容器（即 Spring IoC 容器）。对象A不再负责创建B，而是通过**依赖注入 (DI)** 的方式，由容器在运行时将B的实例“注入”到A中。DI 是 IoC 原则最典型的实现方式。\r\n  \r\n  - **目的：** 实现对象之间的**解耦**。对象只关注自身的业务逻辑，而不关心依赖从何而来、如何创建，这使得代码更加灵活、可测试、可维护。\r\n\r\n- **实现过程（怎么做）：**  \r\n  IoC 容器的实现过程，本质上就是 **Bean 的生命周期管理**。这个过程可以概括为以下几个关键步骤：\r\n\r\n    **a. 配置元数据 (Configuration Metadata)**\r\n\r\n- 容器需要知道要创建哪些对象（Bean），以及这些对象之间的依赖关系。这些信息称为“配置元数据”。\r\n\r\n- **实现方式：**\r\n  \r\n  - **XML 配置文件：** 早期的标准方式，在 `.xml` 文件中定义 `<bean>` 标签。\r\n  \r\n  - **注解 (Annotation)：** 现代主流方式，使用如 `@Component`, `@Service`, `@Autowired` 等注解。\r\n  \r\n  - **Java 配置类 (Java Configuration)：** 使用 `@Configuration` 和 `@Bean` 注解在 Java 类中定义配置。\r\n\r\n  **b. 容器的启动与初始化**\r\n\r\n- 应用程序启动时，会创建 Spring IoC 容器（最常用的是 `ApplicationContext`）。\r\n\r\n- 容器会读取、解析上述的配置元数据。\r\n\r\n  **c. Bean 的实例化**\r\n\r\n- 根据配置信息，容器通过**反射 (Reflection)** 机制，使用类的无参构造函数或工厂方法来创建 Bean 的实例。例如：`Class.forName(\"com.example.MyClass\").newInstance()`。\r\n\r\n  **d. 依赖注入**\r\n\r\n- 容器分析 Bean 之间的依赖关系（通过构造函数参数、属性 Setter 方法或字段上的 `@Autowired` 注解识别）。\r\n\r\n- 容器将其依赖的 Bean 实例注入到目标 Bean 中。这通常也是通过反射调用 Setter 方法或直接设置字段值来实现的。\r\n\r\n  **e. Bean 生命周期的后置处理**\r\n\r\n- 这是 Spring 提供强大扩展能力的关键。容器允许开发者“介入” Bean 的创建过程。\r\n\r\n- **BeanPostProcessor 接口：** 这是一个核心接口。容器在完成依赖注入后，会遍历所有 `BeanPostProcessor`，调用其 `postProcessBeforeInitialization` 和 `postProcessAfterInitialization` 方法。AOP 的动态代理就是在这个阶段创建的！\r\n\r\n- **生命周期回调：** 如果 Bean 实现了 `InitializingBean` 接口或定义了 `init-method`，容器会调用这些初始化方法。同样，如果实现了 `DisposableBean` 接口或定义了 `destroy-method`，在容器关闭时会被调用。\r\n\r\n  **f. 将完整的 Bean 放入容器缓存**\r\n\r\n- 经过上述步骤后，一个完整的、可用的 Bean 被放入一个名为“单例缓存”的 Map 中。后续每次请求该 Bean 时，容器都会从这个缓存中返回同一个实例（默认单例作用域下）。\r\n\r\n- ****\r\n\r\n#### 2. 面向切面编程 (AOP)\r\n\r\n- **逻辑（是什么和为什么）：**\r\n  \r\n  - 在业务系统中，有很多横跨多个模块的功能点，如日志记录、事务管理、安全校验等。这些功能被称为“横切关注点”。\r\n  \r\n  - 如果将这些代码直接嵌入到业务逻辑中，会导致代码混乱、冗余且难以维护。\r\n  \r\n  - AOP 允许我们将这些横切关注点模块化为独立的“切面 (Aspect)”，然后通过“织入 (Weaving)”的方式，在运行时将它们动态地应用到目标方法上。这样，业务代码可以保持纯净。\r\n\r\n- **实现过程：**   \r\n  Spring AOP 默认使用**动态代理** 技术来实现。\r\n  \r\n  **a. 找到匹配的切点**\r\n  \r\n  - 根据配置的切点表达式（如 `@Pointcut(\"execution(* com.example.service.*.*(..))\")`），找到所有需要被增强的 Bean 和方法。\r\n\r\n  **b. 创建代理对象**\r\n\r\n- 当 Bean 的初始化进行到 `BeanPostProcessor` 的后置处理阶段时，Spring 会检查当前 Bean 是否匹配某个切点。\r\n\r\n- **如果匹配：** Spring 不会将原始的 Bean 实例放入容器，而是为其创建一个**代理对象**。\r\n\r\n- **代理方式：**\r\n  \r\n  - **JDK 动态代理：** 如果目标类实现了至少一个接口，Spring 默认使用 JDK 动态代理。代理对象会实现相同的接口。\r\n  \r\n  - **CGLIB 代理：** 如果目标类没有实现任何接口，Spring 会使用 CGLIB 库生成一个目标类的子类作为代理。\r\n\r\n  **c. 方法调用与拦截器链**\r\n\r\n- 当客户端代码调用代理对象的方法时，调用会被代理对象拦截。\r\n\r\n- 代理对象会创建一个**方法调用链（拦截器链）**，这个链包含了通知（Advice，如 `@Before`, `@After` 等）的逻辑。\r\n\r\n- 代理对象会按顺序执行这个调用链，从而将切面的逻辑动态地“织入”到原始方法执行的周围。\r\n\r\n****\r\n\r\n## 二，Spring容器：\r\n\r\n#### 1，概念：\r\n\r\n    简单来说，**Spring 容器就是一个管理应用程序中所有组件（称为 Bean）的“大管家”**。\r\n\r\n   它负责：\r\n\r\n1. **创建对象：** 不再使用 `new` 关键字，而是由容器来创建。\r\n\r\n2. **组装对象：** 解决对象之间的依赖关系（谁需要谁）。\r\n\r\n3. **管理生命周期：** 控制对象的生老病死（何时创建、初始化、销毁）。\r\n\r\n4. **提供配置：** 提供一个统一的配置方式来定义这些组件。\r\n\r\n它的核心思想是 **IoC（控制反转）**，即把创建和组装对象的控制权从应用程序代码中“反转”给了容器。**依赖注入（DI）** 是实现 IoC 的主要技术手段。\r\n\r\n\r\n\r\n\r\n\r\n#### 2，Spring容器的类型：\r\n\r\nSpring 提供了两种风格的容器，它们有共同的根接口 `BeanFactory`，但在功能上有差异。\r\n\r\n1. `BeanFactory`（基础容器）\r\n\r\n- **接口位置：** `org.springframework.beans.factory.BeanFactory`\r\n\r\n- **特点：**\r\n  \r\n  - 这是一个**最基础、最底层的 IoC 容器接口**。\r\n  \r\n  - 它实现了容器的基本功能：加载配置、创建和管理 Bean。\r\n  \r\n  - 它采用了 **“懒加载”** 模式。即，只有当客户端真正请求某个 Bean 时（如调用 `getBean()` 方法），它才会对该 Bean 进行实例化和依赖注入。\r\n\r\n- **适用场景：** 在资源非常受限的环境下（如移动设备、Applet），为了节省内存和启动时间，可能会直接使用 `BeanFactory`。但在现代企业级应用中，很少直接使用。\r\n\r\n\r\n\r\n\r\n\r\n2. `ApplicationContext`（高级容器，推荐使用）\r\n\r\n- **接口位置：** `org.springframework.context.ApplicationContext`\r\n\r\n- **特点：**\r\n  \r\n  - 它是 `BeanFactory` 的**子接口**，意味着它包含了 `BeanFactory` 的所有功能，并在此基础上进行了大量**增强**。\r\n  \r\n  - 它提供了更多企业级的功能，因此也常被称为“Spring 上下文”。\r\n  \r\n  - 它默认采用 **“急切的预加载”** 模式。在容器启动时，就会创建并配置所有单例作用域的 Bean（非懒加载的）。这有助于在应用启动时就发现配置错误，而不是在运行时。\r\n\r\n- **为什么推荐使用 `ApplicationContext`？**  \r\n  因为它提供了 `BeanFactory` 所没有的便利功能：\r\n  \r\n  - **与 Spring AOP 集成：** 无缝支持面向切面编程。\r\n  \r\n  - **消息资源处理（国际化）：** 支持 `MessageSource`，方便处理 i18n。\r\n  \r\n  - **事件发布机制：** 支持 `ApplicationEvent` 和 `ApplicationListener`，用于 Bean 之间的解耦通信。\r\n  \r\n  - **便捷的访问资源：** 更容易访问各种资源（如 URL 和文件）。\r\n  \r\n  - **继承性：** 可以有多个上下文，并通过父上下文来共享配置。\r\n\r\n\r\n\r\n\r\n\r\n**结论：** 在几乎所有情况下，我们都应该使用 `ApplicationContext`。`BeanFactory` 通常只用于底层扩展或非常特殊的环境。\r\n\r\n****\r\n\r\n## 三，Spring的核心模块：\r\n\r\nSpring框架由多个模块组成，主要包括：\r\n\r\n1. **Spring Core**：提供核心工具类和IoC容器。\r\n\r\n2. **Spring AOP**：提供面向切面的编程支持。\r\n\r\n3. **Spring ORM**：提供与ORM框架的集成支持。\r\n\r\n4. **Spring DAO**：提供数据访问对象的支持。\r\n\r\n5. **Spring Context**：提供上下文信息和事件传播支持。\r\n\r\n6. **Spring Web**：提供Web应用程序的支持。\r\n\r\n7. **Spring Web MVC**：提供Web MVC框架。\r\n\r\n****\r\n\r\n## 四，Bean的生命周期\r\n\r\nSpring容器管理Bean的生命周期，包括创建、初始化和销毁。Bean的生命周期由以下几个步骤组成：\r\n\r\n1. **实例化**：通过构造器或工厂方法创建Bean实例。\r\n\r\n2. **属性设置**：通过依赖注入设置Bean的属性。\r\n\r\n3. **初始化**：调用初始化方法（如*init-method*或*@PostConstruct*）。\r\n\r\n4. **销毁**：调用销毁方法（如*destroy-method*或*@PreDestroy*）。\r\n\r\n\r\n\r\n![](E:\\deepseek_mermaid_20250924_04246a.png)\r\n\r\n\r\n\r\n***\r\n\r\n## 五，事件机制：\r\n\r\nSpring的事件机制基于观察者设计模式，通过**ApplicationEvent**类和**ApplicationListener**接口实现。Spring容器中的事件可以是容器事件（如上下文刷新事件）或自定义事件。\r\n\r\n****\r\n\r\n## 六 、总结：Spring 应用启动的宏观实现过程\r\n\r\n结合 IoC 和 AOP，一个典型的 Spring 应用启动流程如下：\r\n\r\n1. **启动容器：** 初始化 `ApplicationContext`（例如 `AnnotationConfigApplicationContext`）。\r\n\r\n2. **扫描加载：** 容器扫描指定的包路径，解析所有带有 `@Component`, `@Service`, `@Configuration` 等注解的类，将它们转化为 `BeanDefinition`（Bean 的定义信息），并注册到容器中。\r\n\r\n3. **实例化 Bean：** 容器根据 `BeanDefinition`，通过反射创建 Bean 的原始实例。\r\n\r\n4. **依赖注入：** 容器分析 `@Autowired` 等注解，将依赖的 Bean 注入到目标 Bean 中。\r\n\r\n5. **AOP 代理：** **`BeanPostProcessor` 开始工作**。特别是 `AnnotationAwareAspectJAutoProxyCreator` 这个后置处理器，它会检查当前 Bean 是否需要被 AOP 增强。如果需要，就用动态代理替换掉原始的 Bean 实例。\r\n\r\n6. **初始化：** 调用 Bean 的初始化回调方法。\r\n\r\n7. **就绪：** 完整的 Bean（可能是代理对象）被放入单例缓存，应用程序可以开始使用它们处理请求。\r\n\r\n### 核心底层技术栈\r\n\r\n- **反射 (Reflection)：** 实现 Bean 实例化、依赖注入的基础。\r\n\r\n- **动态代理 (Dynamic Proxy)：** 实现 AOP 的核心。\r\n\r\n- **设计模式：** 大量使用了工厂模式、模板方法模式、代理模式、策略模式等。\r\n  \r\n  - **工厂模式：** `ApplicationContext` 本身就是一个 Bean 工厂。\r\n  \r\n  - **模板方法：** 在 `BeanPostProcessor`、事务管理等地方广泛应用，定义了骨架，允许子类扩展特定步骤。\r\n\r\n- **XML/注解解析器：** 用于解析配置信息。\r\n\r\n简单来说，**Spring 的底层逻辑是通过 IoC 容器管理对象的生命周期和依赖关系，通过 AOP 动态代理增强对象功能，其实现过程依赖于反射、动态代理等Java核心机制和一系列精妙的设计模式。** 这种设计使得开发者能够专注于业务逻辑，而框架负责处理所有复杂的基础设施问题。\r\n',7,0,1,1,'2026-01-03 18:44:26','2026-01-01 01:58:02','2026-01-03 18:44:26','','7,6,5'),(66,'《苍穹外卖》设计模式：',' 《苍穹外卖》设计模式：\r\n\r\n 1,工厂模式：\r\n\r\n    Spring IoC容器管理所有Bean\r\n\r\n- 模式分析：\r\n  \r\n  - 产品：OrderService接口。\r\n  \r\n  - 具体产品：OrderServiceImpl类。\r.....','## 《苍穹外卖》设计模式：\r\n\r\n### 1,工厂模式：\r\n\r\n  **  Spring IoC容器管理所有Bean**\r\n\r\n- **模式分析**：\r\n  \r\n  - **产品**：`OrderService`接口。\r\n  \r\n  - **具体产品**：`OrderServiceImpl`类。\r\n  \r\n  - **工厂**：**Spring IoC容器**充当了抽象工厂和具体工厂的角色。\r\n  \r\n  - **客户端**：所有通过`@Autowired`注入`OrderService`的类（如`OrderController`）。\r\n\r\n- **好处**：\r\n  \r\n  - **控制反转**：将对象的创建权从程序员手中移交给了Spring容器，极大降低了耦合度。\r\n  \r\n  - **易于测试**：可以轻松地注入一个Mock的实现来进行单元测试。\r\n  \r\n  - **符合开闭原则**：要替换实现（例如升级为`OrderServiceV2Impl`），只需修改注解或配置，而无需修改客户端代码。\r\n  \r\n  ****\r\n  \r\n  ### 2，建造者模式：\r\n  \r\n  **Lombok `@Builder`创建DTO/VO**\r\n  \r\n  **好处**：\r\n  \r\n  - **代码可读性**：链式调用非常清晰，一眼就能看出对象的构造过程。\r\n  \r\n  - **解决重叠构造器问题**：对于有大量可选字段的对象，无需编写多个参数列表不同的构造器。\r\n  \r\n  - **创建不可变对象**：可以轻松创建`final`对象，保证线程安全。\r\n\r\n****\r\n\r\n### 3，代理模式：\r\n\r\n       **Spring AOP实现事务、日志**, 例如：**事务管理**（`@Transactional`）、**全局异常处理**  （`@RestControllerAdvice`）\r\n\r\n       **好处**：\r\n\r\n- **职责分离**：事务管理、日志记录、安全检查等**横切关注点**与核心业务逻辑完全分离。\r\n\r\n- **无侵入性**：不需要修改原有的业务代码，通过注解即可增强功能。\r\n  \r\n  ******\r\n  \r\n  ### 4 ,  模板方法模式：\r\n  \r\n  **支付回调流程**\r\n  \r\n  **好处**：\r\n  \r\n  **消除大量if-else**，代码结构清晰。\r\n  \r\n  **符合开闭原则**：新增一种支付方式，只需新增一个子类，无需修改原有模板逻辑。\r\n  \r\n  ****\r\n  \r\n  ### 5，策略模式：\r\n  \r\n  **订单状态处理**，例如:  **订单状态机**。订单从“待支付”到“待接单”到“配送中”等状态变迁，每个变迁都需要不同的校验和操作。\r\n\r\n       **好处**：\r\n\r\n       **将复杂的状态处理逻辑分解到独立的类中**，每个类职责单一。\r\n\r\n       **易于扩展**：新增状态处理逻辑只需添加新的策略类。\r\n\r\n\r\n',7,2,1,1,'2026-01-03 22:06:57','2026-01-03 22:06:57','2026-01-08 22:50:00','','1'),(67,'Docker的基础知识·笔记',' Docker的基础知识·笔记\r\n\r\n 一，什么是docker：\r\n\r\ndocker是一个 “容器化” 的平台——把应用及其依赖打包成轻量，一致，可移植的单元（镜像 image），运行后就是一个隔离的运行实例（容器 container）。\r\n\r\n容器启动快，占用少，适合开发，测试，部署.....','# Docker的基础知识·笔记\r\n\r\n## 一，什么是docker：\r\n\r\ndocker是一个 **“容器化”** 的平台——把应用及其依赖打包成轻量，一致，可移植的单元（镜像 image），运行后就是一个隔离的运行实例（容器 container）。\r\n\r\n容器启动快，占用少，适合开发，测试，部署微服务和CI/CD。\r\n\r\n****\r\n\r\n## 二，docker的核心概念：\r\n\r\n- **Image（镜像）：** 只读模板；包含应用、运行时、库、文件系统。类似可复用的快照，做糕点的模具。\r\n\r\n- **Container（容器）：** 镜像的一个可运行实例，读写层在运行时生成，**容器是镜像的实例。** 类似于模具做出来的糕点。\r\n\r\n- **Dockerfile：** 描述如何构建镜像的一组指令（像配方）。\r\n\r\n- **Registry（仓库）：** 存放镜像的地方（如 Docker Hub、私有 Registry），类似于模具蓝图。\r\n\r\n- **Docker Daemon（dockerd）：** 在宿主机上运行的后台服务，管理镜像、容器、网络、存储。\r\n\r\n- **Docker Client（docker CLI）：** 你在终端用的命令行工具，与 daemon 通信。\r\n\r\n- **Volume（卷）：** 用于持久化数据或在容器间共享数据。\r\n\r\n- **Network（网络）：** 容器间通信的抽象（bridge、host、overlay 等）。\r\n\r\n****\r\n\r\n## 三，常见的docker命令：\r\n\r\n```\r\n 拉取镜像\r\ndocker pull nginx:latest\r\n \r\n 运行容器（后台）并映射端口\r\ndocker run -d --name mynginx -p 8080:80 nginx:latest\r\n\r\n 列出运行中的容器\r\ndocker ps\r\n\r\n 列出所有容器（包括停止的）\r\ndocker ps -a\r\n\r\n 进入正在运行的容器（交互式 shell）\r\ndocker exec -it mynginx /bin/bash  # 或 /bin/sh\r\n\r\n 查看镜像\r\ndocker images\r\n\r\n 构建镜像（当前目录 Dockerfile）\r\ndocker build -t myapp:1.0 .\r\n\r\n 停止容器\r\ndocker stop mynginx\r\n\r\n 删除容器\r\ndocker rm mynginx\r\n\r\n 删除镜像\r\ndocker rmi myapp:1.0\r\n\r\n 查看容器日志\r\ndocker logs mynginx\r\n\r\n\r\n```\r\n\r\n****\r\n\r\n## 四，Dockerfile：把应用打包成镜像\r\n\r\n```\r\nDockerfile（以某个前端项目为例）\r\n\r\n# 阶段1：构建\r\nFROM node:20.12.1-slim as build-stage\r\n\r\n# 设置工作目录\r\nWORKDIR /memory\r\n\r\n# 复制 package.json 和 package-lock.json（如果存在）\r\nCOPY package*.json ./\r\n\r\n# 升级 npm 到最新版本\r\nRUN npm install -g npm@latest\r\n\r\n# 安装项目依赖\r\nRUN npm install\r\n\r\n# 复制项目文件到工作目录\r\nCOPY . .\r\n\r\n# 设置 NODE_OPTIONS 来增加 Node.js 内存限制\r\nENV NODE_OPTIONS=\"--max_old_space_size=4096\"\r\n\r\n# 构建应用\r\nRUN npm run build\r\n\r\n# 阶段2：运行\r\n# 使用 Nginx 镜像作为基础来提供前端静态文件服务\r\nFROM nginx:stable-alpine as production-stage\r\n\r\n# 设置工作目录\r\nWORKDIR /usr/share/nginx/html\r\n\r\n# 从构建阶段拷贝构建出的文件到 Nginx 目录\r\nCOPY --from=build-stage /memory/dist .\r\n\r\n# 可选：如果有自定义的 nginx 配置文件，取消注释下面一行并复制配置文件\r\nCOPY nginx.conf /etc/nginx/templates/default.conf.template\r\n\r\n# 将环境变量传递到 Nginx 配置文件中\r\n#RUN sed -i \'s|$BACK_API|\'\"$BACK_API\"\'|g\' /etc/nginx/conf.d/default.conf\r\n\r\n# 暴露80端口\r\nEXPOSE 80\r\n```\r\n\r\n**注：**\r\n\r\nFROM : 指定基础镜像\r\n\r\nWORKDIR : 设置工作目录\r\n\r\nCOPY / ADD : 拷贝文件\r\n\r\nRUN : 在构建镜像时执行的命令\r\n\r\nCMD / ENTRYPOINT : 指定容器启动命令\r\n\r\n**使用多阶段构建可以减小最终镜像体积**\r\n\r\n****\r\n\r\n## 五，Docker Compose（管理多个服务）：\r\n\r\n**Compose** :  用于定义和运行多容器 Docker 应用程序的工具。通过 Compose，可以使用 YML 文件来配置应用程序需要的所有服务。\r\n\r\n**Compose 使用的三个步骤：**\r\n\r\n- 使用 Dockerfile 定义应用程序的环境。\r\n\r\n- 使用 docker-compose.yml 定义构成应用程序的服务，这样它们可以在隔离环境中一起运行。\r\n\r\n- 最后，执行 **docker compose up -d** 命令来启动并运行整个应用程序。\r\n\r\n```\r\n# yaml 配置实例\r\n\r\nversion: \'3\'\r\nservices:\r\n  web:\r\n    build: .\r\n    ports:\r\n    - \"5000:5000\"\r\n    volumes:\r\n    - .:/code\r\n    - logvolume01:/var/log\r\n    links:\r\n    - redis\r\n  redis:\r\n    image: redis\r\nvolumes:\r\n  logvolume01: {}\r\n```\r\n\r\n****\r\n\r\n## 六，容器vs虚拟机：\r\n\r\n- 虚拟机（VM）：包含完整的操作系统（内核+用户空间），每个VM启动慢，占用大。\r\n\r\n- 容器：共享宿主机内核，只打包应用和依赖，启动快，资源占用少，容器更轻量，适合部署多个微服务在同一台主机上。\r\n\r\n****\r\n\r\n\r\n',7,0,1,1,'2026-01-03 22:07:19','2026-01-03 22:07:19','2026-01-03 22:07:19','','7'),(68,'redis总结：',' redis总结：\r\n\r\n 一，什么是redis？\r\n\r\n     Redis 是 Remote Dictionary Server 的缩写，是一个开源的、基于内存的键值存储数据库。它不仅支持简单的键值对，还支持丰富的数据结构，如字符串、哈希、列表、集合、位图、HyperLogLogs等。.....','# redis总结：\r\n\r\n#### 一，什么是redis？\r\n\r\n     Redis 是 Remote Dictionary Server 的缩写，是一个开源的、基于内存的键值存储数据库。它不仅支持简单的键值对，还支持丰富的数据结构，如字符串、哈希、列表、集合、位图、HyperLogLogs等。同时，Redis 也支持持久化，能够将内存中的数据异步保存到磁盘上，是内存数据库中最具代表性的产品之一。\r\n\r\n      与MySQL数据库不同的是，Redis的数据是存在**内存**中的。它的读写速度非常快，每秒可以处理超过10万次读写操作。因此redis被**广泛应用于缓存**，另外，Redis也经常用来做分布式锁。除此之外，Redis支持事务、持久化、[LUA 脚本](https://zhida.zhihu.com/search?content_id=235651583&content_type=Article&match_order=1&q=LUA+%E8%84%9A%E6%9C%AC&zd_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJ6aGlkYV9zZXJ2ZXIiLCJleHAiOjE3NTgyMDAxMDYsInEiOiJMVUEg6ISa5pysIiwiemhpZGFfc291cmNlIjoiZW50aXR5IiwiY29udGVudF9pZCI6MjM1NjUxNTgzLCJjb250ZW50X3R5cGUiOiJBcnRpY2xlIiwibWF0Y2hfb3JkZXIiOjEsInpkX3Rva2VuIjpudWxsfQ.NUt6kPAEpaNGFb91H_jJeB2m9FIPNmLqG-TGdTV3wX8&zhida_source=entity)、LRU 驱动事件、多种集群方案\r\n\r\n****\r\n\r\n#### 二，redis的常用应用场景：\r\n\r\n- **缓存**：利用其高速读写能力，Redis常用于缓存频繁访问的数据，提高应用程序的响应速度。\r\n\r\n- **会话存储**：Redis可用于存储和管理用户会话数据，处理会话信息。\r\n\r\n- **消息队列**：利用发布订阅功能和列表数据结构，Redis可以实现消息队列的功能。\r\n\r\n- **实时排行榜**：有序集合数据结构非常适合实现实时排行榜功能。\r\n\r\n****\r\n\r\n**三，redis的数据类型 :**\r\n\r\n 1.基本数据类型：\r\n\r\n- String（字符串）\r\n- Hash（哈希）\r\n- List（列表）\r\n- Set（集合）\r\n- zset（有序集合）\r\n\r\n 2.特殊的数据结构类型 :\r\n\r\n- Geospatial\r\n- Hyperloglog\r\n- Bitmap\r\n\r\n****\r\n\r\n##### 四， redis常用操作命令：\r\n\r\n- **启动 Redis 服务**：\r\n\r\n` redis-server `\r\n\r\n- **连接 Redis 客户端**：\r\n\r\n` redis-cli `\r\n\r\n- **停止 Redis 服务**：\r\n  \r\n  `redis-cli shutdown ``\r\n\r\n- **测试连通性**：\r\n\r\n`redis-cli ping `\r\n\r\n****\r\n\r\n##### Key 操作命令 :\r\n\r\n- **获取所有键**：\r\n\r\n`keys *`\r\n\r\n- **获取键总数**：\r\n\r\n`dbsize`\r\n\r\n- **判断键是否存在**：\r\n\r\n`exists key`\r\n\r\n- **删除键**：\r\n\r\n`del key`\r\n\r\n- **设置键的过期时间**：\r\n\r\n`expire key seconds`\r\n\r\n- **查询键的生命周期**：\r\n\r\n`ttl key`\r\n\r\n- **重命名键**：\r\n\r\n`rename key newkey`\r\n\r\n- **清除当前数据库**：\r\n\r\n`flushdb`\r\n\r\n- **清除所有数据库**：\r\n\r\n`flushall`\r\n\r\n****\r\n\r\n##### 数据类型操作命令 :\r\n\r\n**字符串类型 (String) :**\r\n\r\n**简介:** String是Redis最基础的数据结构类型，它是二进制[安全](https://link.zhihu.com/?target=https%3A//activity.huaweicloud.com/free_test/index.html%3Futm_source%3Dhwc-csdn%26utm_medium%3Dshare-op%26utm_campaign%3D%26utm_content%3D%26utm_term%3D%26utm_adplace%3DAdPlace070851)的，可以[存储](https://link.zhihu.com/?target=https%3A//activity.huaweicloud.com/free_test/index.html%3Futm_source%3Dhwc-csdn%26utm_medium%3Dshare-op%26utm_campaign%3D%26utm_content%3D%26utm_term%3D%26utm_adplace%3DAdPlace070851)图片或者序列化的对象，值最大[存储](https://link.zhihu.com/?target=https%3A//activity.huaweicloud.com/free_test/index.html%3Futm_source%3Dhwc-csdn%26utm_medium%3Dshare-op%26utm_campaign%3D%26utm_content%3D%26utm_term%3D%26utm_adplace%3DAdPlace070851)为512M\r\n\r\n- **设置值**：\r\n\r\n`set key value`\r\n\r\n- **获取值**：\r\n\r\n`get key`\r\n\r\n- **删除值**：\r\n\r\n`del key`\r\n\r\n- **递增值**：\r\n\r\n`incr key`\r\n\r\n- **递减值**：\r\n\r\n`decr key`\r\n\r\n- **追加值**：\r\n\r\n`append key value`\r\n\r\n- **获取值的长度**：\r\n\r\n`strlen key`\r\n\r\n****\r\n\r\n**哈希类型 (Hash) :**\r\n\r\n**简介:** 在Redis中，哈希类型是指v（值）本身又是一个键值对（k-v）结构\r\n\r\n**内部编码:**  `ziplist(压缩列表)`，`hashtable(哈希表)`\r\n\r\n- **设置字段值**：\r\n\r\n`hset hash field value`\r\n\r\n- **获取字段值**：\r\n\r\n`hget hash field`\r\n\r\n- **删除字段**：\r\n\r\n`hdel hash field`\r\n\r\n- **获取所有字段和值**：\r\n\r\n`hgetall hash`\r\n\r\n<mark>注意点：</mark>如果开发使用hgetall，哈希元素比较多的话，可能导致Redis阻塞，可以使用hscan。而如果只是获取部分field，建议使用hmget。\r\n\r\n****\r\n\r\n**列表类型 (List) :**\r\n\r\n**简介:** 列表（list）类型是用来[存储](https://link.zhihu.com/?target=https%3A//activity.huaweicloud.com/free_test/index.html%3Futm_source%3Dhwc-csdn%26utm_medium%3Dshare-op%26utm_campaign%3D%26utm_content%3D%26utm_term%3D%26utm_adplace%3DAdPlace070851)多个有序的字符串，一个列表最多可以[存储](https://link.zhihu.com/?target=https%3A//activity.huaweicloud.com/free_test/index.html%3Futm_source%3Dhwc-csdn%26utm_medium%3Dshare-op%26utm_campaign%3D%26utm_content%3D%26utm_term%3D%26utm_adplace%3DAdPlace070851)2^32-1个元素。\r\n\r\n**内部编码：** `ziplist（压缩列表）`、`linkedlist（链表）`\r\n\r\n- **从左边添加元素**：\r\n\r\n`lpush list value`\r\n\r\n- **从右边添加元素**：\r\n\r\n`rpush list value`\r\n\r\n- **获取列表长度**：\r\n\r\n`llen list`\r\n\r\n- **获取指定范围的元素**：\r\n\r\n`lrange list start end`\r\n\r\n- **删除最左边的元素**：\r\n\r\n`lpop list`\r\n\r\n- **删除最右边的元素**：\r\n\r\n`rpop list`\r\n\r\n****\r\n\r\n**集合类型 (Set) :**\r\n\r\n**简介:** 集合（set）类型也是用来保存多个的字符串元素，但是不允许重复元素\r\n\r\n**内部编码：**`intset（整数集合）`、`hashtable（哈希表）`\r\n\r\n- **添加元素**：\r\n\r\n`sadd set value`\r\n\r\n- **获取所有元素**：\r\n\r\n`smembers set`\r\n\r\n- **删除元素**：\r\n\r\n`srem set value`\r\n\r\n- **获取集合大小**：\r\n\r\n`scard set`\r\n\r\n****\r\n\r\n**有序集合类型 (Sorted Set) :**\r\n\r\n**简介：** 已排序的字符串集合，同时元素不能重复\r\n\r\n**底层内部编码:** `ziplist(压缩列表)`，`skiplist(跳跃表)`\r\n\r\n- **添加元素及其分数**：\r\n\r\n`zadd zset score value`\r\n\r\n- **获取所有元素**：\r\n\r\n`zrange zset start end`\r\n\r\n- **删除元素**：\r\n\r\n`zrem zset value`\r\n\r\n- **获取集合大小**：\r\n\r\n`zcard zset`\r\n',7,0,1,1,'2026-01-03 22:07:41','2026-01-03 22:07:39','2026-01-03 22:07:40','','1,2'),(70,'使用<el-anchor-link>目录跳转失效的可能问题',' 使用 <el-anchor-link> 目录跳转失效的可能问题\r\n\r\n 1，ID 命名不统一\r\n\r\n例如：你的侧边栏点击的是 ’heading-0‘，但正文标题的实际 ID 是 ‘Docker基础知识’\r\n\r\n锚点跳转的原理是 “地址栏的Hash必须与DOM元素的ID严格相等\r\n\r.....','# 使用 <el-anchor-link> 目录跳转失效的可能问题\n\n## 1，ID 命名不统一\n\n例如：你的侧边栏点击的是 ’#heading-0‘，但正文标题的实际 ID 是 ‘Docker基础知识’\n\n锚点跳转的原理是 **“地址栏的Hash必须与DOM元素的ID严格相等**\n\n## 2，scrollBehavior 全局拦截\n\n点击目录，URL改变，但是页面却纹丝不动或者回到顶部，如果你在router/index.js中设置了全局逻辑，例如： 路由切换时自动滚动到页面顶部，这时候锚点和路由就会“对冲”，当锚点刚要把页面拉下去，你的路由守卫瞬间又把页面拽回顶部，所以再写全局滚动策略时，****必须排除掉带 #（Hash）的情况**。**\n\n``` \n  // 路由切换时自动滚动到页面顶部\n  scrollBehavior(to, from, savedPosition) {\n    // 如果路径变了（从页面A跳到页面B），但没有锚点，才回到\n  顶部,main-scroll-container是页面滚动区域\n    if (to.path !== from.path && !to.hash) {\n      const container = document.querySelector(\'.main-scroll-container\')\n      if (container) {\n        container.scrollTop = 0\n      }\n    }\n\n    // 如果有锚点，我们什么都不做，让 el-anchor 自己去处理跳转\n    if (to.hash) {\n      return false // 返回 false 表示不干预滚动\n    }\n  },\n```\n\n## 3 ，组件属性不生效\n\n当你尝试给 MdPreview 传 :headingId，但控制台依然显示中文 ID 甚至空 ID，这个问题的本质就是第三方组件（如 md-editor-v3）的内部逻辑并不总是完全遵循 prop 传递，或者不同版本间属性名不一致。这个时候建议直接用 **DOM 暴力注入法**（在 nextTick 里手动修改 DOM 的 ID）是最高效、最稳妥的保底方案。\n\n```\nnextTick(() => {\n    // 找到预览区域内所有的标题标签\n    const previewEl = document.querySelector(\'#preview-only .md-editor-preview\')\n    if (previewEl) {\n      const headings = previewEl.querySelectorAll(\'h1, h2, h3, h4, h5, h6\')\n      headings.forEach((el, index) => {\n        // 强制覆盖原来的 ID，改为 heading-0, heading-1...\n        el.setAttribute(\'id\', `heading-${index}`)\n      })\n    }\n  })\n```\n\n## 4，滚动容器（Container）找错人\n\n如果你配置了 :container=\"scrollContainer\"，但点击无效。原因是 el-anchor 需要知道具体是哪个 div 在产生滚动条。如果你指向了一个高度自适应、没有 overflow: auto 的容器，它就抓不到滚动事件。\n\n## 5，特殊符号与空ID\n\n当你点击目录跳转时，有些标题能跳，有些（带 《》：， 的）不能跳。本质原因是：\n\n- **特殊字符**：中文标点在 URL 编码和 CSS 选择器匹配中非常容易出错。\n\n- **空 ID**：当标题全是符号时，组件可能生成一个 id=\"\" 的非法属性。\n\n可以直接用上面 **DOM 暴力注入法**实现强行跳转\n\n## 总结：\n\n## 想要实现一个完美的目录跳转\n\n1. **确定容器**：明确是全局滚动（window）还是局部滚动（某个 div）。\n\n2. **强制唯一 ID**：不使用标题文字，统一使用 heading-${index}。\n\n3. **双向注入**：\n   \n   - 侧边栏 href 绑定 heading-${index}。\n   \n   - 正文 DOM 在渲染后，手动或通过组件属性确保 ID 也是 heading-${index}。\n\n4. **路由放行**：在路由配置文件中，对带 hash 的跳转不做 scrollTop = 0 处理。\n\n5. **时机控制**：使用 nextTick 确保在正文完全渲染出来后再进行 ID 匹配或滚动操作。\n\n```示例代码\n\ntemplate层：\n<MdPreview\n    editorId=\"preview-only\"\n    :modelValue=\"article.content\"\n    @onGetCatalog=\"onGetCatalog\"\n    :headingId=\"(text, level, index) => `heading-${index}`\"\n    :markedHeadingId=\"(text, level, index) => `heading-${index}`\"\n/>\n\n<div class=\"catalog-title\">目录导航</div>\n    <el-anchor :container=\"scrollContainer\" :offset=\"70\" v-if=\"catalogList.length > 0\">\n        <el-anchor-link\n                v-for=\"item in catalogList\"\n                :key=\"item.uniqueId\"\n                :href=\"`#${item.uniqueId}`\"\n                :title=\"item.text\"\n                :class=\"`indent-level-${item.level}`\"\n                @click.prevent\n          />\n    </el-anchor>\n<div v-else class=\"empty-catalog\">暂无目录</div>\n\n\n\n\nscript 逻辑层：\nconst catalogList = ref([])\nconst scrollContainer = ref(null)\n\n// list 里的 text 就是现在正文里的实际 ID\nconst onGetCatalog = (list) => {\n  catalogList.value = list.map((item, index) => ({\n    ...item,\n    // 这里绝对不要用 item.text，只用 index\n    uniqueId: `heading-${index}`,\n  }))\n\n  nextTick(() => {\n    // 找到预览区域内所有的标题标签\n    const previewEl = document.querySelector(\'#preview-only .md-editor-preview\')\n    if (previewEl) {\n      const headings = previewEl.querySelectorAll(\'h1, h2, h3, h4, h5, h6\')\n      headings.forEach((el, index) => {\n        // 强制覆盖原来的 ID，改为 heading-0, heading-1...\n        el.setAttribute(\'id\', `heading-${index}`)\n      })\n    }\n  })\n}\n\n\n```\n\n\n',7,1,1,1,'2026-01-04 22:40:38','2026-01-04 22:40:38','2026-01-07 17:15:00','','5'),(71,'页面滚动进度条',' 页面滚动进度条\r\n\r\n<img src=\"file:///C:/Users/33313/AppData/Roaming/marktext/images/2025-12-27-18-23-12-image.png\" title=\"\" alt=\"\" width=\"701\">\r\n\r\n> 随着页面的.....','### 页面滚动进度条\r\n\r\n<img src=\"file:///C:/Users/33313/AppData/Roaming/marktext/images/2025-12-27-18-23-12-image.png\" title=\"\" alt=\"\" width=\"701\">\r\n\r\n> 随着页面的滚动而自行加减\r\n\r\n如何设置页面滚动进度条：\r\n\r\n组件：<  el-progress />\r\n\r\n上代码：\r\n\r\n> <template>\r\n>     <!-- 页面滚动进度条 -->\r\n>     <el-progress\r\n>       :percentage=\"scrollProgress\"\r\n>       :stroke-width=\"2.5\"\r\n>       :show-text=\"false\"\r\n>       class=\"scroll-progress\"\r\n>       status=\"success\"\r\n>     />\r\n>   </div>\r\n> </template>\r\n> \r\n> \r\n\r\n<template>\r\n    <!-- 页面滚动进度条 -->\r\n    <el-progress\r\n      :percentage=\"scrollProgress\"\r\n      :stroke-width=\"2.5\"\r\n      :show-text=\"false\"\r\n      class=\"scroll-progress\"\r\n      status=\"success\"\r\n    />\r\n</template>\r\n\r\n<template>\r\n\r\n\r\n\r\n\r\n\r\n\r\n</template>\r\n\r\n<template>\r\n    <el-progress\r\n      :percentage=\"scrollProgress\"\r\n      :stroke-width=\"2.5\"\r\n      :show-text=\"false\"\r\n      class=\"scroll-progress\"\r\n      status=\"success\"\r\n    />\r\n</template>\r\n\r\n>     <el-progress\r\n> \r\n>       :percentage=\"scrollProgress\"\r\n> \r\n>       :stroke-width=\"2.5\"\r\n> \r\n>       :show-text=\"false\"\r\n> \r\n>       class=\"scroll-progress\"\r\n> \r\n>       status=\"success\"\r\n> \r\n>     />\r\n\r\n        \r\n\r\n<script>\r\nconst scrollProgress = ref(0)\r\n\r\n// 计算页面滚动进度\r\nconst calculateScrollProgress = () => {\r\n  const windowHeight = window.innerHeight\r\n  const documentHeight = document.documentElement.scrollHeight\r\n  const scrollTop = window.pageYOffset || document.documentElement.scrollTop\r\n  const scrollableHeight = documentHeight - windowHeight\r\n  const progress = scrollableHeight > 0 ? (scrollTop / scrollableHeight) * 100 : 0\r\n  scrollProgress.value = Math.min(Math.max(progress, 0), 100)\r\n} \r\n// 监听滚动事件\r\nconst handleScroll = () => {\r\n  calculateScrollProgress()\r\n}\r\n\r\nonMounted(() => {\r\n  // 监听页面滚动\r\n  window.addEventListener(\'scroll\', handleScroll)\r\n  // 初始化计算一次\r\n  calculateScrollProgress()\r\n})\r\n\r\nonUnmounted(() => {\r\n  // 移除滚动监听\r\n  window.removeEventListener(\'scroll\', handleScroll)\r\n})\r\n\r\n</script>\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n        \r\n',7,8,1,1,'2026-01-04 22:52:24','2026-01-04 22:52:24','2026-01-07 18:15:00','','5'),(72,'《苍穹外卖》设计模式：',' 《苍穹外卖》设计模式：\r\n\r\n 1,工厂模式：\r\n\r\n    Spring IoC容器管理所有Bean\r\n\r\n- 模式分析：\r\n  \r\n  - 产品：OrderService接口。\r\n  \r\n  - 具体产品：OrderServiceImpl类。\r.....','## 《苍穹外卖》设计模式：\r\n\r\n### 1,工厂模式：\r\n\r\n  **  Spring IoC容器管理所有Bean**\r\n\r\n- **模式分析**：\r\n  \r\n  - **产品**：`OrderService`接口。\r\n  \r\n  - **具体产品**：`OrderServiceImpl`类。\r\n  \r\n  - **工厂**：**Spring IoC容器**充当了抽象工厂和具体工厂的角色。\r\n  \r\n  - **客户端**：所有通过`@Autowired`注入`OrderService`的类（如`OrderController`）。\r\n\r\n- **好处**：\r\n  \r\n  - **控制反转**：将对象的创建权从程序员手中移交给了Spring容器，极大降低了耦合度。\r\n  \r\n  - **易于测试**：可以轻松地注入一个Mock的实现来进行单元测试。\r\n  \r\n  - **符合开闭原则**：要替换实现（例如升级为`OrderServiceV2Impl`），只需修改注解或配置，而无需修改客户端代码。\r\n  \r\n  ****\r\n  \r\n  ### 2，建造者模式：\r\n  \r\n  **Lombok `@Builder`创建DTO/VO**\r\n  \r\n  **好处**：\r\n  \r\n  - **代码可读性**：链式调用非常清晰，一眼就能看出对象的构造过程。\r\n  \r\n  - **解决重叠构造器问题**：对于有大量可选字段的对象，无需编写多个参数列表不同的构造器。\r\n  \r\n  - **创建不可变对象**：可以轻松创建`final`对象，保证线程安全。\r\n\r\n****\r\n\r\n### 3，代理模式：\r\n\r\n       **Spring AOP实现事务、日志**, 例如：**事务管理**（`@Transactional`）、**全局异常处理**  （`@RestControllerAdvice`）\r\n\r\n       **好处**：\r\n\r\n- **职责分离**：事务管理、日志记录、安全检查等**横切关注点**与核心业务逻辑完全分离。\r\n\r\n- **无侵入性**：不需要修改原有的业务代码，通过注解即可增强功能。\r\n  \r\n  ******\r\n  \r\n  ### 4 ,  模板方法模式：\r\n  \r\n  **支付回调流程**\r\n  \r\n  **好处**：\r\n  \r\n  **消除大量if-else**，代码结构清晰。\r\n  \r\n  **符合开闭原则**：新增一种支付方式，只需新增一个子类，无需修改原有模板逻辑。\r\n  \r\n  ****\r\n  \r\n  ### 5，策略模式：\r\n  \r\n  **订单状态处理**，例如:  **订单状态机**。订单从“待支付”到“待接单”到“配送中”等状态变迁，每个变迁都需要不同的校验和操作。\r\n\r\n       **好处**：\r\n\r\n       **将复杂的状态处理逻辑分解到独立的类中**，每个类职责单一。\r\n\r\n       **易于扩展**：新增状态处理逻辑只需添加新的策略类。\r\n\r\n\r\n',7,3,1,1,'2026-01-04 22:53:06','2026-01-04 22:53:06','2026-01-08 22:50:00','','1'),(73,'Vue2学习笔记：',' Vue2学习笔记：\r\n\r\n 一，常用指令：\r\n\r\n> 内容渲染指令\r\n> \r\n> - v-text：将数据渲染为文本内容，会覆盖元素原有内容\r\n> - v-html：将数据渲染为HTML结构，能解析HTML标签但存在XSS风险\r\n>   条件渲染指令\r\n> - v-if：通过动态创建/移除D.....','# Vue2学习笔记：\r\n\r\n## 一，常用指令：\r\n\r\n> 内容渲染指令\r\n> \r\n> - v-text：将数据渲染为文本内容，会覆盖元素原有内容\r\n> - v-html：将数据渲染为HTML结构，能解析HTML标签但存在XSS风险\r\n>   条件渲染指令\r\n> - v-if：通过动态创建/移除DOM元素来控制显示隐藏，适合不频繁切换的场景\r\n> - v-show：通过切换display:none控制显示隐藏，元素始终存在于DOM中，适合频繁切换场景\r\n> - v-else-if、v-else：辅助v-if进行多条件判断渲染\r\n>   列表渲染指令\r\n> - v-for：基于数组或对象循环渲染列表结构，需要为每个元素提供唯一key标识\r\n>   事件绑定指令\r\n> - v-on：绑定DOM事件监听器，可简写为@符号，支持事件修饰符如.stop（阻止冒泡）、.prevent（阻止默认行为）等\r\n>   属性绑定指令\r\n> - v-bind：动态绑定HTML属性，可简写为:符号，常用于绑定class、style、src等属性双向绑定指令\r\n> - v-model：在表单元素上实现双向数据绑定，支持修饰符如.number（转数字）、.trim（去除空格）等其他常用指令\r\n> \r\n> <img title=\"\" src=\"file:///C:/Users/33313/AppData/Roaming/marktext/images/2025-12-10-20-14-52-image.png\" alt=\"\" width=\"571\" data-align=\"inline\">\r\n\r\n> - v-pre：跳过元素和子元素的编译过程\r\n> - v-cloak：解决插值语法闪烁问题  \r\n> - v-once：元素只渲染一次，后续数据变化不更新\r\n> \r\n> \r\n',7,14,1,1,'2026-01-05 18:17:20','2026-01-05 18:17:20','2026-01-08 22:50:00','','5'),(74,'页面滚动进度条',' 页面滚动进度条\r\n\r\n<img src=\"file:///C:/Users/33313/AppData/Roaming/marktext/images/2025-12-27-18-23-12-image.png\" title=\"\" alt=\"\" width=\"701\">\r\n\r\n> 随着页面的.....','### 页面滚动进度条\r\n\r\n<img src=\"file:///C:/Users/33313/AppData/Roaming/marktext/images/2025-12-27-18-23-12-image.png\" title=\"\" alt=\"\" width=\"701\">\r\n\r\n> 随着页面的滚动而自行加减\r\n\r\n如何设置页面滚动进度条：\r\n\r\n组件：<  el-progress />\r\n\r\n上代码：\r\n\r\n> <template>\r\n>     <!-- 页面滚动进度条 -->\r\n>     <el-progress\r\n>       :percentage=\"scrollProgress\"\r\n>       :stroke-width=\"2.5\"\r\n>       :show-text=\"false\"\r\n>       class=\"scroll-progress\"\r\n>       status=\"success\"\r\n>     />\r\n>   </div>\r\n> </template>\r\n> \r\n> \r\n\r\n<template>\r\n    <!-- 页面滚动进度条 -->\r\n    <el-progress\r\n      :percentage=\"scrollProgress\"\r\n      :stroke-width=\"2.5\"\r\n      :show-text=\"false\"\r\n      class=\"scroll-progress\"\r\n      status=\"success\"\r\n    />\r\n</template>\r\n\r\n<template>\r\n\r\n\r\n\r\n\r\n\r\n\r\n</template>\r\n\r\n<template>\r\n    <el-progress\r\n      :percentage=\"scrollProgress\"\r\n      :stroke-width=\"2.5\"\r\n      :show-text=\"false\"\r\n      class=\"scroll-progress\"\r\n      status=\"success\"\r\n    />\r\n</template>\r\n\r\n>     <el-progress\r\n> \r\n>       :percentage=\"scrollProgress\"\r\n> \r\n>       :stroke-width=\"2.5\"\r\n> \r\n>       :show-text=\"false\"\r\n> \r\n>       class=\"scroll-progress\"\r\n> \r\n>       status=\"success\"\r\n> \r\n>     />\r\n\r\n        \r\n\r\n<script>\r\nconst scrollProgress = ref(0)\r\n\r\n// 计算页面滚动进度\r\nconst calculateScrollProgress = () => {\r\n  const windowHeight = window.innerHeight\r\n  const documentHeight = document.documentElement.scrollHeight\r\n  const scrollTop = window.pageYOffset || document.documentElement.scrollTop\r\n  const scrollableHeight = documentHeight - windowHeight\r\n  const progress = scrollableHeight > 0 ? (scrollTop / scrollableHeight) * 100 : 0\r\n  scrollProgress.value = Math.min(Math.max(progress, 0), 100)\r\n} \r\n// 监听滚动事件\r\nconst handleScroll = () => {\r\n  calculateScrollProgress()\r\n}\r\n\r\nonMounted(() => {\r\n  // 监听页面滚动\r\n  window.addEventListener(\'scroll\', handleScroll)\r\n  // 初始化计算一次\r\n  calculateScrollProgress()\r\n})\r\n\r\nonUnmounted(() => {\r\n  // 移除滚动监听\r\n  window.removeEventListener(\'scroll\', handleScroll)\r\n})\r\n\r\n</script>\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n        \r\n',7,16,1,0,'2026-01-08 22:39:55','2026-01-06 22:23:38','2026-01-08 22:45:00','','5'),(75,'Spring的原理',' Spring的底层原理：\r\n\r\n 一，Spring的概念：\r\n\r\nSpring框架是一个开源的Java应用程序框架，主要用于管理Java对象的生命周期和依赖关系，简化开发过程。\r\n\r\n\r\n\r\n 核心底层逻辑：IoC 与 AOP\r\n\r\nSpring 框架的基石是两个核心概念：\r\n\r\n1......','# Spring的底层原理：\r\n\r\n## 一，Spring的概念：\r\n\r\nSpring框架是一个开源的Java应用程序框架，主要用于管理Java对象的生命周期和依赖关系，简化开发过程。\r\n\r\n\r\n\r\n### 核心底层逻辑：IoC 与 AOP\r\n\r\nSpring 框架的基石是两个核心概念：\r\n\r\n1. **控制反转 (IoC)**\r\n\r\n2. **面向切面编程 (AOP)**\r\n\r\n#### 1. 控制反转 (IoC) 与依赖注入 (DI)\r\n\r\n- **逻辑（是什么和为什么）：**\r\n  \r\n  - **传统方式：** 在传统的程序设计中，对象A如果需要使用对象B，会由A内部通过 `new` 关键字主动创建B的实例。这意味着A对B有绝对的**控制权**，两者紧密**耦合**在一起。\r\n  \r\n  - **IoC 方式：** IoC 将创建和组装对象的控制权从应用程序代码中“反转”到了一个外部容器（即 Spring IoC 容器）。对象A不再负责创建B，而是通过**依赖注入 (DI)** 的方式，由容器在运行时将B的实例“注入”到A中。DI 是 IoC 原则最典型的实现方式。\r\n  \r\n  - **目的：** 实现对象之间的**解耦**。对象只关注自身的业务逻辑，而不关心依赖从何而来、如何创建，这使得代码更加灵活、可测试、可维护。\r\n\r\n- **实现过程（怎么做）：**  \r\n  IoC 容器的实现过程，本质上就是 **Bean 的生命周期管理**。这个过程可以概括为以下几个关键步骤：\r\n\r\n    **a. 配置元数据 (Configuration Metadata)**\r\n\r\n- 容器需要知道要创建哪些对象（Bean），以及这些对象之间的依赖关系。这些信息称为“配置元数据”。\r\n\r\n- **实现方式：**\r\n  \r\n  - **XML 配置文件：** 早期的标准方式，在 `.xml` 文件中定义 `<bean>` 标签。\r\n  \r\n  - **注解 (Annotation)：** 现代主流方式，使用如 `@Component`, `@Service`, `@Autowired` 等注解。\r\n  \r\n  - **Java 配置类 (Java Configuration)：** 使用 `@Configuration` 和 `@Bean` 注解在 Java 类中定义配置。\r\n\r\n  **b. 容器的启动与初始化**\r\n\r\n- 应用程序启动时，会创建 Spring IoC 容器（最常用的是 `ApplicationContext`）。\r\n\r\n- 容器会读取、解析上述的配置元数据。\r\n\r\n  **c. Bean 的实例化**\r\n\r\n- 根据配置信息，容器通过**反射 (Reflection)** 机制，使用类的无参构造函数或工厂方法来创建 Bean 的实例。例如：`Class.forName(\"com.example.MyClass\").newInstance()`。\r\n\r\n  **d. 依赖注入**\r\n\r\n- 容器分析 Bean 之间的依赖关系（通过构造函数参数、属性 Setter 方法或字段上的 `@Autowired` 注解识别）。\r\n\r\n- 容器将其依赖的 Bean 实例注入到目标 Bean 中。这通常也是通过反射调用 Setter 方法或直接设置字段值来实现的。\r\n\r\n  **e. Bean 生命周期的后置处理**\r\n\r\n- 这是 Spring 提供强大扩展能力的关键。容器允许开发者“介入” Bean 的创建过程。\r\n\r\n- **BeanPostProcessor 接口：** 这是一个核心接口。容器在完成依赖注入后，会遍历所有 `BeanPostProcessor`，调用其 `postProcessBeforeInitialization` 和 `postProcessAfterInitialization` 方法。AOP 的动态代理就是在这个阶段创建的！\r\n\r\n- **生命周期回调：** 如果 Bean 实现了 `InitializingBean` 接口或定义了 `init-method`，容器会调用这些初始化方法。同样，如果实现了 `DisposableBean` 接口或定义了 `destroy-method`，在容器关闭时会被调用。\r\n\r\n  **f. 将完整的 Bean 放入容器缓存**\r\n\r\n- 经过上述步骤后，一个完整的、可用的 Bean 被放入一个名为“单例缓存”的 Map 中。后续每次请求该 Bean 时，容器都会从这个缓存中返回同一个实例（默认单例作用域下）。\r\n\r\n- ****\r\n\r\n#### 2. 面向切面编程 (AOP)\r\n\r\n- **逻辑（是什么和为什么）：**\r\n  \r\n  - 在业务系统中，有很多横跨多个模块的功能点，如日志记录、事务管理、安全校验等。这些功能被称为“横切关注点”。\r\n  \r\n  - 如果将这些代码直接嵌入到业务逻辑中，会导致代码混乱、冗余且难以维护。\r\n  \r\n  - AOP 允许我们将这些横切关注点模块化为独立的“切面 (Aspect)”，然后通过“织入 (Weaving)”的方式，在运行时将它们动态地应用到目标方法上。这样，业务代码可以保持纯净。\r\n\r\n- **实现过程：**   \r\n  Spring AOP 默认使用**动态代理** 技术来实现。\r\n  \r\n  **a. 找到匹配的切点**\r\n  \r\n  - 根据配置的切点表达式（如 `@Pointcut(\"execution(* com.example.service.*.*(..))\")`），找到所有需要被增强的 Bean 和方法。\r\n\r\n  **b. 创建代理对象**\r\n\r\n- 当 Bean 的初始化进行到 `BeanPostProcessor` 的后置处理阶段时，Spring 会检查当前 Bean 是否匹配某个切点。\r\n\r\n- **如果匹配：** Spring 不会将原始的 Bean 实例放入容器，而是为其创建一个**代理对象**。\r\n\r\n- **代理方式：**\r\n  \r\n  - **JDK 动态代理：** 如果目标类实现了至少一个接口，Spring 默认使用 JDK 动态代理。代理对象会实现相同的接口。\r\n  \r\n  - **CGLIB 代理：** 如果目标类没有实现任何接口，Spring 会使用 CGLIB 库生成一个目标类的子类作为代理。\r\n\r\n  **c. 方法调用与拦截器链**\r\n\r\n- 当客户端代码调用代理对象的方法时，调用会被代理对象拦截。\r\n\r\n- 代理对象会创建一个**方法调用链（拦截器链）**，这个链包含了通知（Advice，如 `@Before`, `@After` 等）的逻辑。\r\n\r\n- 代理对象会按顺序执行这个调用链，从而将切面的逻辑动态地“织入”到原始方法执行的周围。\r\n\r\n****\r\n\r\n## 二，Spring容器：\r\n\r\n#### 1，概念：\r\n\r\n    简单来说，**Spring 容器就是一个管理应用程序中所有组件（称为 Bean）的“大管家”**。\r\n\r\n   它负责：\r\n\r\n1. **创建对象：** 不再使用 `new` 关键字，而是由容器来创建。\r\n\r\n2. **组装对象：** 解决对象之间的依赖关系（谁需要谁）。\r\n\r\n3. **管理生命周期：** 控制对象的生老病死（何时创建、初始化、销毁）。\r\n\r\n4. **提供配置：** 提供一个统一的配置方式来定义这些组件。\r\n\r\n它的核心思想是 **IoC（控制反转）**，即把创建和组装对象的控制权从应用程序代码中“反转”给了容器。**依赖注入（DI）** 是实现 IoC 的主要技术手段。\r\n\r\n\r\n\r\n\r\n\r\n#### 2，Spring容器的类型：\r\n\r\nSpring 提供了两种风格的容器，它们有共同的根接口 `BeanFactory`，但在功能上有差异。\r\n\r\n1. `BeanFactory`（基础容器）\r\n\r\n- **接口位置：** `org.springframework.beans.factory.BeanFactory`\r\n\r\n- **特点：**\r\n  \r\n  - 这是一个**最基础、最底层的 IoC 容器接口**。\r\n  \r\n  - 它实现了容器的基本功能：加载配置、创建和管理 Bean。\r\n  \r\n  - 它采用了 **“懒加载”** 模式。即，只有当客户端真正请求某个 Bean 时（如调用 `getBean()` 方法），它才会对该 Bean 进行实例化和依赖注入。\r\n\r\n- **适用场景：** 在资源非常受限的环境下（如移动设备、Applet），为了节省内存和启动时间，可能会直接使用 `BeanFactory`。但在现代企业级应用中，很少直接使用。\r\n\r\n\r\n\r\n\r\n\r\n2. `ApplicationContext`（高级容器，推荐使用）\r\n\r\n- **接口位置：** `org.springframework.context.ApplicationContext`\r\n\r\n- **特点：**\r\n  \r\n  - 它是 `BeanFactory` 的**子接口**，意味着它包含了 `BeanFactory` 的所有功能，并在此基础上进行了大量**增强**。\r\n  \r\n  - 它提供了更多企业级的功能，因此也常被称为“Spring 上下文”。\r\n  \r\n  - 它默认采用 **“急切的预加载”** 模式。在容器启动时，就会创建并配置所有单例作用域的 Bean（非懒加载的）。这有助于在应用启动时就发现配置错误，而不是在运行时。\r\n\r\n- **为什么推荐使用 `ApplicationContext`？**  \r\n  因为它提供了 `BeanFactory` 所没有的便利功能：\r\n  \r\n  - **与 Spring AOP 集成：** 无缝支持面向切面编程。\r\n  \r\n  - **消息资源处理（国际化）：** 支持 `MessageSource`，方便处理 i18n。\r\n  \r\n  - **事件发布机制：** 支持 `ApplicationEvent` 和 `ApplicationListener`，用于 Bean 之间的解耦通信。\r\n  \r\n  - **便捷的访问资源：** 更容易访问各种资源（如 URL 和文件）。\r\n  \r\n  - **继承性：** 可以有多个上下文，并通过父上下文来共享配置。\r\n\r\n\r\n\r\n\r\n\r\n**结论：** 在几乎所有情况下，我们都应该使用 `ApplicationContext`。`BeanFactory` 通常只用于底层扩展或非常特殊的环境。\r\n\r\n****\r\n\r\n## 三，Spring的核心模块：\r\n\r\nSpring框架由多个模块组成，主要包括：\r\n\r\n1. **Spring Core**：提供核心工具类和IoC容器。\r\n\r\n2. **Spring AOP**：提供面向切面的编程支持。\r\n\r\n3. **Spring ORM**：提供与ORM框架的集成支持。\r\n\r\n4. **Spring DAO**：提供数据访问对象的支持。\r\n\r\n5. **Spring Context**：提供上下文信息和事件传播支持。\r\n\r\n6. **Spring Web**：提供Web应用程序的支持。\r\n\r\n7. **Spring Web MVC**：提供Web MVC框架。\r\n\r\n****\r\n\r\n## 四，Bean的生命周期\r\n\r\nSpring容器管理Bean的生命周期，包括创建、初始化和销毁。Bean的生命周期由以下几个步骤组成：\r\n\r\n1. **实例化**：通过构造器或工厂方法创建Bean实例。\r\n\r\n2. **属性设置**：通过依赖注入设置Bean的属性。\r\n\r\n3. **初始化**：调用初始化方法（如*init-method*或*@PostConstruct*）。\r\n\r\n4. **销毁**：调用销毁方法（如*destroy-method*或*@PreDestroy*）。\r\n\r\n\r\n\r\n![](E:\\deepseek_mermaid_20250924_04246a.png)\r\n\r\n\r\n\r\n***\r\n\r\n## 五，事件机制：\r\n\r\nSpring的事件机制基于观察者设计模式，通过**ApplicationEvent**类和**ApplicationListener**接口实现。Spring容器中的事件可以是容器事件（如上下文刷新事件）或自定义事件。\r\n\r\n****\r\n\r\n## 六 、总结：Spring 应用启动的宏观实现过程\r\n\r\n结合 IoC 和 AOP，一个典型的 Spring 应用启动流程如下：\r\n\r\n1. **启动容器：** 初始化 `ApplicationContext`（例如 `AnnotationConfigApplicationContext`）。\r\n\r\n2. **扫描加载：** 容器扫描指定的包路径，解析所有带有 `@Component`, `@Service`, `@Configuration` 等注解的类，将它们转化为 `BeanDefinition`（Bean 的定义信息），并注册到容器中。\r\n\r\n3. **实例化 Bean：** 容器根据 `BeanDefinition`，通过反射创建 Bean 的原始实例。\r\n\r\n4. **依赖注入：** 容器分析 `@Autowired` 等注解，将依赖的 Bean 注入到目标 Bean 中。\r\n\r\n5. **AOP 代理：** **`BeanPostProcessor` 开始工作**。特别是 `AnnotationAwareAspectJAutoProxyCreator` 这个后置处理器，它会检查当前 Bean 是否需要被 AOP 增强。如果需要，就用动态代理替换掉原始的 Bean 实例。\r\n\r\n6. **初始化：** 调用 Bean 的初始化回调方法。\r\n\r\n7. **就绪：** 完整的 Bean（可能是代理对象）被放入单例缓存，应用程序可以开始使用它们处理请求。\r\n\r\n### 核心底层技术栈\r\n\r\n- **反射 (Reflection)：** 实现 Bean 实例化、依赖注入的基础。\r\n\r\n- **动态代理 (Dynamic Proxy)：** 实现 AOP 的核心。\r\n\r\n- **设计模式：** 大量使用了工厂模式、模板方法模式、代理模式、策略模式等。\r\n  \r\n  - **工厂模式：** `ApplicationContext` 本身就是一个 Bean 工厂。\r\n  \r\n  - **模板方法：** 在 `BeanPostProcessor`、事务管理等地方广泛应用，定义了骨架，允许子类扩展特定步骤。\r\n\r\n- **XML/注解解析器：** 用于解析配置信息。\r\n\r\n简单来说，**Spring 的底层逻辑是通过 IoC 容器管理对象的生命周期和依赖关系，通过 AOP 动态代理增强对象功能，其实现过程依赖于反射、动态代理等Java核心机制和一系列精妙的设计模式。** 这种设计使得开发者能够专注于业务逻辑，而框架负责处理所有复杂的基础设施问题。\r\n',6,14,1,1,'2026-01-08 22:39:30','2026-01-07 19:55:33','2026-01-09 03:15:00','','8');
/*!40000 ALTER TABLE `article` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `article_count` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (1,'project','2025-10-20 19:11:34','2025-12-25 21:25:29',NULL),(5,'Mood Writing','2025-10-25 17:11:16','2025-12-31 21:57:38',NULL),(6,'Tech Digest','2025-10-26 17:10:30','2025-12-25 21:24:19',NULL),(7,'notes','2025-12-21 22:57:49','2025-12-31 21:33:43',NULL);
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `nickname` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '昵称',
  `email` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `website` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '网站',
  `avatar` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像地址',
  `ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'IP地址',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评论内容',
  `blog_id` bigint DEFAULT NULL COMMENT '文章ID',
  `status` tinyint DEFAULT '1' COMMENT '是否公开：0-否，1-是',
  `notice` tinyint(1) DEFAULT '0' COMMENT '邮件提醒：0-否，1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `Title` varchar(388) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `adminComment` tinyint(1) DEFAULT NULL,
  `parentCommentId` mediumtext COLLATE utf8mb4_unicode_ci,
  `page` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '所在页面：0普通文章，1友链页面',
  PRIMARY KEY (`id`),
  KEY `idx_article_id` (`blog_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=176 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `friend_link`
--

DROP TABLE IF EXISTS `friend_link`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `friend_link` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '友链ID',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '网站名称',
  `url` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '网站URL',
  `logo` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '网站头像/Logo',
  `description` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '网站描述',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `view_count` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='友链表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `friend_link`
--

LOCK TABLES `friend_link` WRITE;
/*!40000 ALTER TABLE `friend_link` DISABLE KEYS */;
/*!40000 ALTER TABLE `friend_link` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `moment`
--

DROP TABLE IF EXISTS `moment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `moment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '动态ID',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '动态内容',
  `image` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '动态图片（多个图片用逗号分隔）',
  `is_public` tinyint DEFAULT '1' COMMENT '是否公开：0-私密，1-公开',
  `status` tinyint DEFAULT '1' COMMENT '动态状态：0-草稿，1-已发布',
  `view_count` int DEFAULT '0' COMMENT '浏览次数',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_is_public` (`is_public`),
  KEY `idx_publish_time` (`publish_time`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='动态表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `moment`
--

LOCK TABLES `moment` WRITE;
/*!40000 ALTER TABLE `moment` DISABLE KEYS */;
INSERT INTO `moment` VALUES (3,'正在学习新技术，加油！💪','',1,0,15,'2025-10-24 20:00:00','2025-10-24 20:00:00','2025-11-02 23:04:31'),(16,'这是我的第一条动态！','/images/fcc846c9-7ae8-4061-ac56-71e0a603568b.png,/images/266c188f-0701-4c72-99a2-0dd750906370.png,/images/c51fd5aa-526d-4c94-8da1-f9bf5f281edc.png',1,1,0,'2026-01-08 16:25:51','2026-01-08 16:25:51','2026-01-08 16:25:51'),(17,'hello world！','/images/5fe52f8f-c3d5-46a5-80a8-f4764e681c23.png',1,1,0,'2026-01-08 16:26:55','2026-01-08 16:26:55','2026-01-08 16:26:55'),(27,'SDA ','/images/2b1cb867-b0dc-4ae1-bdf4-7bd55ad35ea1.png',1,1,0,'2026-01-08 20:48:23','2026-01-08 20:48:23','2026-01-08 20:48:23');
/*!40000 ALTER TABLE `moment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `operation_log`
--

DROP TABLE IF EXISTS `operation_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `module` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作模块',
  `type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作类型：CREATE-新增，UPDATE-修改，DELETE-删除，QUERY-查询',
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作描述',
  `request_method` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求方法：GET/POST/PUT/DELETE',
  `request_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求URL',
  `request_params` text COLLATE utf8mb4_unicode_ci COMMENT '请求参数（JSON）',
  `response_data` text COLLATE utf8mb4_unicode_ci COMMENT '响应数据（JSON）',
  `user_id` bigint DEFAULT NULL COMMENT '操作用户ID',
  `username` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作用户名',
  `ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'IP地址',
  `user_agent` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户代理',
  `execution_time` int DEFAULT NULL COMMENT '执行时间（毫秒）',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-失败，1-成功',
  `error_message` text COLLATE utf8mb4_unicode_ci COMMENT '错误信息',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `operation_log`
--

LOCK TABLES `operation_log` WRITE;
/*!40000 ALTER TABLE `operation_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `operation_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_config`
--

DROP TABLE IF EXISTS `system_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置键',
  `config_value` text COLLATE utf8mb4_unicode_ci COMMENT '配置值',
  `config_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'STRING' COMMENT '配置类型：STRING/NUMBER/BOOLEAN/JSON',
  `description` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '配置描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_config`
--

LOCK TABLES `system_config` WRITE;
/*!40000 ALTER TABLE `system_config` DISABLE KEYS */;
INSERT INTO `system_config` VALUES (1,'site.name','Eleven 博客','STRING','网站名称','2025-10-20 19:11:35','2025-10-20 19:11:35'),(2,'site.description','一个简洁优雅的博客系统','STRING','网站描述','2025-10-20 19:11:35','2025-10-20 19:11:35'),(3,'site.keywords','Eleven,博客,Spring Boot','STRING','网站关键词','2025-10-20 19:11:35','2025-10-20 19:11:35'),(4,'site.author','Eleven','STRING','网站作者','2025-10-20 19:11:35','2025-10-20 19:11:35'),(5,'site.icp','','STRING','ICP备案号','2025-10-20 19:11:35','2025-10-20 19:11:35'),(6,'comment.need_audit','false','BOOLEAN','评论是否需要审核','2025-10-20 19:11:35','2025-10-20 19:11:35'),(7,'comment.allow_guest','true','BOOLEAN','是否允许游客评论','2025-10-20 19:11:35','2025-10-20 19:11:35'),(8,'upload.max_size','10485760','NUMBER','上传文件最大大小（字节）','2025-10-20 19:11:35','2025-10-20 19:11:35'),(9,'upload.allowed_types','jpg,jpeg,png,gif,webp','STRING','允许上传的文件类型','2025-10-20 19:11:35','2025-10-20 19:11:35');
/*!40000 ALTER TABLE `system_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tags`
--

DROP TABLE IF EXISTS `tags`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tags` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名称',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `article_count` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tags`
--

LOCK TABLES `tags` WRITE;
/*!40000 ALTER TABLE `tags` DISABLE KEYS */;
INSERT INTO `tags` VALUES (1,'Java','2025-10-20 19:11:34','2026-01-08 21:03:14',NULL),(2,'Spring Boot','2025-10-20 19:11:34','2025-10-20 19:11:34',NULL),(3,'MySQL','2025-10-20 19:11:34','2025-12-31 21:57:24',NULL),(5,'JavaScript','2025-10-20 19:11:34','2025-10-20 19:11:34',NULL),(6,'Python','2025-10-20 19:11:34','2025-10-20 19:11:34',NULL),(7,'Docker','2025-10-20 19:11:34','2025-10-20 19:11:34',NULL),(8,'Linux','2025-10-20 19:11:34','2025-10-25 20:18:30',NULL),(15,'前端','2026-01-07 19:41:26','2026-01-07 19:43:42',NULL);
/*!40000 ALTER TABLE `tags` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码（加密）',
  `nickname` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '昵称',
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像URL',
  `role` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'USER' COMMENT '角色：ADMIN-管理员，USER-普通用户',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最后登录IP',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','123456','管理员',NULL,NULL,'ADMIN',1,NULL,NULL,'2025-10-20 19:11:34','2025-10-20 19:11:34');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-09 21:56:46
