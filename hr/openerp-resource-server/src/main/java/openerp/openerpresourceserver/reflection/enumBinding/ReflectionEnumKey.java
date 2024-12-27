package openerp.openerpresourceserver.reflection.enumBinding;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ReflectionEnumKeys.class)
@Target(ElementType.TYPE)
public @interface ReflectionEnumKey {
    String value() default "";
}
