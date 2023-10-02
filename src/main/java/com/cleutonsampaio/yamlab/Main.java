package com.cleutonsampaio.yamlab;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;

public class Main {

    public void implicitList() {
        /* Single List
        ["orange", "apple", "banana"]
         */
        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass()
          .getClassLoader()
          .getResourceAsStream("implicitlist.yml");
        List<String> list = (List<String>) yaml.load(inputStream);
        System.out.println(list);
    }

    public void multipleObjects() {
        /* Separated lists
        ["apple", "orange", "banana"]
        ["peanut", "blueberry"]
         */
        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass()
          .getClassLoader()
          .getResourceAsStream("multipleObjects.yml");
        int count = 0;
        for (Object object : yaml.loadAll(inputStream)) {
            System.out.println("Object " + (++count)
                                + ", (" + object.getClass().getName() + ") "
                                + ": "
                                + ((List) object));
        }
    }

    public void blockMaps() {
        /*
        Map<String, Object>
        {"npc1" : {"name": "grok", "type": "snake",  "size": 20}
         "npc2" : {"name": "drb",  "type": "dragon", "size": 50}
         */
        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass()
          .getClassLoader()
          .getResourceAsStream("blockMap.yml");
        Map<String, Object> maps = (Map<String, Object>) yaml.load(inputStream);
        maps.forEach((k, v) -> System.out.println((k + ":" + v)));
    }

    public void blockSequence() {
        /*
        List of Map<String, Object>
        [{"objectId": 1,"type": "castle","height": 10,"width": 15},
         {"objectId": 2,"type": "tree"  ,"height": 7, "width": 2},
         {"objectId": 3,"type": "house" ,"height": 7, "width": 10}]
         */
        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass()
          .getClassLoader()
          .getResourceAsStream("blockSequence.yml");
        Object block = yaml.load(inputStream);
        ((List)block).forEach(b ->
            ((Map<String, Object>) b).forEach((k,v) -> System.out.println(k + ": " + v)));
    }

    class ConfigConstructor extends Constructor {
        ConfigConstructor() {
            super(new LoaderOptions());
            this.yamlConstructors.put(new Tag("!Config"), new ConstructCustomObject());
        }

        private class ConstructCustomObject extends AbstractConstruct {
            public Object construct(Node node) {
                final Map<Object, Object> values = constructMapping((MappingNode) node);
                final String serverUrl = (String) values.get("serverUrl");
                final int port = (int) values.get("port");
                final Map<String, User> users = (Map<String, User>) values.get("users");
                Config config = new Config();
                config.serverUrl = serverUrl;
                config.port = port;
                config.users = users;
                return config;
            }
        }
    }

    public void customClass() {
        Yaml yaml = new Yaml(new ConfigConstructor());
        InputStream inputStream = this.getClass()
          .getClassLoader()
          .getResourceAsStream("customClass.yml");
        Config config = (Config) yaml.load(inputStream);
        System.out.println(config);
    }

    public static void main(String[] args) {
        Main m = new Main();
        System.out.println("Implicit list:");
        m.implicitList();
        System.out.println("Multiple objects:");
        m.multipleObjects();
        System.out.println("Block maps:");
        m.blockMaps();
        System.out.println("Block sequence:");
        m.blockSequence();
        System.out.println("Custom class:");
        m.customClass();
    }
}