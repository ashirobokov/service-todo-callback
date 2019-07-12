package ru.ashirobokov.qbit.test;

import io.advantageous.boon.core.Sys;
import io.advantageous.qbit.reactive.Callback;
import io.advantageous.qbit.server.EndpointServerBuilder;
import io.advantageous.qbit.server.ServiceEndpointServer;
import io.advantageous.reakt.promise.Promise;
import io.advantageous.reakt.promise.Promises;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.ashirobokov.qbit.todo.model.TodoItem;
import ru.ashirobokov.qbit.todo.service.TodoManagerClient;
import ru.ashirobokov.qbit.todo.service.TodoManagerImpl;
import ru.ashirobokov.qbit.todo.system.EndpointServerBuilderNoHealthSrv;
import ru.ashirobokov.qbit.todo.system.ServiceUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static io.advantageous.qbit.service.ServiceProxyUtils.flushServiceProxy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TodoManagerImplTestByEndpointLocalProxy {
    /** Object address to the todoManagerImpl service actor. */
    private final String todoAddress = "todoService";
    /** Service Server */
    private ServiceEndpointServer serviceEndpointServer;

    /** Client service proxy to the todoManager */
    private TodoManagerClient proxy;

    @Before
    public void setup() {

        try {

        /* Create the EndpointServerBuilder. */
            final EndpointServerBuilder endpointServerBuilder = new EndpointServerBuilderNoHealthSrv();


        /* Create the service server. */
            serviceEndpointServer = endpointServerBuilder
                    .setHealthService(null)
                    .setEnableHealthEndpoint(false)
                    .build();

        /* Create a todo manager */
            final TodoManagerImpl todoManager = new TodoManagerImpl();
            // Add the todoManager to the serviceBundle.
            serviceEndpointServer.serviceBundle()
                    .addServiceObject(todoAddress, todoManager)
                    .startServiceBundle();

        //Create a proxy proxy to communicate with the service actor.
        proxy = serviceEndpointServer.serviceBundle().createLocalProxy(TodoManagerClient.class, todoAddress);

        /* Start the service endpoint server and wait until it starts. */
            ServiceUtils.disableHttpServerDebug(endpointServerBuilder.getHttpTransport());
            serviceEndpointServer.startServerAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }

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

        proxy.add(callback, new TodoItem("Write tutorial-1", "write-1", new Date()));
        flushServiceProxy(proxy);
        Sys.sleep(100);

        proxy.add(callback, new TodoItem("Write tutorial-2", "write-2", new Date()));
        flushServiceProxy(proxy);
        Sys.sleep(100);


        Callback<List<TodoItem>> listCallback = new Callback<List<TodoItem>>() {
            @Override
            public void accept(List<TodoItem> todoList) {
                assertEquals("Make sure there is one", 2, todoList.size());
                todoList.forEach(item -> {
                    System.out.println("Name :" + item.getName());
                    System.out.println("Description :" + item.getDescription());
                } );
            }
        };
        proxy.list(listCallback);
        flushServiceProxy(proxy);
        Sys.sleep(1000);

        // Add the todo item.
//        proxy.add(new TodoItem("Write tutorial", "write", new Date()))
//                .invokeWithPromise(promise);
//
//
//        assertTrue("The call was successful", promise.success());
//        assertTrue("The return from the add call", promise.get());
//
//        final Promise<List<TodoItem>> promiseList = Promises.blockingPromiseList(TodoItem.class);
//
//        // Get a list of todo items.
//        proxy.list().invokeWithPromise(promiseList);
//
//
//        // See if the TodoItem item we created is in the listing.
//        final List<TodoItem> todoList = promiseList.get().stream()
//                .filter(todo -> todo.getName().equals("write")
//                        && todo.getDescription().equals("Write tutorial")).collect(Collectors.toList());
//
//        // Make sure we found it.
//        assertEquals("Make sure there is one", 1, todoList.size());
//
//
//        // Remove promise
//        final Promise<Boolean> removePromise = Promises.blockingPromiseBoolean();
//        proxy.remove(todoList.get(0).getId()).invokeWithPromise(removePromise);
//
//
//        final Promise<List<TodoItem>> promiseList2 = Promises.blockingPromiseList(TodoItem.class);
//
//        // Make sure it is removed.
//        proxy.list().invokeWithPromise(promiseList2);
//
//        // See if the TodoItem item we created is removed.
//        final List<TodoItem> todoList2 = promiseList2.get().stream()
//                .filter(todo -> todo.getName().equals("write")
//                        && todo.getDescription().equals("Write tutorial")).collect(Collectors.toList());
//
//        // Make sure we don't find it.
//        assertEquals("Make sure there is one", 0, todoList2.size());

    }


    @After
    public void tearDown() throws Exception{
        Thread.sleep(100);
        serviceEndpointServer.stop();
    }

}
