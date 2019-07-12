package ru.ashirobokov.qbit.todo.service;

import io.advantageous.qbit.annotation.RequestMapping;
import io.advantageous.qbit.reactive.Callback;
import io.advantageous.reakt.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ashirobokov.qbit.todo.model.TodoItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TodoManagerImpl {
    public final static Logger LOG = LoggerFactory.getLogger(TodoManagerImpl.class);

    private final Map<String, TodoItem> todoMap = new TreeMap<>();

    public void add(final Callback<Boolean> callback, final TodoItem todo) {
        LOG.info("TodoManagerClient.add TodoItem ....{}....", todo);
        todoMap.put(todo.getId(), todo);
        callback.resolve(true);
//  research : for type Callback<Integer>
//        callback.accept(todoMap.size());
        LOG.info("/TodoManagerClient.add .....");
    }

    public void remove(final Callback<Boolean> callback, final String id) {
        final TodoItem removed = todoMap.remove(id);
        callback.resolve(removed != null);
    }

    public void list(final Callback<List<TodoItem>> callback) {
        LOG.info("TodoManagerClient.list ...");
        callback.accept(new ArrayList<>(todoMap.values()));
        LOG.info("/TodoManagerClient.list ...");
    }

}
