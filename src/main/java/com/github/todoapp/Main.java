package com.github.todoapp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import ControllerPackage.ToDoController;

public class Main {
    public static void main(String[] args) throws SQLException, FileNotFoundException, IOException {
        ToDoController ctr = new ToDoController();
        ctr.start();
    }
}