package test;

import java.io.IOException;

public class testRuntime {
	public static void main(String[] args) throws IOException, InterruptedException {
		Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec("top");
//		process.destroy();
		byte[] b = new byte[1024];
		int i ;
//		while((i=process.getErrorStream().read(b))!=-1)
//			System.out.print(new String(b));
		while((i=process.getInputStream().read(b))!=-1) {
			System.out.print(new String(b));
			Thread.sleep(1000);
		}
			
	}
}
