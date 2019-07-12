package ru.ashirobokov.qbit.test;

import io.advantageous.boon.core.Sys;
import io.advantageous.qbit.client.Client;
import io.advantageous.qbit.client.ClientBuilder;
import io.advantageous.qbit.http.client.HttpClient;
import io.advantageous.qbit.http.client.HttpClientBuilder;
import io.advantageous.qbit.reactive.Callback;
import io.advantageous.qbit.server.EndpointServerBuilder;
import io.advantageous.qbit.server.ServiceEndpointServer;
import io.advantageous.qbit.service.ServiceQueue;
import io.advantageous.reakt.Result;
import io.advantageous.reakt.promise.Promise;
import io.advantageous.reakt.promise.Promises;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import ru.ashirobokov.qbit.todo.model.TodoItem;
import ru.ashirobokov.qbit.todo.service.TodoManagerClient;
import ru.ashirobokov.qbit.todo.service.TodoManagerImpl;
import ru.ashirobokov.qbit.todo.system.EndpointServerBuilderNoHealthSrv;
import ru.ashirobokov.qbit.todo.system.ServiceUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static io.advantageous.qbit.service.ServiceBuilder.serviceBuilder;
import static io.advantageous.qbit.service.ServiceProxyUtils.flushServiceProxy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TodoManagerImplTestByWebSocket {
    /** QBit WebSocket Client */
    private Client webSocketClient;

    /** Object address to the todoManagerImpl service actor. */
    private final String todoAddress = "todoService";
    /** Service Server */
    private ServiceEndpointServer serviceEndpointServer;
    /** Client service proxy to the todoManager */
    private TodoManagerClient proxy;

    @Before
    public void setup() {

    try {

        /* Create the serviceBundleBuilder. */
//        final EndpointServerBuilder endpointServerBuilder = endpointServerBuilder();
        final EndpointServerBuilder endpointServerBuilder = new EndpointServerBuilderNoHealthSrv();

        /* Create a todo manager */
        final TodoManagerImpl todoManager = new TodoManagerImpl();

        /* Create the service server. */
        serviceEndpointServer = endpointServerBuilder
                .setHost("127.0.0.1")
                .setPort(8080)
                .setHealthService(null)
                .setEnableHealthEndpoint(false)
//                .addService(todoAddress, todoManager)
                .build();

        // Add the todoManager to the serviceBundle. This an alternative way to .addService(todoAddress, todoManager) above
        serviceEndpointServer.serviceBundle()
                .addServiceObject(todoAddress, todoManager)
                .startServiceBundle();

// Create a proxy proxy to communicate with the service actor.
// NO need in case of REMOTE calls by WebSocet
//       proxy = serviceEndpointServer.serviceBundle().createLocalProxy(TodoManagerClient.class, todoAddress);

        /* Start the service endpoint server and wait until it starts. */
        ServiceUtils.disableHttpServerDebug(endpointServerBuilder.getHttpTransport());
        serviceEndpointServer.startServerAndWait();

    } catch (Exception e) {
        e.printStackTrace();
    }

        /* Create the WebSocket Client. */
        final ClientBuilder clientBuilder = ClientBuilder.clientBuilder();

        /** Build the webSocketClient. */
        webSocketClient = clientBuilder
                .setHost("127.0.0.1")
                .setPort(8080)
                .setAutoFlush(true)
                .setFlushInterval(1)
                .setProtocolBatchSize(100)
                .build();

        /* Create a REMOTE proxy proxy to communicate with the service actor. */
        proxy = webSocketClient.createProxy(TodoManagerClient.class, todoAddress);

        /* Start the remote proxy. */
        webSocketClient.start();
    }


    @Test
    public void test() throws Exception {

        // Add the todo item.
        Callback<Boolean> callback =  new Callback<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {
                System.out.println(aBoolean.booleanValue() ? "*** succeed" : "--- failed");
            }
        };


//        Callback<Integer> callback =  new Callback<Integer>() {
//            @Override
//            public void accept(Integer size) {
//                System.out.println("SIZE :" + size);
//            }
//        };

        proxy.add(callback, new TodoItem("Write tutorial", "writing document", new Date()));
//        flushServiceProxy(proxy);
       Sys.sleep(1000);

       proxy.add(callback, new TodoItem("Write something ", "writing email", new Date()));
//        flushServiceProxy(proxy);
        Sys.sleep(1000);

        List<TodoItem> list = new ArrayList<>();
        Callback<List<TodoItem>> listCallback = new Callback<List<TodoItem>>() {
            @Override
            public void accept(List<TodoItem> todoList) {
  //              assertEquals("Make sure there is two", 2, todoList.size());
                System.out.println("Size :" + todoList.size());
                todoList.forEach(item -> {
                    list.add(item);
                    System.out.println("Name :" + item.getName());
                    System.out.println("Description :" + item.getDescription());
                } );
            }
        };
        proxy.list(listCallback);
//        flushServiceProxy(proxy);
        Sys.sleep(1000);

        proxy.remove(callback, list.get(0).getId());
//        flushServiceProxy(proxy);
        Sys.sleep(1000);
//
        proxy.list(listCallback);
//        flushServiceProxy(proxy);
        Sys.sleep(1000);

    }

    @After
    public void tearDown() throws Exception{
        Thread.sleep(100);
        serviceEndpointServer.stop();
        webSocketClient.stop();
    }

}
