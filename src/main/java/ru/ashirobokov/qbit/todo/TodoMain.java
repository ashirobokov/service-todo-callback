package ru.ashirobokov.qbit.todo;

import io.advantageous.qbit.admin.ManagedServiceBuilder;
import io.advantageous.qbit.server.EndpointServerBuilder;
import io.advantageous.qbit.server.ServiceEndpointServer;
import io.advantageous.qbit.service.ServiceBundle;
import io.advantageous.reakt.reactor.Reactor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ashirobokov.qbit.todo.service.TodoManagerImpl;
import ru.ashirobokov.qbit.todo.system.EndpointServerBuilderNoHealthSrv;
import ru.ashirobokov.qbit.todo.system.ServiceUtils;

public class TodoMain {

    private final static String todoAddress = "todoService";

    public final static Logger LOG = LoggerFactory.getLogger(TodoMain.class);

    public static void main(String[] args) {
        int port = 8080;
        LOG.info("Todo Service Server starting ..... ");
        try {

//            Reactor reactor = Reactor.reactor();

            EndpointServerBuilder endpointServerBuilder = new EndpointServerBuilderNoHealthSrv();
            ServiceEndpointServer server = endpointServerBuilder
                    .setHealthService(null).setEnableHealthEndpoint(false)
                    .setPort(port)
                    .build();

/*  инициализируем сервисы и запускаем ендпойнт на порту 8080  */
//            final TodoManagerImpl todoManager = new TodoManagerImpl(reactor);
            final TodoManagerImpl todoManager = new TodoManagerImpl();
            server.serviceBundle()
                  .addServiceObject("todoService", todoManager)
                  .startServiceBundle();

            ServiceUtils.disableHttpServerDebug(endpointServerBuilder.getHttpTransport());
            server.start();

        } catch (Exception e) {
            LOG.error("Todo Service Server start error");
        }

        LOG.info("/Todo Service Server on " + port + " started");

    }

}
