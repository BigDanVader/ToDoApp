package com.github.todoapp;

import java.sql.SQLException;
import ControllerPackage.ToDoController;

public class Main {
    public static void main(String[] args) throws SQLException {
        ToDoController ctr = new ToDoController();
        ctr.login();
    }
}