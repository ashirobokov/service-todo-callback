package ru.ashirobokov.qbit.test;

import io.advantageous.boon.core.Sys;
import io.advantageous.qbit.reactive.Callback;
import io.advantageous.qbit.service.ServiceQueue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.ashirobokov.qbit.todo.model.TodoItem;
import ru.ashirobokov.qbit.todo.service.TodoManagerClient;
import ru.ashirobokov.qbit.todo.service.TodoManagerImpl;

import java.util.Date;
import java.util.List;

import static io.advantageous.qbit.service.ServiceBuilder.serviceBuilder;
import static io.advantageous.qbit.service.ServiceProxyUtils.flushServiceProxy;
import static org.junit.Assert.assertEquals;

public class TodoManagerImplTestOverServiceQueue {

    /** Client service proxy to the todoManager */
    private TodoManagerClient proxy;

    @Before
    public void setup() {

        final TodoManagerImpl todoManager = new TodoManagerImpl();

        ServiceQueue serviceQueue = serviceBuilder()
                .setServiceObject(todoManager)
                .build().startServiceQueue().startCallBackHandler();

        proxy = serviceQueue.createProxy(TodoManagerClient.class);

    }

    @Test
    public void test() throws Exception {

        // Add the todo item.
        Callback<Boolean> callback =  new Callback<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {
                System.out.println(aBoolean.booleanValue() ? "*** succeed" : "--- failed" );
            }
        };

        proxy.add(callback, new TodoItem("Write tutorial", "write", new Date()));
        flushServiceProxy(proxy);
        Sys.sleep(100);

        Callback<List<TodoItem>> listCallback = new Callback<List<TodoItem>>() {
            @Override
            public void accept(List<TodoItem> todoList) {
                assertEquals("Make sure there is one", 1, todoList.size());
                todoList.forEach(item -> {
                    System.out.println("Name :" + item.getName());
                    System.out.println("Description :" + item.getDescription());
                } );
            }
        };
        proxy.list(listCallback);
        flushServiceProxy(proxy);
        Sys.sleep(100);

    }

    @After
    public void tearDown() throws Exception{
        Thread.sleep(100);
    }

}
