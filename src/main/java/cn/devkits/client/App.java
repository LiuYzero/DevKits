/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client;

import cn.devkits.client.asyn.AppStarter;
import org.jdesktop.swingx.JXTree;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.swing.*;


/**
 * 
 * Development Kits
 * 
 * @author Fengshao Liu
 * @version 1.0.0
 * @datetime 2019年9月5日 下午10:51:07
 */
public class App {

    private static AnnotationConfigApplicationContext context;

    @SuppressWarnings("resource")
    public static void main(String[] args) {
        context = new AnnotationConfigApplicationContext(AppSpringContext.class);
        SwingUtilities.invokeLater(new AppStarter());
    }

    public static AnnotationConfigApplicationContext getContext() {
        return context;
    }
}
