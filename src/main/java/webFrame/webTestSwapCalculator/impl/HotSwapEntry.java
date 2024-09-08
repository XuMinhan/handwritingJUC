package webFrame.webTestSwapCalculator.impl;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

public class HotSwapEntry {
    public static CalculatorInterface compilerAndSwapImpl() throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        // 指定新Java文件路径
        String filePath = "src/main/java/webFrame/webTestSwapCalculator/impl/NewCalculator.java";  // 替换为你的实际路径

        // 获取 Java 编译器
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        String outputDir = "/Users/xuminhan/IdeaProjects/handwritingJUC/target/classes";  // 替换为你的实际输出目录路径
// 设置编译选项，指定输出目录
        Iterable<String> options = Arrays.asList("-d", outputDir);
        // 编译 Java 文件
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromStrings(Arrays.asList(filePath));
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null, compilationUnits);

        if (task.call()) {
            // 编译成功，动态加载新类
            fileManager.close();

            // 使用URLClassLoader加载编译后的类
            File classesDir = new File("/Users/xuminhan/IdeaProjects/handwritingJUC/target/classes");  // 替换为你的实际路径
//            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{classesDir.toURI().toURL()});
            CustomClassLoader classLoader = new CustomClassLoader(new URL[]{classesDir.toURI().toURL()}, HotSwapEntry.class.getClassLoader());

            System.out.println(classLoader);
            Class<?> newCalculatorClass = classLoader.loadClass("webFrame.webTestSwapCalculator.impl.NewCalculator");
            Object o = newCalculatorClass.getDeclaredConstructor().newInstance();
            System.out.println(o);
            System.out.println("Loaded by: " + newCalculatorClass.getClassLoader());

            return (CalculatorInterface) o;

        } else {
            // 输出编译错误信息
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                System.out.println(diagnostic.getMessage(null));
            }
        }
        return null;
    }
}
