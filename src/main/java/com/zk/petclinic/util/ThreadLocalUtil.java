package com.zk.petclinic.util;

public class ThreadLocalUtil {
    private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();
    public static <T> T get(){
        return (T) threadLocal.get();
    }
    public static void set(String value) {
        threadLocal.set(value);
    }
    public static void remove(){
        threadLocal.remove();
    }
}
