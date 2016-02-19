package net.ludvicek.kb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

/**
 * Created by jludvice on 11.9.15.
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    /**
     * Example main method.
     *
     * @param args first argument is path to input device (eg system keyboard or barcode scanner
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        if (args.length != 1) {
            System.err.println("Pass path to input device as first argument");
            System.exit(1);
        }


        String device = args[0];
        Listener l = Listener.forPath(device);
        Runnable r = () -> {
            try {
                l.stopListening();
            } catch (Exception e) {
                logger.error("Failed to stop listening", e);
            }
        };
        Runtime.getRuntime().addShutdownHook(new Thread(r));

        InputStream is = Main.class.getClassLoader().getResourceAsStream("mapping.properties");

        KeyCodeMapper mapper = KeyCodeMapper.fromMapping(is);
        is.close();

        l.addListener(i -> {
            System.out.println(String.format("Keycode: %s, key: %s", i, mapper.keyValue(i)));
        });

        l.listen();
    }
}
