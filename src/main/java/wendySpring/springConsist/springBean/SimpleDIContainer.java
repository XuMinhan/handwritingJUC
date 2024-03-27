package wendySpring.springConsist.springBean;

import wendySpring.springConsist.wendyNetty.processors.httpProcessor.Controller;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleDIContainer {
    private static final Map<Class<?>, Object> singletonInstances = new ConcurrentHashMap<>();
    private static final Map<Class<?>, String> scopeDefinitions = new HashMap<>();

    public static <T> T getInstance(Class<T> classType) {
        // 检查是否定义了作用域为prototype
        if ("prototype".equals(scopeDefinitions.get(classType))) {
            return createInstance(classType, false);
        }
        // 默认单例逻辑
        return (T) singletonInstances.computeIfAbsent(classType, key -> createInstance(key, true));
    }

    private static <T> T createInstance(Class<T> classType, boolean isSingleton) {
        // 检查类是否有@Component注解，如果没有，则抛出异常
        if (!(classType.isAnnotationPresent(Component.class) ||
                classType.isAnnotationPresent(Controller.class))) {
            throw new IllegalArgumentException("Class " + classType.getName() + " is not annotated with @Component and cannot be instantiated by SimpleDIContainer");
        }

        try {
            // 如果是单例，但已存在实例，则直接返回
            if (isSingleton && singletonInstances.containsKey(classType)) {
                return (T) singletonInstances.get(classType);
            }

            T instance = classType.newInstance();

            Scope scopeAnnotation = classType.getAnnotation(Scope.class);
            if (scopeAnnotation != null && "prototype".equals(scopeAnnotation.value())) {
                scopeDefinitions.put(classType, "prototype");
            } else {
                scopeDefinitions.put(classType, "singleton");
            }

            // 对所有字段进行遍历，查看是否有@Resource注解
            for (Field field : classType.getDeclaredFields()) {
                if (field.isAnnotationPresent(Resource.class)) {
                    field.setAccessible(true);
                    Object fieldInstance = getInstance(field.getType());
                    field.set(instance, fieldInstance);
                }
            }

            return instance;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Cannot create instance of " + classType, e);
        }
    }
}
