package ru.ashirobokov.qbit.todo.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TodoItem {

    private String id;
    private final String description;
    private final String name;
    private final Date due;

    public TodoItem(String description, String name, Date due) {
        this.id = name + "::" + new SimpleDateFormat("dd-MM-yyyy").format(due);
        this.description = description;
        this.name = name;
        this.due = due;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public Date getDue() {
        return due;
    }

    @Override
    public String toString() {
        return "TodoItem{" +
                "description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", due=" + due +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TodoItem todo = (TodoItem) o;

        if (due != todo.due) { return false; }
        return !(name != null ? !name.equals(todo.name) : todo.name != null);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (int) (due.getTime() ^ (due.getTime() >>> 32));
        return result;
    }

}
