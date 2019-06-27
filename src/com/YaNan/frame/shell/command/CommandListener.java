package com.YaNan.frame.shell.command;

import com.YaNan.frame.plugin.annotations.Service;
import com.YaNan.frame.shell.EchoShellFactory.EchoShell;

@Service
public interface CommandListener {
	public void accept(String command);
	public void setShell(EchoShell shell);
	public boolean acceptParam(char[] buffered, StringBuffer lineBuffer, int dst, int len);
	public void enterCommand(String commandLine);
	public boolean destory() ;
}
