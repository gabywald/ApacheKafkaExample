package gabywald.kafka.formation.gtc.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gabywald.kafka.formation.gtc.example.model.GTCDataLoader;

/**
 * Properties loader. 
 * <br/>See https://stackoverflow.com/questions/4689779/how-to-parse-property-value-in-properties-file
 * <br/>See https://www.tutorialspoint.com/log4j/log4j_configuration.htm
 * @author Gabriel Chandesris (20180625)
 */
public class GTCPropertiesLoader {

  private static final Logger log = LoggerFactory.getLogger(GTCPropertiesLoader.class);
  private Properties props;
  // private Map<String, String> values;

  public static void main(String args[]) {
    GTCPropertiesLoader pl01 = new GTCPropertiesLoader();
    pl01.loadProps(GTCDataLoader.PATH_BASE + "gtcProducer.properties");
    GTCPropertiesLoader pl02 = new GTCPropertiesLoader();
    pl02.loadProps(GTCDataLoader.PATH_BASE + "gtcConsumer.properties");
  }

  public enum GTCConfigProperties {

    BOOTSRAP_SERVERS("bootstrap.servers"), // 
    KEY_SERIALIZER("key.serializer"), // 
    VALUE_SERIALIZER("value.serializer"), // 
    TOPIC_NAME("topic.name"), // 
    KEY_DESERIALIZER("key.deserializer"), // 
    VALUE_DESERIALIZER("value.deserializer"), // 
    GROUP_ID("group.id"), // 
    CLIENT_ID("client.id");

    private String name;

    private GTCConfigProperties(String name) {
      this.name = name;
    }

    public String getName() {
      return this.name;
    }

    public String toString() {
      return this.name;
    }
  }

  public void loadProps(String propertiesFilePath) {
    try {
      // this.getClass().getResourceAsStream(propertiesFilePath);
      File propsFile = new File(propertiesFilePath);
      GTCPropertiesLoader.log.debug("[" + propsFile.exists() + "]\t{" + propsFile.getAbsolutePath() + "}");
      FileInputStream propsFileIS = new FileInputStream(propertiesFilePath);
      InputStream is = propsFileIS; // this.getClass().getResourceAsStream( propsFile.getAbsolutePath() );
      //			if (is == null) {
      //				PropertiesLoader.log.error( "{" + propertiesFilePath + "} not found !" );
      //				
      //				System.out.println("Working Directory = [" + System.getProperty("user.dir") + "]");
      //				File local = new File( System.getProperty("user.dir") );
      //				if (local.isDirectory()) {
      //					Arrays.asList( local.list() ).stream().forEach( PropertiesLoader.log::info );
      //				}
      //				
      //				File resources = new File( System.getProperty("user.dir") + "/" + "resources" );
      //				if (resources.isDirectory()) {
      //					Arrays.asList( resources.list() ).stream().map(s -> "resources/" + s).forEach( PropertiesLoader.log::info );
      //				}
      //				return;
      //			} // END "if (is == null)"
      this.props = new Properties();
      this.props.load(is);
      is.close();
      //			this.props.keySet().stream().sorted().forEach( System.out::println );
      this.props.keySet().stream().sorted().map(n -> n + "->" + this.props.get(n)).forEach(GTCPropertiesLoader.log::info);
      // this.props.keySet().stream().sorted().forEach( n -> this.values.put(n, this.props.get(n)) );
    } catch (IOException ioe) {
      GTCPropertiesLoader.log.error("IOException in loadProps");
      for (StackTraceElement ste : ioe.getStackTrace()) {
        GTCPropertiesLoader.log.error(ste.toString());
      }
    }
  }

  public String get(String key) {
    return (String) this.props.get(key);
  }

  public Set<String> keySet() {
    return this.props.keySet().stream().map(n -> n.toString()).collect(Collectors.toSet());
  }
}
/*

System.out.println("Working Directory = [" + System.getProperty("user.dir") + "]");



Set<Object> properties = System.getProperties().keySet();

Iterator<Object> iter = properties.iterator();

while (iter.hasNext()) {

                Object current                  = iter.next();

                String stringify   = current.toString();

                System.out.println("\t {" + stringify + "} => {" + System.getProperty( stringify ) + '}' );

}





++  .class.getClassLoader().getResourceAsStream ++

*/
/*
https://stackoverflow.com/questions/4689779/how-to-parse-property-value-in-properties-file


You can put your key/value pairs in a properties file like this:

dbUrl = yourURL
username = yourusername
password = yourpassword

Then you can load them into your app from the properties file:

private void loadProps() {
    try {
        InputStream is = getClass().getResourceAsStream("database_props.properties");
        props = new Properties();
        props.load(is);
        is.close();
        dbConnStr = props.getProperty("dbUrl");
        username = props.getProperty("username");
        password = props.getProperty("password");
    }
    catch(IOException ioe) {
        log.error("IOException in loadProps");
        for(StackTraceElement ste : ioe.getStackTrace())
        log.error(ste.toString());
    }
}
*/
