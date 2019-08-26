package com.mooc.house.web.interceptor;

import com.mooc.house.common.model.User;

public class UserContext {
    //ThreadLocal很多程序猿只将它作为一种用于“方便传參”的工具
    //每一个ThreadLocal能够放一个线程级别的变量，可是它本身能够被多个线程共享使用，并且又能够达到线程安全的目的，且绝对线程安全。
    /**
     * USER_HOLDER代表一个能够存放User类型的ThreadLocal对象。
     * 此时不论什么一个线程能够并发訪问这个变量，对它进行写入、读取操作，都是线程安全的。
     * 比方一个线程通过USER_HOLDER.set(user);将数据写入ThreadLocal中，在不论什么地方，都能够通过USER_HOLDER.get();将值获取出来。
     */
    private static final ThreadLocal<User> USER_HOLDER = new ThreadLocal<>();

    public static void setUser(User user){
        USER_HOLDER.set(user);
    }
    public static User getUser(){
        return USER_HOLDER.get();
    }
    public static void remove(){
        USER_HOLDER.remove();
    }
}
