/*
 * This source file was generated by the Gradle 'init' task
 */
package org.example;

import Factory.MainFactory;

public class App {
    //private static Object MainFactory;

    public static String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        //String a  = getGreeting();
        MainFactory.initFactory();
        MainFactory.createModels();
    }
}
