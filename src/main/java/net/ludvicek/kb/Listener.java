package net.ludvicek.kb;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * Created by jludvice on 11.9.15.
 */
public class Listener implements Callable<String> {
    private static final Logger logger = LoggerFactory.getLogger(Listener.class);

    public static final int ET_KEY_EVENT = 1;
    public static final int ET_KEY_RELEASE = 0;

    private String path = null;
    DataInputStream dis = null;

    private Future<String> f = null;
    ExecutorService backend = null;
    private List<Consumer<Integer>> listeners;


    Listener(String path) {
        this.path = path;
        this.listeners = new LinkedList<>();
    }

    public static Listener forPath(final String path) {
        return new Listener(path);
    }

    public void addListener(Consumer<Integer> consumer) {
        this.listeners.add(consumer);
    }

    private static int unsigned16ToInt(byte b1, byte b2) {
        // TODO(ludvicekj) there might be issue when running on system with different little/big endian
        return (b2 << 1) ^ b1;
    }

    public void listen() throws FileNotFoundException {
        if (backend != null) {
            logger.error("Don't call listen() multiple times. Ignoring this call");
            return;
        }
        backend = Executors.newSingleThreadExecutor();
        File device = new File(path);
        logger.info("opening device {} which exists? {}", device, device.exists());
        dis = new DataInputStream(new FileInputStream(device));

        f = backend.submit(this);
        //not expecting other threads
        backend.shutdown();
    }

    public void stopListening() throws ExecutionException, InterruptedException {
        logger.info("Stopping keyboard watcher.");
        this.listeners = null;

        try {
            logger.info("Closing input stream {}", path);
            dis.close();
        } catch (IOException e) {
            logger.error("Failed to close input stream for path {}", path, e);
        }

        f.cancel(true);
        if (!f.isCancelled()) {
            logger.error("Worker {} should have been canceled.", f);
        }
        f = null;
        backend.shutdownNow();
        logger.info("Listener should be stopped. isShutdown(): {}, isTerminated(): {}", backend.isShutdown(), backend.isTerminated());
    }


    private void notify(int i) {
        for (Consumer<Integer> listener : listeners) {
            listener.accept(i);
        }
    }


    @Override
    public String call() throws Exception {

        byte[] inputEvent = new byte[24];
        byte[] time = new byte[16];

        while (true) {
            dis.read(inputEvent);
            ByteBuffer event = ByteBuffer.wrap(inputEvent);

            event.get(time);
            int type = unsigned16ToInt(event.get(), event.get());
            int code = unsigned16ToInt(event.get(), event.get());
            int value = event.getInt();

            if (type == ET_KEY_EVENT && value == ET_KEY_RELEASE) { //key event && key release
                //notify listeners on keypress
                notify(code);
            }
        }
    }
}
