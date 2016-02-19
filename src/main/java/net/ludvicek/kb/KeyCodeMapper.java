package net.ludvicek.kb;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

/**
 * Created by jludvice on 28.9.15.
 */
public class KeyCodeMapper {

    private final Map<Integer, String> mapping;


    /**
     * Create keycode mapper from given Map, where key is keyCode.
     *
     * @param mapping
     */
    public KeyCodeMapper(Map<Integer, String> mapping) {
        this.mapping = mapping;
    }

    /**
     * Create new Mapper from property file input stream.
     * <p>
     * Property file should be in format keyCode=key (eg {@code 32=D}).
     *
     * @param propertiesStream properties file input stream
     * @return new KeyCodeMapper
     * @throws IOException
     */
    public static KeyCodeMapper fromMapping(InputStream propertiesStream) throws IOException {
        Properties p = new Properties();
        p.load(propertiesStream);

        Enumeration<String> keys = (Enumeration<String>) p.propertyNames();
        Map<Integer, String> mapping = new TreeMap<>();

        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            mapping.put(Integer.decode(key), p.getProperty(key));
        }
        return new KeyCodeMapper(mapping);
    }

    /**
     * Get key value for given key code
     *
     * @param keyCode keycode from input event
     * @return string value for given keycode (eg "A", "1", "SHIFT", ...)
     */
    public String keyValue(Integer keyCode) {
        return mapping.get(keyCode);
    }


    public Map<Integer, String> getMapping() {
        return mapping;
    }
}
