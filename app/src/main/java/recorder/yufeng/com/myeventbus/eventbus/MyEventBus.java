package recorder.yufeng.com.myeventbus.eventbus;

import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;



/**
 * Created by yufeng on 2016/11/15.
 * Version 1
 * Function
 * Tips
 */

public class MyEventBus {

    private static final String TAG = "MyEventBus";
    /**
     * 一个类对应一个 方法数组
     * 因为一个类可能有多个方法是eventbus回调的方法
     */
    private Map<Object, List<Method>> METHOD_CACHE = new HashMap<>();

    private Handler handler=new Handler();


    private static volatile MyEventBus myEventBus;

    private MyEventBus() {
    }

    public static MyEventBus getDefault() {

        if (myEventBus == null) {
            synchronized (MyEventBus.class) {
                if (myEventBus == null) {
                    myEventBus = new MyEventBus();
                }
            }
        }
        return myEventBus;
    }

    /**
     * 注册当前类的监听
     * tips
     * 注册的步骤
     * 1、获取当前类的所有的Subscribe注解的方法
     * 2、将方法缓存在Map中(METHOD_CACHE)
     *
     * @param object
     */
    public void register(Object object) {

        Class<?> aClass = object.getClass();//获取当前类的  class
        List<Method> classMethods = METHOD_CACHE.get(aClass);

        if (classMethods == null) {//第一次注册

            //1、通过反射带Subscribe注解的方法 存在Map中(METHOD_CACHE)
            List<Method> methods = obtainMethod4Class(aClass);
            if (methods.size() == 0) {
                throw new RuntimeException(aClass.getName() + "这个类没有带Subscribe注解的方法");
            }

            METHOD_CACHE.put(object, methods);

        }

    }

    /**
     * 解绑  比较简单
     * @param subscriber
     */
    public void unregister(Object subscriber) {
        METHOD_CACHE.remove(subscriber);
    }

    /**
     * 获取一个class中带Subscribe注解的方法
     */
    private List<Method> obtainMethod4Class(Class<?> claszz) {

        List<Method> myMethods = new ArrayList<>();

        Method[] methods = claszz.getDeclaredMethods();//获取这个类所有的方法
        for (Method method : methods) {
            //判断这个方法是否带Subscribe
            if (method.isAnnotationPresent(Subscribe.class)) {

                method.setAccessible(true);//设置了后 私有方法也可以拿到

                myMethods.add(method);

            }
        }

        return myMethods;

    }


    public void post(final Object object){

        //1、遍历Map 和Map里面的list  找到一个跟object类型一样的
        //2、执行这个类的方法

        Set<Object> set= METHOD_CACHE.keySet();
        final Iterator<Object> it = set.iterator();
        while (it.hasNext()){

            final Object next = it.next();
            List<Method> myMethods=METHOD_CACHE.get(next);
            for (final Method myMethod:myMethods){

                //拿出参数类型
                Class<?>[] types = myMethod.getParameterTypes();
                final Class<?> aClass= types[0];
                //发送的类型跟当前遍历的类型是否是一个类
                if (aClass.isAssignableFrom(object.getClass())){

                    ThreadMode threadMode= myMethod.getAnnotation(Subscribe.class).threadMode();///获取注解后面的ThreadMode的值

                    if (threadMode==ThreadMode.POSTING){//不用处理
                        //执行
                        invokeMethod(next,myMethod,object);
                    }else if (threadMode==ThreadMode.MAIN){//uiThread;

                        //判断当前发送的线程是否是uiThread
                        if (Looper.myLooper()==Looper.getMainLooper()){
                            //是
                            invokeMethod(next,myMethod,object);
                        }else{
                            //不是
                            handler.post(new Runnable() {//看看这个原理
                                @Override
                                public void run() {
                                    invokeMethod(next,myMethod,object);
                                }
                            });

                        }

                    }else {
                        //ASYNC模式
                        if (Looper.myLooper()==Looper.getMainLooper()){
                            //是主线程就转工作线程   一般框架做都是用线程池  咱们就随便写下 就不维护线程池了

                            new Thread(){
                                @Override
                                public void run() {
                                    super.run();
                                    invokeMethod(next,myMethod,object);
                                }
                            }.start();

                        }else{
                            invokeMethod(next,myMethod,object);
                        }
                    }

                }

            }

        }


    }

    private void invokeMethod(Object object,Method method,Object param) {

        try {
            method.setAccessible(true);
            //传递对应的参数过去
            method.invoke(object, param);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
