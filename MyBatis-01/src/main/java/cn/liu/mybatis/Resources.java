package cn.liu.mybatis;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

//通过类加载器获得resource的辅助类
public class Resources {

    public static Reader getResourceAsReader(String resource) throws IOException {
        return new InputStreamReader(getResourceAsStream(resource));
    }

    //根据传入的路径读取资源转为字节流
    private static InputStream getResourceAsStream(String resource) throws IOException {
        ClassLoader[] classLoaders = getContextClassLoader();
        for (ClassLoader classLoader : classLoaders) {
            InputStream inputStream = classLoader.getResourceAsStream(resource);
            if (inputStream != null) {
                return inputStream;
            }
        }
        throw new IOException("Could not find resource "+resource);
    }

    //获取类加载器
    private static ClassLoader[] getContextClassLoader() {
        return new ClassLoader[]{
                ClassLoader.getSystemClassLoader(),
                Thread.currentThread().getContextClassLoader()};
    }
}
