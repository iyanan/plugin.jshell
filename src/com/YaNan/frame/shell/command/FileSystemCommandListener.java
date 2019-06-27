package com.YaNan.frame.shell.command;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.shell.EchoShellFactory;
import com.YaNan.frame.shell.EchoShellFactory.EchoShell;

@Register(attribute= {"ll","ls"})
public class FileSystemCommandListener implements CommandListener{
	private EchoShell shell;
	@Override
	public void accept(String command) {
		System.out.println("磁盘命令："+command);
	}

	@Override
	public void setShell(EchoShell shell) {
		this.shell = shell;
	}

	@Override
	public boolean acceptParam(char[] buffered, StringBuffer lineBuffer, int dst, int len) {
		return false;
	}

	@Override
	public void enterCommand(String commandLine) {
		String[] commandStrs = commandLine.split(" ");
		String path = shell.getPath();
		if(commandStrs.length>1) {
			if(commandStrs.length>1) {
				path = commandStrs[1];
			}
		}
		File file = new File(path);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int maxNameLen = 0;
		if(file.isDirectory()) {
			for(File f : file.listFiles()) {
				maxNameLen = f.getName().length()>maxNameLen?f.getName().length():maxNameLen;
			}
			if(maxNameLen>32)
				maxNameLen = 32;
			shell.write("totail:"+file.listFiles().length);
			for(File f : file.listFiles()) {
				if(commandStrs[0].equals("ls")) {
					shell.write("\r\n"+f.getName());   //note the '\r'
				}else {
					shell.write("\r\n"+getName(f.getName(),maxNameLen)+"\t"+getPer(f)+"\t"+getLen(f.length())+"\t"+sdf.format(new Date(f.lastModified()))+"\t"+(f.isDirectory()?"dir":"file"));
				}
			}
		}
	}

	private String getName(String name, int maxNameLen) {
		if(name.length()>maxNameLen) {
			return name.substring(0, maxNameLen-3)+"...";
		}
		for(int i = name.length();i<maxNameLen;i++) {
			name+=" ";
		}
		return name;
	}

	private String getPer(File f) {
		return "-"+(f.canExecute()?"e":"")+(f.canRead()?"r":"")+(f.canWrite()?"w":"")+(f.getName().endsWith(".class")?"j":"")+"-";
	}

	private String getLen(long length) {
		int i = 0 ;
		while(length>1024) {
			length = length/1024;
			i++;
		}
		switch(i) {
		case 0:
			return length+"b";
		case 1:
			return length+"kb";
		case 2:
			return length+"mb";
		case 3:
			return length+"gb";
		case 4:
			return length+"tb";
		default :
			return length+"tb*1024*"+(i-4);
		}
	}

	@Override
	public boolean destory() {
		return true;
	}
}
