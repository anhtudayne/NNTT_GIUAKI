package com.languagecenter.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {
    private static EntityManagerFactory factory;

    // Sử dụng khối static để khởi tạo EntityManagerFactory 1 lần duy nhất khi class Load
    static {
        try {
            // Tên "LanguageCenterPU" phải khớp với name trong thẻ <persistence-unit> của persistence.xml
            factory = Persistence.createEntityManagerFactory("LanguageCenterPU");
        } catch (Throwable ex) {
            System.err.println("Khởi tạo EntityManagerFactory thất bại. " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static EntityManager getEntityManager() {
        return factory.createEntityManager();
    }

    public static void shutdown() {
        if (factory != null) {
            factory.close();
        }
    }
}
