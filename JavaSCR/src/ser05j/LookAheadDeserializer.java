package ser05j;

import javax.naming.ConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
//import org.apache.commons.io.serialization.ValidatingObjectInputStream;
// import org.apache.commons.configuration.ConfigurationException;

/**
 * A simple Java program to demonstrate how to perform input validation on
 * serialized binary buffers. Specifically, we only want to allow instances of
 * the Bicycle class to be deserialized.
 * 
 * @author Pierre Ernst
 * 
 */
public class LookAheadDeserializer {

  private static byte[] serialize(Object obj) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(obj);
    byte[] buffer = baos.toByteArray();
    oos.close();
    baos.close();
    return buffer;
  }

  /*
  private static Object deserialize(byte[] buffer) throws IOException, ClassNotFoundException, ConfigurationException {
    Object obj;
    try (ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
         ObjectInputStream ois = new ObjectInputStream(bais);) {
      obj = ois.readObject();
    }
    return obj;
  }
  */
  private static Object deserialize(byte[] buffer) throws IOException, ClassNotFoundException, ConfigurationException {
    Object obj;
    try (ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
         ObjectInputStream ois = new ObjectInputStream(bais)) {
      ois.setObjectInputFilter(new BikeFilter());
      obj = ois.readObject();
    }
    return obj;
  }

  public static void main(String[] args) {
    try {
      // Serialize a Bicycle instance
      byte[] serializedBicycle = serialize(new Bicycle(0, "Unicycle", 1));

      // Serialize a File instance
      byte[] serializedFile = serialize(new File("file.txt"));

      // Deserialize the Bicycle instance (legitimate use case)
      Bicycle bicycle0 = (Bicycle) deserialize(serializedBicycle);
      System.out.println(bicycle0.getName() + " has been deserialized.");

      // Deserialize the File instance (error case)
      @SuppressWarnings("unused")
      Object bicycle1 = (Object) deserialize(serializedFile);

    } catch (IOException | ClassNotFoundException | ConfigurationException ex) {
      ex.printStackTrace(System.err);
    }
  }  // end main
}  // end LookAheadDeserializer











































/*
private static Object deserialize(byte[] buffer) throws IOException, ClassNotFoundException, ConfigurationException {
  Object obj;
  try (ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
      // Use ValidatingObjectInputStream instead of InputStream
      ValidatingObjectInputStream ois = new ValidatingObjectInputStream(bais);) {
    ois.accept(Bicycle.class);
    obj = ois.readObject();
  }
  return obj;
}
*/