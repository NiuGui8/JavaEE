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

