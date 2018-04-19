 Validation, Data Binding, and Type Conversion （校验、数据绑定和类型转换）
## 1 Validation using Spring’s Validator interface
Spring的特点是验证器接口，您可以使用它来验证对象。Validator接口使用一个纠错对象，这样在验证时，验证器可以向纠错对象报告验证失败。<br/><br/>
假如我们这里有一个Person类：

	public class Person {
	
		private String name;
		private int age;
		
		// the usual getters and setters...
	}

我们这里通过实现 `org.springframework.validation.Validator` 的两个方法来为 `Person` 提供校验行为：

- `supports(Class)` — 这个校验器是否能校验所提供类的实例 <br/>
- `validate(Object, org.springframework.validation.Errors)` — 验证给定对象，并在验证错误的情况下，用给定的错误对象注册这些对象：


		public class PersonValidator implements Validator {
	
			/**
			 * This Validator validates *just* Person instances
			 */
			public boolean supports(Class clazz) {
			    return Person.class.equals(clazz);
			}
			
			public void validate(Object obj, Errors e) {
			    ValidationUtils.rejectIfEmpty(e, "name", "name.empty");
			    Person p = (Person) obj;
			    if (p.getAge() < 0) {
			        e.rejectValue("age", "negativevalue");
			    } else if (p.getAge() > 110) {
			        e.rejectValue("age", "too.darn.old");
			    }
			}
		}

---


## 2 Spring 字段格式化
### 2.1 Formatter SPI
 Formatter SPI 实现的字段格式化是简单的，也是强制性的（强类型转换）

	package org.springframework.format;
	
		public interface Formatter<T> extends Printer<T>, Parser<T> {
	}
`Formatter` 继承了 `Printer` 和 `Parser` 

	public interface Printer<T> {
	    String print(T fieldValue, Locale locale);
	} 

<br/>

	import java.text.ParseException;
	
	public interface Parser<T> {
	    T parse(String clientValue, Locale locale) throws ParseException;
	}

要创建你自己的 formatter 只要实现上面的 `Formatter` 就行，T 是你需要格式化的类型，例如， `java.util.Date` 。<br/>
实现 `print()` 方法来打印 T 的一个实例在该客户端语言环境下的展示。 <br/>
实现 `parse()` 方法来解析格式化好的 T 的实例 <br/><br/>
formatter 的子目录下有一些默认的实现： 

+ number 包： 提供了 `NumberFormatter`, `CurrencyFormatter`, and 	`PercentFormatter` ，通过 `java.text.NumberFormat` 来格式化 `java.lang.Number` 对象。
+ datetime 包： 提供了 `DateFormatter` ，通过 `java.text.DateFormat` 来格式化 `java.util.Date` 对象等
<br/>
<br/>
以下以 ` DateFormatter` 作为实现 `Formatter` 的例子：

		package org.springframework.format.datetime;
		public final class DateFormatter implements Formatter<Date> {
			private String pattern;
			public DateFormatter(String pattern) {
			    this.pattern = pattern;
			}
			
			public String print(Date date, Locale locale) {
			    if (date == null) {
			        return "";
			    }
			    return getDateFormat(locale).format(date);
			}
			
			public Date parse(String formatted, Locale locale) throws ParseException {
			    if (formatted.length() == 0) {
			        return null;
			    }
			    return getDateFormat(locale).parse(formatted);
			}
			
			protected DateFormat getDateFormat(Locale locale) {
			    DateFormat dateFormat = new SimpleDateFormat(this.pattern, locale);
			    dateFormat.setLenient(false);
			    return dateFormat;
			}
		}

### 2.2 注解驱动的 formatting
可通过注解和字段类型来配置 formatter , 要将一个 formatter 绑定到注解，只需要实现 `AnnotationFormatterFactory` 接口：

		package org.springframework.format;
		
		public interface AnnotationFormatterFactory<A extends Annotation> {
		
			Set<Class<?>> getFieldTypes();
			
			Printer<?> getPrinter(A annotation, Class<?> fieldType);
			
			Parser<?> getParser(A annotation, Class<?> fieldType);
		
		}


- A 是需要关联格式化逻辑的字段的注解类型
- `getFieldTypes()` 返回注解字段可能用到的类型
- `getPrinter()` 返回一个Printer
- `getParser()` 返回一个Parser


以下是一个将 `@NumberFormat` 注解绑定到 `formatter` 的例子：
		
		public final class NumberFormatAnnotationFormatterFactory
		    implements AnnotationFormatterFactory<NumberFormat> {
		
			public Set<Class<?>> getFieldTypes() {
			    return new HashSet<Class<?>>(asList(new Class<?>[] {
			        Short.class, Integer.class, Long.class, Float.class,
			        Double.class, BigDecimal.class, BigInteger.class }));
			}
			
			public Printer<Number> getPrinter(NumberFormat annotation, Class<?> fieldType) {
			    return configureFormatterFrom(annotation, fieldType);
			}
			
			public Parser<Number> getParser(NumberFormat annotation, Class<?> fieldType) {
			    return configureFormatterFrom(annotation, fieldType);
			}
			
			private Formatter<Number> configureFormatterFrom(NumberFormat annotation,
			        Class<?> fieldType) {
			    if (!annotation.pattern().isEmpty()) {
			        return new NumberFormatter(annotation.pattern());
			    } else {
			        Style style = annotation.style();
			        if (style == Style.PERCENT) {
			            return new PercentFormatter();
			        } else if (style == Style.CURRENCY) {
			            return new CurrencyFormatter();
			        } else {
			            return new NumberFormatter();
			        }
			    }
			}
		}

想要触发格式化操作 ， 只要将 `@NumberFormat` 注解添加到某个字段上：

		public class MyModel {
		
		    @NumberFormat(style=Style.CURRENCY)
		    private BigDecimal decimal;
		
		}

`org.springframework.format.annotation` 包下提供了方便的注解Api `@NumberFormat` `@DateTimeFormat `  等，下面是一个使用 `@DateTimeFormat` 将 `java.util.Date` 格式化成 ISO 时间的列子：

		public class MyModel {
		
		    @DateTimeFormat(iso=ISO.DATE)
		    private Date date;
		
		}


### 2.3 配置全局的 date&time formatter
没有使用 @DateTimeFormat 注解的字符串，默认是用 `DateFormat.SHORT ` 风格来转换的， 如果你想使用自己的方式来格式化，你可以改变你自己的全局设置。<br/><br/>
Spring 不会注册默认的 formatters ，所有的 formatters 都需要你自己手动注册。<br/>

使用 `org.springframework.format.datetime.joda.JodaTimeFormatterRegistrar` 还是`org.springframework.format.datetime.DateFormatterRegistrar` 取决于你要不要使用 Joda api. 例如下面配置了全局的 `yyyyMMdd` 的格式化器(不适应Joda) :

	@Configuration
	public class AppConfig {
	
	    @Bean
	    public FormattingConversionService conversionService() {
	
	        // Use the DefaultFormattingConversionService but do not register defaults
	        DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService(false);
	
	        // Ensure @NumberFormat is still supported
	        conversionService.addFormatterForFieldAnnotation(new NumberFormatAnnotationFormatterFactory());
	
	        // Register date conversion with a specific global format
	        DateFormatterRegistrar registrar = new DateFormatterRegistrar();
	        registrar.setFormatter(new DateFormatter("yyyyMMdd"));
	        registrar.registerFormatters(conversionService);
	
	        return conversionService;
	    }
	}

---

## 3 Spring 校验
&emsp;&emsp;Spring3 介绍了对校验支持的几个增强：

- 完全支持 JSR-303 的bean校验
- Spring 的 `DataBinder ` 不仅能做数据绑定，还能同时支持校验
- Spring MVC 支持对 `Controller` 的输入值进行声明式的校验  

### 3.1 JSR-303 bean 校验概览
&emsp;&emsp;  JSR-303标准化了Java平台的验证约束声明和元数据。你再一个实体类的属性行通过注解来声明校验约束，到了运行时就会强制执行，这有大量的编译时约束可以利用，也可创建你自己的自定义约束。<br/>
&emsp;&emsp;  这有个简单的实体类： 

	public class PersonForm {
	    private String name;
	    private int age;
	}

JSR-303允许您对这些属性定义声明式验证约束：

	public class PersonForm {
	
	    @NotNull
	    @Size(max=64)
	    private String name;
	
	    @Min(0)
	    private int age;
	
	}

### 3.2 配置一个 Bean Validation Provider
&emsp;&emsp;  Spring 对 bean 校验提供了全面的支持，将JSR-303/JSR-349 的 Bean Validation Provider 引导为一个spring bean 注入，也就是说你可以在任何需要的地方注入：

- javax.validation.ValidatorFactory 
- javax.validation.Validator

使用 `LocalValidatorFactoryBean` 来配置一个默认的校验器作为一个 `Spring bean` 

	<bean id="validator"
	    class="org.springframework.validation.beanvalidation.LocalValidatorFactoryBean"/>

上面的基本配置将使用默认引导机制来触发Bean验证初始化，JSR-303/JSR-349提供者，如Hibernate验证器，预计将出现在类路径中，并将自动检测到。
### 3.3 注入校验器
&emsp;&emsp; `LocalValidatorFactoryBean ` 即实现了 `javax.validation.ValidatorFactory` 和 `javax.validation.Validator`, 也实现了 `Spring’s org.springframework.validation.Validator` ，更喜欢哪个就注入哪个：

		import javax.validation.Validator;
		
		@Service
		public class MyService {
		
		    @Autowired
		    private Validator validator;
<br/>

		import org.springframework.validation.Validator;
		
		@Service
		public class MyService {
		
		    @Autowired
		    private Validator validator;
		
		}
### 3.4 配置自定义约束
每个 `Bean Validation constraint` 都包含两个部分：

- 一个 `@Constraint` 注解，声明它的约束校验和相关的属性配置
- 实现 `javax.validation.ConstraintValidator` 接口，并且实现约束行为

为了将声明与实现相关联，每个 `@Constraint` 注解引用一个对应的 validation 约束实现类。一般来说，`LocalValidatorFactoryBean` 配置了一个 `SpringConstraintValidatorFactory`，这可以是你像使用其他bean一样收益与spring的依赖注入。<br/><br/>
下面是一个自动 `Constraint` 的声明，紧接着的是使用Spring 依赖注入的相关联的类：

		@Target({ElementType.METHOD, ElementType.FIELD})
		@Retention(RetentionPolicy.RUNTIME)
		@Constraint(validatedBy=MyConstraintValidator.class)
		public @interface MyConstraint {
		}
		import javax.validation.ConstraintValidator;
		
		public class MyConstraintValidator implements ConstraintValidator {
		
		    @Autowired;
		    private Foo aDependency;
		
		    ...
		} 
 