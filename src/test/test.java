package test;

import java.util.Arrays;

import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.shell.command.CommandListener;

public class test {
	public static void main(String[] args) {
		CommandListener commandListener = PlugsFactory.getPlugsInstanceByAttribute(CommandListener.class,"ll");
		System.out.println(commandListener);
		commandListener.accept("ll");
		char[] c = new char[100];
		StringBuffer sb = new StringBuffer();
		for(int i = 0;i<5;i++) {
			c[i] = 'a';
		}
		for(int i = 5;i<10;i++) {
			c[i] = 'b';
		}
		sb.append(new String(c));
		System.out.println(sb.toString());
		sb.trimToSize();
		System.out.println(sb.length());
		System.out.println(Arrays.toString(sb.toString().toCharArray()));
	}
}
