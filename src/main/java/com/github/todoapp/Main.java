package com.github.todoapp;

import WrapperPackage.ToDoWrapper;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.postgresql.ds.PGSimpleDataSource;

import JavaBeanPackage.ToDoBean;
import ServicePackage.CockroachHandler;



public class Main {
    public static void main(String[] args) throws SQLException {
        PGSimpleDataSource ds = new PGSimpleDataSource();

        ds.setUrl("jdbc:postgresql://stoic-cat-3327.g95.cockroachlabs.cloud:26257/ToDoDB?sslmode=verify-full");
        ds.setSslMode( "require" );
        ds.setUser("demo_todo");
        ds.setPassword("oKDnWiZLElJt7pCal8KsDA");

        System.out.println("getAll");
        System.out.println();
        CockroachHandler handler = new CockroachHandler(ds);
        printDB(handler.getAll());

        System.out.println("create");
        System.out.println();
        ToDoBean bean = new ToDoBean();
        bean.setEvent("Get this working.");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");   
        bean.setCreated(dtf.format(LocalDateTime.now()));
        bean.setNotes("Holy crap, I did it!");
        bean.setPriority("true");
        handler.create(bean);
        ToDoWrapper test = handler.getAll();
        List<ToDoBean> testcase = test.getTodos();
        printDB(test);

        System.out.println("search");
        System.out.println();
        ToDoBean search = new ToDoBean();
        search.setPriority("true");
        printDB(handler.searchByPriority(search));

        ToDoBean bean2 = new ToDoBean();
        for (ToDoBean b : testcase){
            if (b.getNotes().equals("Holy crap, I did it!")){
                bean2 = b;
                break;
            }
        }

        bean2.setNotes("I did it again!");

        handler.update(bean2);
        System.out.println("Upsert");
        System.out.println();
        printDB(handler.getAll());

        System.out.println("Delete");
        System.out.println();
        handler.delete(bean2);
        printDB(handler.getAll());
    }

    private static void printDB(ToDoWrapper wrap) {
        List<ToDoBean> db = wrap.getTodos();
        List<String> md = wrap.getMetadata();
        for (ToDoBean bean : db){
            System.out.print(bean.getUuid());
            System.out.print(", ");
            System.out.print(bean.getEvent());
            System.out.print(", ");
            System.out.print(bean.getCreated());
            System.out.print(", ");
            System.out.print(bean.getNotes());
            System.out.print(", ");
            System.out.print(bean.getPriority());
            System.out.println();
        }
        for (String col : md){
            System.out.print(col);
            System.out.print(" ");
        }
        System.out.println();
        System.out.println();
    }
}