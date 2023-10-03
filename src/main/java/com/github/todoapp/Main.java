package com.github.todoapp;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import ControllerPackage.ToDoController;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    
    public static void main(String[] args) {
        FileHandler fh = null;
        try {
            fh = new FileHandler("ErrorLog.log");
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
        fh.setFormatter(new SimpleFormatter());
        LOGGER.addHandler(fh);

        ToDoController ctr = new ToDoController();
        ctr.start();
    }
}