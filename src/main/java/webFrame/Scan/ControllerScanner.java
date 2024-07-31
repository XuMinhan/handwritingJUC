package webFrame.Scan;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import webFrame.Scan.annotation.Controller;
import webFrame.Scan.annotation.GetMapping;
import webFrame.Scan.annotation.PostMapping;


import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class ControllerScanner {
    private Map<String, MethodAndHandler> commandMap = new HashMap<>();
    private static final Map<Class<?>, Object> singletonInstances = new ConcurrentHashMap<>();

    private Object getInstance(Class<?> clazz) {
        Object handler = singletonInstances.get(clazz);
        if (handler == null) {
            try {
                Object newHandler = clazz.newInstance();
                singletonInstances.put(clazz, newHandler);
                return newHandler;
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } else {
            return handler;
        }

    }


    public Map<String, MethodAndHandler> doScan(Class<?> controllerRegister) {
        // 将包名转换为路径格式
        String packagePath = controllerRegister.getPackage().getName().replace('.', '/');
        // 获取类加载器用于找到资源路径
        Path basePath = Paths.get(ClassLoader.getSystemResource(packagePath).getPath());

        try (Stream<Path> paths = Files.walk(basePath)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".class") && path.toString().contains(packagePath))
                    .forEach(this::processClassFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return commandMap;
    }

    private void processClassFile(Path classFile) {
        try {
            ClassReader classReader = new ClassReader(Files.newInputStream(classFile));
            classReader.accept(new ClassVisitor(Opcodes.ASM9) {
                @Override
                public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                    String className = name.replace('/', '.');
                    try {
                        Class<?> clazz = Class.forName(className);
                        if (clazz.isAnnotationPresent(Controller.class)) {

                            Object handler = getInstance(clazz);

                            for (Method method : clazz.getDeclaredMethods()) {
                                if (method.isAnnotationPresent(GetMapping.class)) {
                                    GetMapping getMapping = method.getAnnotation(GetMapping.class);
                                    commandMap.put(getMapping.value(), new MethodAndHandler(method, handler));
                                } else if (method.isAnnotationPresent(PostMapping.class)) {
                                    PostMapping postMapping = method.getAnnotation(PostMapping.class);
                                    commandMap.put(postMapping.value(), new MethodAndHandler(method, handler));
                                }
                            }
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
