package ru.ramprox.server.webserver;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.ramprox.server.dispatcher.DispatcherRequest;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WebServerTest {

    @Mock
    private DispatcherRequest dispatcherRequest;

    @Captor
    private ArgumentCaptor<Socket> captor;

    /**
     * 1. Проверяется соединился ли клиент
     * 2. Проверяется вызвался ли метод DispatcherRequestImpl.dispatchRequest
     * 3. Проверяется тот ли объект Socket отправляется методу DispatcherRequestImpl
     * 4. Проверяется, в другом ли потоке вызывается метод DispatcherRequestImpl.dispatchRequest
     */
    @Test
    public void testStart() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.openMocks(this);
        WebServer webServer = new WebServer(dispatcherRequest);
        Field port = webServer.getClass().getDeclaredField("port");
        port.setAccessible(true);
        port.set(webServer, "8080");
        Thread workingServerThread = new Thread(webServer::start);

        doAnswer(invocation -> {
            Thread handleRequestThread = Thread.currentThread();
            assertNotEquals(handleRequestThread, workingServerThread);
            return null;
        }).when(dispatcherRequest).dispatchRequest(any(Socket.class));
        workingServerThread.start();
        try {
            Thread.sleep(2000);
            assertTrue(webServer.isStarted());
            Socket socket = new Socket("127.0.0.1", 8080);
            assertTrue(socket.isConnected());
            verify(dispatcherRequest, timeout(3000).times(1)).dispatchRequest(captor.capture());
            Socket socketCaptor = captor.getValue();
            assertArrayEquals(socket.getInetAddress().getAddress(), socketCaptor.getInetAddress().getAddress());
            socket.close();
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
        webServer.stop();
    }
}
