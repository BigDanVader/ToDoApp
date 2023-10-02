package com.github.todoapp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import ControllerPackage.ToDoController;



public class Main{
    public void main(String[] args){
        
        final Logger LOGGER = Logger.getLogger(Main.class.getName());
        SimpleDateFormat format = new SimpleDateFormat("M-d_HHmmss");
        FileHandler fh = null;
        try {
            fh = new FileHandler("ErrorLog_" + format.format(Calendar.getInstance().getTime()) + ".log");
        } catch (Exception e) {
            e.printStackTrace();
        }
        fh.setFormatter(new SimpleFormatter());
        LOGGER.addHandler(fh);

        ToDoController ctr = new ToDoController();
        ctr.start();
    }
}