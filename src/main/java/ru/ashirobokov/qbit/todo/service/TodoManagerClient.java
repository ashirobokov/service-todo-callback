package ru.ashirobokov.qbit.todo.service;

import io.advantageous.qbit.reactive.Callback;
import ru.ashirobokov.qbit.todo.model.TodoItem;

import java.util.List;

public interface TodoManagerClient {
    void add(Callback<Boolean> callback, TodoItem todo);
    void remove(Callback<Boolean> callback, String id);
    void list(Callback<List<TodoItem>> callback);
}
