package org.example.utils;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        // Giải phóng cache và Connection Pools.
        getSessionFactory().close();
    }

    // Hibernate 5:
    private static SessionFactory buildSessionFactory() {
        // Tạo danh sách dịch vụ từ file config
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml")
                .build();

        // Tạo MetaData (siêu dữ liệu) cung cấp các thông tin về DB, charset, vv...
        Metadata metadata = new MetadataSources(serviceRegistry).getMetadataBuilder().build();
        // Từ Metadata chúng ta có thể lấy ra SessionFactory, class đảm nhiệm tạo ra Session
        return metadata.getSessionFactoryBuilder().build();
    }
}
