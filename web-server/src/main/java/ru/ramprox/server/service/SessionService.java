package ru.ramprox.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ramprox.server.config.Environment;
import ru.ramprox.server.config.PropertyName;
import ru.ramprox.server.model.Session;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

public class SessionService {

    private Map<UUID, Session> sessions = new ConcurrentHashMap<>();

    private ScheduledExecutorService executorService;
    private Semaphore semaphore = new Semaphore(1);
    private static final Logger logger = LoggerFactory.getLogger(SessionService.class);

    public SessionService() {
        createSessionStorage();
        loadSessionsFromStorage();
        Runtime.getRuntime().addShutdownHook(new Thread(this::saveSessionsInStorage));
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(this::periodicalClearSession, 0, 10, TimeUnit.MINUTES);
    }

    public void add(UUID uuid, Session session) {
        sessions.put(uuid, session);
    }

    public Session getSession(UUID uuid) {
        return sessions.get(uuid);
    }

    /**
     * Создание хранилища для сессии
     */
    private void createSessionStorage() {
        Path pathToSessionStorage = Paths.get(Environment.getProperty(PropertyName.PATH_TO_SESSIONS));
        if(!Files.exists(pathToSessionStorage)) {
            try {
                Files.createDirectory(pathToSessionStorage.getParent());
                Files.createFile(pathToSessionStorage);
            } catch (IOException ex) {
                logger.error("Can't create directory for session data storage: {}", ex.getMessage());
            }
        }
    }

    /**
     * Загрузка сессии из хранилища
     */
    private void loadSessionsFromStorage() {
        File file = new File(Environment.getProperty(PropertyName.PATH_TO_SESSIONS));
        if(file.length() > 0) {
            try (InputStream is = new FileInputStream(file);
                 ObjectInputStream oos = new ObjectInputStream(is)) {
                sessions = (Map<UUID, Session>) oos.readObject();
            } catch (IOException ex) {
                logger.error("Can't load sessions data from storage: {}", ex.getMessage());
            } catch (ClassNotFoundException ex) {
                logger.error("Can't convert session data content: {}", ex.getMessage());
            }
        }
    }

    /**
     * Сохранение сессии в хранилище
     */
    public void saveSessionsInStorage() {
        executorService.shutdown();
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            logger.error("Interrupted exception");
        }
        File file = new File(Environment.getProperty(PropertyName.PATH_TO_SESSIONS));
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            logger.info("Saving sessions data in storage");
            oos.writeObject(sessions);
            oos.flush();
        } catch (Exception ex) {
            logger.error("Can't save sessions data in storage: {}", ex.getMessage());
        }
    }

    /**
     * Периодическая очистка сессии
     */
    private void periodicalClearSession() {
        if(semaphore.tryAcquire()) {
            logger.info("Clearing session...");
            sessions.keySet()
                    .forEach(uuid -> sessions.compute(uuid, (uuid1, session) -> {
                        LocalDateTime now = LocalDateTime.now();
                        if (session.getExpireDate().compareTo(now) < 0) {
                            return null;
                        }
                        return session;
                    }));
            semaphore.release();
        }
    }
}
