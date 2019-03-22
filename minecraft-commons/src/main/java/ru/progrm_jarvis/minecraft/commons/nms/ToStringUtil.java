package ru.progrm_jarvis.minecraft.commons.nms;

import com.comphenix.packetwrapper.AbstractPacket;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import lombok.val;
import ru.progrm_jarvis.reflector.wrapper.FieldWrapper;
import ru.progrm_jarvis.reflector.wrapper.MethodWrapper;
import ru.progrm_jarvis.reflector.wrapper.fast.FastMethodWrapper;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ToStringUtil {

    private String FIELDS_CACHE_CONCURRENCY_LEVEL_PROPERTY_NAME
            = ToStringUtil.class.getCanonicalName().concat(".FIELDS_CACHE_CONCURRENCY_LEVEL"),
            METHODS_CACHE_CONCURRENCY_LEVEL_PROPERTY_NAME
                    = ToStringUtil.class.getCanonicalName().concat(".METHODS_CACHE_CONCURRENCY_LEVEL");

    private Cache<Class<?>, Map<String, FieldWrapper<Object, Object>>> FIELDS_CACHE = CacheBuilder.newBuilder()
            .concurrencyLevel(Integer.parseInt(MoreObjects.firstNonNull(
                    System.getProperty(FIELDS_CACHE_CONCURRENCY_LEVEL_PROPERTY_NAME), "2")
            ))
            .build();

    private Cache<Class<?>, Map<String, MethodWrapper<Object, Object>>> METHODS_CACHE = CacheBuilder.newBuilder()
            .concurrencyLevel(Integer.parseInt(MoreObjects.firstNonNull(
                    System.getProperty(METHODS_CACHE_CONCURRENCY_LEVEL_PROPERTY_NAME), "2")
            ))
            .build();

    @SneakyThrows
    public String toString(final AbstractPacket packet) {
        if (packet == null) return "null";

        final Map<String, MethodWrapper<Object, Object>> methods;
        final String className;
        {
            val clazz = packet.getClass();
            methods = METHODS_CACHE.get(clazz, () -> Arrays.stream(clazz.getDeclaredMethods())
                    .filter(method -> method.getName().startsWith("get"))
                    .collect(Collectors.toMap(
                            method -> getterNameToString(method.getName()),
                            (Function<Method, MethodWrapper<Object, Object>>) FastMethodWrapper::from
                    )));
            className = clazz.getName();
        }


        if (methods.isEmpty()) return className + "{}";

        val stringBuilder = new StringBuilder(className)
                .append('{');

        {
            val entries = methods.entrySet().iterator();
            boolean hasNext = entries.hasNext();
            while (hasNext) {
                val entry = entries.next();
                // invoked before append() to exit if an exception occurs without any unneeded operations
                val value = entry.getValue().invoke(packet);

                stringBuilder
                        .append(entry.getKey())
                        .append(value);

                hasNext = entries.hasNext();
                if (hasNext) stringBuilder
                        .append(',')
                        .append(' ');
            }
        }

        return stringBuilder
                .append('}')
                .toString();
    }

    public String getterNameToString(@NonNull final String getterName) {
        if (getterName.startsWith("get")) {
            val name = getterName.substring(3);
            if (name.length() == 0) return "get";
            else return name.substring(0, 1).toLowerCase() + name.substring(1);
        } else return getterName;
    }

}
