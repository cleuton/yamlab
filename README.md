# YAML Simplificado
[**Cleuton Sampaio**](https://github.com/cleuton) - [**LinkedIn**](https://www.linkedin.com/in/cleutonsampaio/) 

**YAML** significa: YAML Ain't Markup Language™

É um formato de serialização/desserialização de informações, compatível com todas as linguagens de programação. Possui recursos de tipagem de valores e é baseado em identações.

Tem sido muito utilizado para configuração de aplicações, entre elas o **Kubernetes** (**k8s**), mas pode ser utilizado com um pouco de esforço em qualquer linguagem de programação. Neste tutorial, eu mostro como utilizar YAML em **Java**.

Neste tutorial, usarei o [**SnakeYaml**](https://bitbucket.org/snakeyaml/snakeyaml-engine/src/master/) para ler arquivos YAML.

## Estrutura: 

Um arquivo YAML é uma coleção de documentos. É possível separarmos documentos com "---". Podemos ter vários tipos de objetos dentro de um arquivo YAML. Vou falar dos mais comuns:

### Listas simples
```yaml
- orange
- apple
- banana
```
Neste exemplo temos um documento único composto por uma lista de Strings. Ele é entendido por um programa Java como algo assim: 
```json
["orange", "apple", "banana"]
```

Ou, mais especificamente: 
```text
List<String>
```

O código para decodificar este arquivo YAML está na [**classe Main**](./src/main/java/com/cleutonsampaio/yamlab/Main.java): 
```text
    public void implicitList() {
        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass()
          .getClassLoader()
          .getResourceAsStream("implicitlist.yml");
        List<String> list = (List<String>) yaml.load(inputStream);
        System.out.println(list);
    }
```
Simplesmente pegamos o resultado do método **load** do objeto **Yaml** que criamos e fazemos um **cast** para **List<String>**.

### Múltiplos documentos

Podemos ter múltiplos documentos (ou objetos) dentro de um arquivo YAML. Por exemplo: 
```yaml
---
- apple
- orange
- banana
---
- peanut
- blueberry
```

Neste exemplo, temos duas listas separadas, uma com 3 elementos e outra com 2. Isso seria entendido por um programa Java como dois objetos separados: 
```json
["apple", "orange", "banana"]
["peanut", "blueberry"]
```

Ou
```text
List<String>
List<String>
```

Esta implementação está no método abaixo, da [**classe Main**](./src/main/java/com/cleutonsampaio/yamlab/Main.java):
```text
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
```
Usamos o método **loadAll** dentro de um loop. Para cada objeto, podemos simplesmente fazer um **cast** para **List** e trabalhar com ele. 

### Block maps

Block maps possuem uma chave e um valor, que pode ser um objeto com propriedades. Vejamos este exemplo:
```yaml
npc1:
  name: grok
  type: snake
  size: 20
npc2:
  name: drb
  type: dragon
  size: 50
```

Aqui temos dois blocos mapeando algumas propriedades. Eles seriam entendidos como algo assim: 
```json
{"npc1" : {"name": "grok", "type": "snake",  "size": 20},
 "npc2" : {"name": "drb",  "type": "dragon", "size": 50}}
```

É um dicionário contendo **String** como chave e outro dicionário como valor. Em Java isso seria entendido assim:
```text
Map<String, Object>
```
Esta implementação está no método abaixo, da [**classe Main**](./src/main/java/com/cleutonsampaio/yamlab/Main.java):
```text
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
```

Como recebemos um mapa (Map<String, Object>) podemos iterar sobre ele pegando cada chave ("npc1" e "npc2") e seus valores. Note que os valores também são mapas!

### Block sequence

Um bloco de propriedades demarcado por um hífen. Vejamos um exemplo: 
```yaml
- objectId: 1
  type: castle
  height: 10
  width: 15
- objectId: 2
  type: tree
  height: 7
  width: 2
- objectId: 3
  type: house
  height: 7
  width: 10
```

Aqui separamos os blocos de atributos utilizando hifens. Este YAML seria entendido como algo assim: 
```json
[{"objectId": 1,"type": "castle","height": 10,"width": 15},
 {"objectId": 2,"type": "tree"  ,"height": 7, "width": 2},
 {"objectId": 3,"type": "house" ,"height": 7, "width": 10}]
```
E seria mapeado em Java desta forma:
```text
List of Map<String, Object>
```
Esta implementação está no método abaixo, da [**classe Main**](./src/main/java/com/cleutonsampaio/yamlab/Main.java):
```text
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
```

É uma lista de mapa. Então podemos utilizar dois **forEach** encadeados: Um para cada elemento da lista, e outro para cada par chave-valor do mapa.

### Custom classes

Podemos ter utilizar classes criadas por nós para carregarmos YAML. O processo requer criarmos uma classe auxiliar para instanciarmos nossas classes particulares. Por exemplo: 
```yaml
!Config
serverUrl: https://server1:8080
port: 8080
users:
  user1:
    email: user1@test
    isAdmin: false
  user2:
    email: user2@test
    isAdmin: true
```
Neste exemplo estamos marcando o arquivo com uma **tag** customizada ("!Config") que será processada pela nossa classe auxiliar (veremos exemplo no código).

Aqui está a classe Config: 
```text
public class Config {
    public String serverUrl;
    public int port;
    public Map<String, User> users;

    @Override
    public String toString() {
        return "Config{" + "serverUrl='" + serverUrl + '\'' + ", port=" + port + ", users=" + users + '}';
    }
}
```

E aqui, a classe User:
```text
public class User {
    public String email;
    public boolean isAdmin;

    @Override
    public String toString() {
        return "User{" + "email='" + email + '\'' + ", isAdmin=" + isAdmin + '}';
    }
}
```

Esta implementação está no método abaixo, da [**classe Main**](./src/main/java/com/cleutonsampaio/yamlab/Main.java):
```text
    public void customClass() {
        Yaml yaml = new Yaml(new ConfigConstructor());
        InputStream inputStream = this.getClass()
          .getClassLoader()
          .getResourceAsStream("customClass.yml");
        Config config = (Config) yaml.load(inputStream);
        System.out.println(config);
    }
```

Parece simples, não? Calma... Tive que criar uma classe que extende **org.yaml.snakeyaml.constructor.Constructor** para lidar com as nossas classes: **Config** e **User**: 
```text
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
```

No método **construct** eu recebo um **Node** do YAML e o transformo em instâncias das nossas classes, retornando uma instância de **Config**. Quanto eu for fazer um **parse** no YAML, tenho que criar uma instância dessa classe auxiliar: 
```text
Yaml yaml = new Yaml(new ConfigConstructor());
```

E posso simplesmente fazer um **cast** para nossa classe **Config**: 
```text
Config config = (Config) yaml.load(inputStream);
```

Mas note que eu criei uma **custom tag** para identificar o construtor que trabalharia a nossa classe: 
```text
this.yamlConstructors.put(new Tag("!Config"), new ConstructCustomObject());
```

É por esta razão que o arquivo YAML começa com essa Tag: 
```yaml
!Config
serverUrl: https://server1:8080
port: 8080
users:
...
```