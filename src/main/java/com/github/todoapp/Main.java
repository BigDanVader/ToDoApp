package com.github.todoapp;

import ControllerPackage.ToDoController;

public class Main {
    public static void main(String[] args) {
        ToDoController ctr = new ToDoController();
        ctr.start();
    }
}