## Spring Expresion Language(SpEL)

<b>简介：</b>SpEL,和struts2的OGNL一样都是表达式语言，但是SpEL更强大，SpEL是独立的，不一定需要绑定至Spring或相关产品使用，SpEL也可以和其他表达式语言仪器使用，但是需要整合之后使用。

## 1 表达式语言具有以下功能：

- Literal expressions 文字表达方式

- Boolean and relational operators 布尔值和关系表达式

- Regular expressions  正则表达式

- Class expressions 类表达方式

- Accessing properties, arrays, lists, maps  读取数组和集合等的属性
 
- Method invocation  方法调用

- Relational operators 关系运算符

- Assignment 任务，分配

- Calling constructors 调用构造

- Bean references bean的引用

- Array construction 数组构造

- Inline lists 内联集合

- Inline maps 内联map

- Ternary operator 三元运算符

- Variables 变量

- User defined functions 用户自定义方法
 
- Collection projection  

- Collection selection  集合选择器

- Templated expressions 模板化表达式

## 2 使用Spring的`Expression `接口进行表达式计算。
这部分介绍了`SpEL API`的简单使用，以及表达式语法，下面的代码介绍了文本字符串表达式`"Hello World"`的表示：

	ExpressionParser parser = new SpelExpressionParser();
	Expression exp = parser.parseExpression("'Hello World'");
	String message = (String) exp.getValue();
`message`得到的就是简单的`"Hello World"`

下面是一个调用`javaBean`属性的例子，`String`的属性可以如下方式调用：

	ExpressionParser parser = new SpelExpressionParser();
	
	// invokes 'getBytes()'
	Expression exp = parser.parseExpression("'Hello World'.bytes");
	byte[] bytes = (byte[]) exp.getValue();

SpEL也支持点符号的嵌套调用，如A.B.C，公共属性也可以被访问：

	ExpressionParser parser = new SpelExpressionParser();
	
	// invokes 'getBytes().length'
	Expression exp = parser.parseExpression("'Hello World'.bytes.length");
	int length = (Integer) exp.getValue();

除了直接使用字符串，还可以使用字符串构造

	ExpressionParser parser = new SpelExpressionParser();
	Expression exp = parser.parseExpression("new String('hello world').toUpperCase()");
	String message = exp.getValue(String.class);

如果使用`public <T> T getValue(Class<T> desiredResultType`，不能正常进行类型转换，会抛出 `EvaluationException ` 的异常。

 一种更通用的SpEL使用方式是，提供一个表达式字符串，它是根据特定的对象实例进行评估的（成为根对象），这有两种选项可供选择，具体选择哪一种取决于表达式所评估的对象，是否会随着每次调用而改变。下面这个例子，我们从创建的类中读取属性：

	// Create and set a calendar
	GregorianCalendar c = new GregorianCalendar();
	c.set(1856, 7, 9);
	
	// The constructor arguments are name, birthday, and nationality.
	Inventor tesla = new Inventor("Nikola Tesla", c.getTime(), "Serbian");
	
	ExpressionParser parser = new SpelExpressionParser();
	Expression exp = parser.parseExpression("name");
	
	EvaluationContext context = new StandardEvaluationContext(tesla);
	String name = (String) exp.getValue(context);

使用 `StandardEvaluationContext ` 类，你可以指定某个具体的属性，这就是根对象不会轻易改变的方式。如果，根目录可能经常改变，只要简单的设置评估上下文，他可以在每次调用 `getValue()` 的时候提供，下面是一个例子：

	// Create and set a calendar
	GregorianCalendar c = new GregorianCalendar();
	c.set(1856, 7, 9);
	
	// The constructor arguments are name, birthday, and nationality.
	Inventor tesla = new Inventor("Nikola Tesla", c.getTime(), "Serbian");
	
	ExpressionParser parser = new SpelExpressionParser();
	Expression exp = parser.parseExpression("name");
	String name = (String) exp.getValue(tesla);

在某些情况下，使用配置的评估上下文是可取的，但在每次调用getValue时仍然提供不同的根对象。getValue允许在同一调用中指定两者。在这些情况下，传递给调用的根对象被认为覆盖了在评估上下文中指定的任何对象（可能是null）。

在单独使用SpEL时，需要创建解析器、解析表达式，并可能提供评估上下文和根上下文对象。然而，更常见的用法是只提供SpEL表达式字符串作为配置文件的一部分，例如Spring bean或Spring Web Flow定义。在这种情况下，解析器、评估上下文、根对象和任何预定义的变量都是隐式地设置的，要求用户只指定表达式以外的任何东西。

### 2.1 EvaluationContext 接口
当要用到表达式来操作属性、字段、方法和类型转换等时，需要用到 `EvaluationContext` 这个接口，这有个开箱即用的实现类 `StandardEvaluationContext` ；利用反射来操作对象，缓存 `java.lang.reflect.Method`, `java.lang.reflect.Field`, and `java.lang.reflect.Constructor` 以提高性能。

`StandardEvaluationContext` 中， 你可能需要通过 `setRootObject()` 方法来指定根对象，或者你可以把根对象放入它的构造；也可以通过 `setVariable()` 和 `registerFunction()` 来指定要操作的变量或者方法。

默认的SpEL类型转换服务可以在 `Spring core` (`org.springframework.core.convert.ConversionService`) 中获取，这个转换服务提供了很多通用的转换器构造，但是也支持扩展，它还有更关键的能力，就是它有泛型意识，当表达式中还有泛型时，它会尝试转换到合适的类型。

具体是什么意思呢？假设有这样一个场景，我们要使用 `setValue()` 来传入一个 `List` 其实，需要的是一个 `List<Boolean>` ，SpEL 就能意识到，这需要一个 `boolean` ，并会在赋值前转换成 `boolean` .下面是一个简单的例子：

	class Simple {
	    public List<Boolean> booleanList = new ArrayList<Boolean>();
	}
	
	Simple simple = new Simple();
	
	simple.booleanList.add(true);
	
	StandardEvaluationContext simpleContext = new StandardEvaluationContext(simple);
	
	// false is passed in here as a string. SpEL and the conversion service will
	// correctly recognize that it needs to be a Boolean and convert it
	parser.parseExpression("booleanList[0]").setValue(simpleContext, "false");
	
	// b will be false
	Boolean b = simple.booleanList.get(0);

### 2.2 解析器配置
可以使用解析器配置对象来配置SpEL表达式（`org.springframework.expression.spel.SpelParserConfiguration`）, 这个配置对象可以控制某些表达式组件的行为。 例如，当检索一个不存在的集合或数组索引时，就可能自动创建这个索引，这在使用由一系列属性引用组成的表达式时非常有用，或者检索一个超出数组或者集合范围的元素时，自动增加数组或集合以适应该索引。

	class Demo {
	    public List<String> list;
	}
	
	// Turn on:
	// - auto null reference initialization
	// - auto collection growing
	SpelParserConfiguration config = new SpelParserConfiguration(true,true);
	
	ExpressionParser parser = new SpelExpressionParser(config);
	
	Expression expression = parser.parseExpression("list[3]");
	
	Demo demo = new Demo();
	
	Object o = expression.getValue(demo);
	
	// demo.list will now be a real collection of 4 entries
	// Each entry is a new empty String

还可以配置SpEL表达式编译器的行为

## 3 SpEL 定义bean定义的表达式支持
SpEL 可以使用xml配置方式也可以使用注解方式来定义 `BeanDefinitions` ，这两种方式都是使用这样的格式 #{...}

### 3.1 以xml基础的配置
属性或者构造参数的 value 值可以使用以下方式使用表达式:

	<bean id="numberGuess" class="org.spring.samples.NumberGuess">
	    <property name="randomNumber" value="#{ T(java.lang.Math).random() * 100.0 }"/>
	
	    <!-- other properties -->
	</bean>

`systemProperties ` 的值是预定义的，所以你可以在表达式中直接使用它，你不一定需要在这个上下文中使用 # 前缀：

<bean id="taxCalculator" class="org.spring.samples.TaxCalculator">
    <property name="defaultLocale" value="#{ systemProperties['user.region'] }"/>

    <!-- other properties -->
</bean>

你也可以引用其他 bean 的属性，如：

	<bean id="numberGuess" class="org.spring.samples.NumberGuess">
	    <property name="randomNumber" value="#{ T(java.lang.Math).random() * 100.0 }"/>
	
	    <!-- other properties -->
	</bean>
	
	<bean id="shapeGuess" class="org.spring.samples.ShapeGuess">
	    <property name="initialShapeSeed" value="#{ numberGuess.randomNumber }"/>
	
	    <!-- other properties -->
	</bean>

### 3.2 注解配置
`@Value` 注解可以给字段、方法、方法和构造的参数指定一个初始值，这有一个例子给字段赋初始值：

	public static class FieldValueTestBean
	
	    @Value("#{ systemProperties['user.region'] }")
	    private String defaultLocale;
	
	    public void setDefaultLocale(String defaultLocale) {
	        this.defaultLocale = defaultLocale;
	    }
	
	    public String getDefaultLocale() {
	        return this.defaultLocale;
	    }
	
	}

也可以把它放在 `set` 方法上:

	public static class PropertyValueTestBean
	
	    private String defaultLocale;
	
	    @Value("#{ systemProperties['user.region'] }")
	    public void setDefaultLocale(String defaultLocale) {
	        this.defaultLocale = defaultLocale;
	    }
	
	    public String getDefaultLocale() {
	        return this.defaultLocale;
	    }
	
	}

`Autowired` 的方法和构造也可以使用 @Value 注解.

	public class SimpleMovieLister {
	
	    private MovieFinder movieFinder;
	    private String defaultLocale;
	
	    @Autowired
	    public void configure(MovieFinder movieFinder,
	            @Value("#{ systemProperties['user.region'] }") String defaultLocale) {
	        this.movieFinder = movieFinder;
	        this.defaultLocale = defaultLocale;
	    }
	
	    // ...
	}

	public class MovieRecommender {
	
	    private String defaultLocale;
	
	    private CustomerPreferenceDao customerPreferenceDao;
	
	    @Autowired
	    public MovieRecommender(CustomerPreferenceDao customerPreferenceDao,
	            @Value("#{systemProperties['user.country']}") String defaultLocale) {
	        this.customerPreferenceDao = customerPreferenceDao;
	        this.defaultLocale = defaultLocale;
	    }
	
	    // ...
	}

## 4 语言参考
### 4.1 文字表达式
文字表达式支持 `String` 数值（int 实数 hex）`bool` 和null值，字符串要放在两个单引号中间，下面的例子是一些简单的使用，但是文字表达式一般不会单独使用，通常是结合复杂的表达式使用：
	
	ExpressionParser parser = new SpelExpressionParser();
	
	// evals to "Hello World"
	String helloWorld = (String) parser.parseExpression("'Hello World'").getValue();
	
	double avogadrosNumber = (Double) parser.parseExpression("6.0221415E+23").getValue();
	
	// evals to 2147483647
	int maxValue = (Integer) parser.parseExpression("0x7FFFFFFF").getValue();
	
	boolean trueValue = (Boolean) parser.parseExpression("true").getValue();
	
	Object nullValue = parser.parseExpression("null").getValue();

数值类型支持负数、指数和小数点，实数默认使用 `Double.parseDouble()` 解析

### 4.2 Property Arrays, Lists, Maps, Indexers
操作属性值是非常简单的，只要使用一个 `.` 来指定一个嵌套的值：
	
	// evals to 1856
	int year = (Integer) parser.parseExpression("Birthdate.Year + 1900").getValue(context);
	
	String city = (String) parser.parseExpression("placeOfBirth.City").getValue(context);

属性的第一个字母可以是不敏感的，数组和集合值使用 `[ ]` 来获取：

	ExpressionParser parser = new SpelExpressionParser();
	
	// Inventions Array
	StandardEvaluationContext teslaContext = new StandardEvaluationContext(tesla);
	
	// evaluates to "Induction motor"
	String invention = parser.parseExpression("inventions[3]").getValue(
	        teslaContext, String.class);
	
	// Members List
	StandardEvaluationContext societyContext = new StandardEvaluationContext(ieee);
	
	// evaluates to "Nikola Tesla"
	String name = parser.parseExpression("Members[0].Name").getValue(
	        societyContext, String.class);
	
	// List and Array navigation
	// evaluates to "Wireless communication"
	String invention = parser.parseExpression("Members[0].Inventions[6]").getValue(
	        societyContext, String.class);

map 值需要指定 `[key]` 来获取，如果key 是 `String` 类型的，就可以使用 `['string']` 来获取：

	// Officer's Dictionary
	
	Inventor pupin = parser.parseExpression("Officers['president']").getValue(
	        societyContext, Inventor.class);
	
	// evaluates to "Idvor"
	String city = parser.parseExpression("Officers['president'].PlaceOfBirth.City").getValue(
	        societyContext, String.class);
	
	// setting values
	parser.parseExpression("Officers['advisors'][0].PlaceOfBirth.Country").setValue(
	        societyContext, "Croatia");

