package com.brainsoon.common.util;

import java.util.WeakHashMap;

/**
 * 
 * @ClassName: KeySynchronizer 
 * @Description:  KEY同步器
 * @author tanghui 
 * @date 2013-8-10 下午9:05:57 
 *
 */
public class KeySynchronizer {
    
    private static final WeakHashMap<Object, Locker> LOCK_MAP = new WeakHashMap<Object, Locker>();
    
    private static class Locker {
        private Locker() {
            
        }
    }
    
    
    public static synchronized Object acquire(Object key) {
        Locker locker = LOCK_MAP.get(key);
        if(locker == null) {
            locker = new Locker();
            LOCK_MAP.put(key, locker);
        }
        return locker;
    }
}
