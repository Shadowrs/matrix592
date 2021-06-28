package com.rs.utils;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class ConfigLoader {

    public static Map<String, String> properties;

    public static boolean hasProperty(String p) {
        return ConfigLoader.properties.containsKey(p);
    }

    public static <T> T prop(String key) {
        return (T) ConfigLoader.properties.get(key);
    }

    public static int intProp(String key) {
        return Integer.parseInt(ConfigLoader.properties.get(key));
    }

    public static boolean boolProp(String key) {
        return Boolean.parseBoolean(ConfigLoader.properties.get(key));
    }

    public static void load() {
        long start = System.currentTimeMillis();
        properties = new HashMap<>();
        try {
            parse();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        //System.out.println(new PrettyPrintingMap(properties));
        System.out.printf("Game properties ... %d (%dms) [%s] %n", properties.size(), System.currentTimeMillis() - start, "configuration.cfg");
    }

    public static File getResourceAsFile(String resourcePath) {
        try {
            InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(resourcePath);
            if (in == null) {
                in = ConfigLoader.class.getResourceAsStream(resourcePath);
            }
            if (in == null) {
                return null;
            }

            File tempFile = File.createTempFile(String.valueOf(in.hashCode()), ".tmp");
            tempFile.deleteOnExit();

            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                //copy stream
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Loads server configuration.
     *
     * @throws IOException            if an I/O error occurs.
     * @throws ClassNotFoundException if a class loaded through reflection was not found.
     * @throws IllegalAccessException if a class could not be accessed.
     * @throws InstantiationException if a class could not be created.
     */
    public static void parse() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        File f = new File("configuration.cfg");
        if (!f.exists()) {
            f = getResourceAsFile("configuration.cfg");
        }
        System.out.println("configuration is at "+f.getAbsolutePath());
        if (!f.exists()) {
            System.err.println("Missing configuration file.");
            return;
        }
        FileInputStream fis = new FileInputStream(f);
        try {
            ConfigurationParser parse = new ConfigurationParser(fis);
            Map<String, String> mappings = parse.getMappings();
            Iterator<Entry<String, String>> it = mappings.entrySet().iterator();
            while (it.hasNext()) {
                Entry<String, String> e = it.next();
                properties.put(
                        e.getKey(),
                        e.getValue());
            }

        } finally {
            fis.close();
        }
    }
}
