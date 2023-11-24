package org.example.utils;

import org.example.Main;
import org.springframework.context.ApplicationContext;

public class DBUtil {
    public static ApplicationContext getContext() {
        return Main.context;
    }
}
