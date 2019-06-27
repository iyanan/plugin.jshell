package com.YaNan.frame.shell.command;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


import com.YaNan.frame.path.ResourceManager;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.shell.EchoShellFactory;
import com.YaNan.frame.shell.EchoShellFactory.EchoShell;

@Register(attribute= {"cd"})
public class ShellPathCommandListener implements CommandListener{
	private EchoShell shell;
	@Override
	public void accept(String command) {
		System.out.println("路径命令："+command);
	}

	@Override
	public void setShell(EchoShell shell) {
		this.shell = shell;
	}

	@Override
	public boolean acceptParam(char[] buffered, StringBuffer stringBuffer, int dst, int len) {
		if(new String(buffered).indexOf("\t")>-1) {
			String[] commandStrs = stringBuffer.toString().trim().split(" ");
			String path = commandStrs.length>1?commandStrs[commandStrs.length-1]:shell.getPath();
			if(!path.startsWith("/")) {
				path = new File(shell.getPath(),path).getAbsolutePath();
			}
			List<File> dirs = ResourceManager.getResource(path+"*");
			Iterator<File> iterator = dirs.iterator();
			while(iterator.hasNext()) {
				if(!iterator.next().isDirectory()) {
					iterator.remove();
				}
			}
			shell.nextLine();
			stringBuffer.setLength(stringBuffer.length()-1);
			if(dirs.size()==1) {
				stringBuffer.append(dirs.get(0).getName().substring(commandStrs.length==1?0:commandStrs[commandStrs.length-1].length()));
			}else {
				int i = 0;
				for(File file : dirs) {
					if(file.isDirectory()) {
						shell.write(file.getName());
						if(i++%2!=0)
							shell.nextLine();
						else
							shell.write("\t");
					}
				}
			}
			
			
			
//
//			File[] files = new File(path).listFiles();
//			List<File> dirs = new ArrayList<File>();
//			for(File file : files) {
//				if(file.isDirectory()) {
//					dirs.add(file);
//				}
//			}
//			shell.nextLine();
//			stringBuffer.setLength(stringBuffer.length()-1);
//			System.out.println(Arrays.toString(stringBuffer.toString().toCharArray()));
//			if(dirs.size()==1) {
//				stringBuffer.append(dirs.get(0).getName().substring(commandStrs.length==1?0:commandStrs[commandStrs.length-1].length()));
//			}else {
//				int i = 0;
//				for(File file : dirs) {
//					shell.write(file.getName());
//					if(i++%2!=0)
//						shell.nextLine();
//					else
//						shell.write("\t");
//				}
//			}
			shell.writeLine();
			shell.write(stringBuffer.toString());
			return true;
		}
		return false;
	}

	@Override
	public void enterCommand(String line) {
		File file = new File(shell.getPath());
		String[] commandStrs = line.split(" ");
		if(commandStrs.length>1) {
			String path = ResourceManager.getPathExress(commandStrs[1]);
			if(!path.startsWith("/")) {
				file = new File(file.getPath(),path);
			}else {
				file = new File(path);
			}
		}
		if(file.exists()) {
			if(file.isDirectory()) {
				shell.setPath(file.getAbsolutePath());
				shell.write("change shell path to "+file.getName());
			}else {
				shell.write("path "+file.getName()+" is a file!");   //note the '\r'
			}
		}else {
			shell.write("path "+file.getName()+" at "+shell.getPath() +" is not exists!");   //note the '\r'
		}
	}

	@Override
	public boolean destory() {
		return true;
	}

}
