package xing.ui;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ComponentLayout {
	LayoutPolicy direction() default LayoutPolicy.CLOSER;
	ExpandPolicy expand() default ExpandPolicy.DEFAULT;
	int span() default 1;
}
