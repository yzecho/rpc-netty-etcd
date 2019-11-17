package io.yzecho.rpcnettyetcd.util;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

/**
 * @author: yzecho
 * @desc
 * @date: 17/11/2019 10:53
 */
public class ProtoStuffUtil {

    public static <T> byte[] serializer(T t) {
        Schema schema = RuntimeSchema.getSchema(t.getClass());
        return ProtostuffIOUtil.toByteArray(t, schema, LinkedBuffer.allocate());
    }

    public static <T> T deserializer(byte[] bytes, Class<T> clazz) {
        Schema<T> schema = RuntimeSchema.createFrom(clazz);
        T message = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes, message, schema);
        return message;
    }
}
