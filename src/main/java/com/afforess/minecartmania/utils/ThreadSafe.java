/**
 *
 */
package com.afforess.minecartmania.utils;

import java.lang.annotation.*;

/**
 * @author Afforess
 */
@Documented
@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ThreadSafe {

    public String author() default "Afforess";

    public String version() default "1.0";

    public String shortDescription() default "Indicates that the function is safe to use in parallel threads";

}
