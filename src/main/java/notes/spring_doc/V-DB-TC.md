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
