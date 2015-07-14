package cn.chinahadoop.streaming;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericArray;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.apache.avro.util.Utf8;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.FlumeException;
import org.apache.flume.api.RpcClient;
import org.apache.flume.api.RpcClientFactory;
import org.apache.flume.event.EventBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by ak on 11/19/14.
 */
public class Generator {

    private Schema root;
    private String host = "localhost";
    private int port = 41414;
    private RpcClient client;
    private ByteArrayOutputStream out;
    private DatumWriter<Object> writer;
    private BinaryEncoder encoder;

    public Generator(Schema schema) {
        this.root = schema;
    }

    public Generator(Schema schema, String host, int port) {
        this.root = schema;
        this.host = host;
        this.port = port;
        this.client = RpcClientFactory.getDefaultInstance(host, port);
    }

    private static Object generate(Schema schema, Random random, int d) {
        switch (schema.getType()) {
            case RECORD:
                GenericRecord record = new GenericData.Record(schema);
                for (Schema.Field field : schema.getFields())
                    record.put(field.name(), generate(field.schema(), random, d + 1));
                return record;
            case ENUM:
                List<String> symbols = schema.getEnumSymbols();
                return symbols.get(random.nextInt(symbols.size()));
            case ARRAY:
                int length = (random.nextInt(5) + 2) - d;
                GenericArray<Object> array =
                        new GenericData.Array(length <= 0 ? 0 : length, schema);
                for (int i = 0; i < length; i++)
                    array.add(generate(schema.getElementType(), random, d + 1));
                return array;
            case MAP:
                length = (random.nextInt(5) + 2) - d;
                Map<Object, Object> map = new HashMap<Object, Object>(length <= 0 ? 0 : length);
                for (int i = 0; i < length; i++) {
                    map.put(randomUtf8(random, 40),
                            generate(schema.getValueType(), random, d + 1));
                }
                return map;
            case UNION:
                List<Schema> types = schema.getTypes();
                return generate(types.get(random.nextInt(types.size())), random, d);
            case STRING:
                return randomUtf8(random, 40);
            case BYTES:
                return randomBytes(random, 40);
            case INT:
                return random.nextInt();
            case LONG:
                long number = random.nextLong();
                number = (number < 0 ? -number : number);
                return number;
            case FLOAT:
                return random.nextFloat();
            case DOUBLE:
                return random.nextDouble();
            case BOOLEAN:
                return random.nextBoolean();
            case NULL:
                return null;
            default:
                throw new RuntimeException("Unknown type: " + schema);
        }
    }


    private static Utf8 randomUtf8(Random rand, int maxLength) {
        Utf8 utf8 = new Utf8().setLength(rand.nextInt(maxLength));
        for (int i = 0; i < utf8.getLength(); i++) {
            utf8.getBytes()[i] = (byte) ('a' + rand.nextInt('z' - 'a'));
        }
        return utf8;
    }

    private static ByteBuffer randomBytes(Random rand, int maxLength) {
        ByteBuffer bytes = ByteBuffer.allocate(rand.nextInt(maxLength));
        bytes.limit(bytes.capacity());
        rand.nextBytes(bytes.array());
        return bytes;
    }

    public void generateEventsToStdout(long eventsPerSecond, int numberOfSeconds) {
        long sleep = 1000L/eventsPerSecond;
        for(int i=0; i<numberOfSeconds; i++) {
            for (int j = 0; j < eventsPerSecond; j++) {
                System.out.println(generate(root, new Random(System.currentTimeMillis()), 0).toString());
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void generateFlumeEvents(long eventsPerSecond, int numberOfSeconds){
        long sleep = 1000L/eventsPerSecond;
        for(int i=0; i<numberOfSeconds; i++) {
            for (int j = 0; j < eventsPerSecond; j++) {
                sendDataToFlume(generate(root, new Random(System.currentTimeMillis()), 0));
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        cleanUp();
    }

    private void sendDataToFlume(Object datum) {
        // Create a Flume Event object that encapsulates the sample data
        out = new ByteArrayOutputStream();
        writer = new ReflectDatumWriter<Object>(root);
        encoder = EncoderFactory.get().binaryEncoder(out, null);
        out.reset();
        try {
            writer.write(datum, encoder);
            encoder.flush();
            out.toByteArray();
        } catch (IOException e) {
            throw new FlumeException(e);
        }
        Map<String, String> hdrs = new HashMap<String, String>();
        Event event = EventBuilder.withBody(out.toByteArray(), hdrs);

        // Send the event
        try {
            client.append(event);
        } catch (EventDeliveryException e) {
            // clean up and recreate the client
            client.close();
            client = null;
            client = RpcClientFactory.getDefaultInstance(host, port);
        }
    }

    private void cleanUp() {
        // Close the RPC connection
        client.close();
    }

    public static void main(String[] args) {
        Generator gen = null;
        try {
            gen = new Generator(Schema.parse(new File(args[0])), "172.27.7.209", 41415);
        } catch (IOException e) {
            e.printStackTrace();
        }
       // gen.generateEventsToStdout(10, 2);
        gen.generateFlumeEvents(1,5);
    }
}
