package test;

import java.lang.reflect.Field;
import java.util.Vector;

public class testClassLoader {
	public static void main(String[] args) throws SecurityException, IllegalArgumentException, IllegalAccessException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Class<?> clzz;
		while(loader!=null) {
			clzz = loader.getClass();
			System.out.println(clzz);
			int i = 0;
			while(clzz != ClassLoader.class&&i++<5) {
				clzz = clzz.getSuperclass();
			}
			Field f;
			try {
				f = clzz.getDeclaredField("classes");
				f.setAccessible(true);
				Vector<Class<?>> clzzs = (Vector<Class<?>>) f.get(loader);
				System.out.println(clzzs);
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
			loader = loader.getParent();
		}
	}
}
