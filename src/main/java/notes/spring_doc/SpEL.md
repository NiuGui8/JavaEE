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