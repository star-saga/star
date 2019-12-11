package org.event.driven.light.kafkaserialize.serialize;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import org.event.driven.light.kafkaserialize.common.OmegaException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class KryoMessageFormat {
    private static final int DEFAULT_BUFFER_SIZE = 4096;

    private static final KryoFactory factory = new KryoFactory() {
        @Override
        public Kryo create() {
            return new Kryo();
        }
    };

    private static final KryoPool pool = new KryoPool.Builder(factory).softReferences().build();

    public static byte[] serialize(Object[] objects) {
        Output output = new Output(DEFAULT_BUFFER_SIZE, -1);

        Kryo kryo = pool.borrow();
        kryo.writeObjectOrNull(output, objects, Object[].class);
        pool.release(kryo);

        return output.toBytes();
    }

    public static Object[] deserialize(byte[] message) {
        try {
            Input input = new Input(new ByteArrayInputStream(message));

            Kryo kryo = pool.borrow();

            Object[] objects = kryo.readObjectOrNull(input, Object[].class);
            pool.release(kryo);

            return objects;
        } catch (KryoException e) {
            throw new OmegaException("Unable to deserialize message", e);
        }
    }
}
