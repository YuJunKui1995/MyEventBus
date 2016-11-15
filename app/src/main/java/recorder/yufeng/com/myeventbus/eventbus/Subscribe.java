package recorder.yufeng.com.myeventbus.eventbus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by yufeng on 2016/11/15.
 * Version 1
 * Function
 * Tips
 */
@Target(ElementType.METHOD)//方法
@Retention(RetentionPolicy.RUNTIME)
public @interface Subscribe {

    ThreadMode threadMode() default ThreadMode.MAIN;

}
