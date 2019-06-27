package com.YaNan.frame.shell.command;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.utils.reflect.ClassLoader;
import com.YaNan.frame.utils.resource.ResourceManager;
import com.YaNan.frame.shell.EchoShellFactory;
import com.YaNan.frame.shell.EchoShellFactory.EchoShell;
import com.YaNan.frame.shell.ansi.AnsiWrapper;

@Register(attribute="*",priority=Integer.MAX_VALUE)
public class ShellInvokeCommandListener implements CommandListener{
	private EchoShell shell;
	private PrintStream ps;
	private Thread currentThread;
	@Override
	public void accept(String command) {
		
	}

	@Override
	public void setShell(EchoShell shell) {
		this.shell = shell;
	}

	@Override
	public boolean acceptParam(char[] buffered, StringBuffer lineBuffer, int dst, int len) {
		if(new String(buffered).indexOf("\t")>-1) {
			String[] commandStrs = lineBuffer.toString().trim().split(" ");
			String path = commandStrs.length>0?commandStrs[0]:shell.getPath()+"/";
			if(!path.startsWith("/")) {
				path = new File(shell.getPath(),path).getAbsolutePath();
			}
			List<File> dirs = ResourceManager.getResource(path+"*");
			Iterator<File> iterator = dirs.iterator();
			while(iterator.hasNext()) {
				File file = iterator.next();
				if(file.isDirectory()||!file.getName().endsWith(".class")) {
					iterator.remove();
				}
			}
			shell.nextLine();
			lineBuffer.setLength(lineBuffer.length()-1);
			if(dirs.size()==1) {
				lineBuffer.append(dirs.get(0).getName().substring(commandStrs[0].length(),dirs.get(0).getName().length()-6));
			}else {
				int i = 0;
				for(File file : dirs) {
					shell.write(file.getName().substring(0,file.getName().length()-6));
					if(i++%2!=0)
						shell.nextLine();
					else
						shell.write("\t");
				}
			}
			shell.writeLine();
			shell.write(lineBuffer.toString());
			return true;
		}
		return false;
	}

	@Override
	public void enterCommand(String commandLine) {
		currentThread = Thread.currentThread();
		File file = new File(shell.getPath());
		String[] commandStrs = commandLine.split(" ");
		if(commandStrs.length>0) {
			String path = ResourceManager.getPathExress(commandStrs[0]+".class");
			if(!path.startsWith("/")) {
				file = new File(file.getPath(),path);
			}else {
				file = new File(path);
			}
		}
		if(file.exists()) {
			if(file.isDirectory()) {
				shell.write("path "+file.getName()+" is a directory!");
			}else if(!file.getName().endsWith(".class")){
				shell.write("class file "+file.getName()+" is not a java file!");   //note the '\r'
			}else {
				String classPath = ResourceManager.getPathExress("classpath:");
				String className = file.getAbsolutePath().replace(classPath, "");
						className = className.substring(0,className.length()-6).replace("/", ".");
				ps = System.out;
				try {
					ClassLoader loader = new ClassLoader(className);
					String[] args = new String[commandStrs.length-1];
					System.arraycopy(commandStrs, 1, args, 0, commandStrs.length-1);
					Method method = loader.getMethod("main", String[].class);
					if(commandLine.indexOf("-o")==-1) {
						System.setOut(new PrintStream(shell.getOut()));
					}
					method.invoke(null,(Object) args);
					if(commandLine.indexOf("-o")==-1) {
						System.setOut(ps);
					}
					shell.nextLine();
					shell.write("invoke "+file.getName()+" success!"); 
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
					shell.writeln(AnsiWrapper.newWrapper()
							.wrapperColor(AnsiWrapper.COLOR.FONT_BLACK)
							.wrapperColor(AnsiWrapper.COLOR.BACKGROUND_RED)
							.wrapperFlash()
							.wrapperContent(e.getClass().getName()+": "+e.getMessage()).toString());
					for(StackTraceElement stack:e.getStackTrace()) {
						shell.writeln("\t at "+stack.getClassName()+"."+stack.getMethodName()+"("+stack.getFileName()+":"+stack.getLineNumber()+")");
					}
					Throwable t = e;
					while((t = t.getCause())!=null) {
						shell.writeln("Cause by: "+t.getClass().getName()+": "+t.getMessage());
						int i = 0;
						for(StackTraceElement stack : t.getStackTrace()) {
//							if(i++>1) {
//								shell.writeln("\t ... "+(t.getStackTrace().length-2)+" more");
//								break;
//							}
							shell.writeln("\t at "+stack.getClassName()+"."+stack.getMethodName()+"("+stack.getFileName()+":"+stack.getLineNumber()+")");
						}
					}
					shell.write("\33[0m");
					shell.nextLine();
					shell.write("invoke "+file.getName()+" failed!"); 
				}
				
			}
		}else {
			shell.write("path "+file.getName()+" at "+shell.getPath() +" is not exists!");   //note the '\r'
		}
	}

	@Override
	public boolean destory() {
		System.setOut(ps);
		try {
			return true;
		}finally{
			currentThread.interrupt();
			currentThread.stop();
		}
		
	}
	
}
