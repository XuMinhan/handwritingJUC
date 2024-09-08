package webFrame.webTestSwapCalculator.impl;

import java.net.URL;
import java.net.URLClassLoader;

public class CustomClassLoader extends URLClassLoader {
    public CustomClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);  // 使用父类加载器
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if ("webFrame.webTestSwapCalculator.impl.NewCalculator".equals(name)) {
            return findClass(name);  // 优先加载自定义类
        }
        return super.loadClass(name, resolve);  // 委派给父类加载器
    }
}
