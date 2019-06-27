package com.YaNan.frame.shell.command;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.shell.EchoShellFactory.EchoShell;
import com.YaNan.frame.shell.ansi.AnsiWrapper;

@Register(attribute = "sh")
public class ShellCommandListener implements CommandListener {
	private EchoShell shell;
	private volatile boolean alive;
	private Process process;
	protected volatile boolean readed;
	private Thread currentThread;

	@Override
	public void accept(String command) {

	}

	@Override
	public void setShell(EchoShell shell) {
		this.shell = shell;
		alive = true;
	}

	@Override
	public boolean acceptParam(char[] buffered, StringBuffer lineBuffer, int dst, int len) {
		PrintStream ps = new PrintStream(process.getOutputStream());
		if (buffered[0] == '\r' || buffered[0] == '\n') {
			if (lineBuffer != null && "exit".equals(lineBuffer.toString().trim())) {
				shell.nextLine();
				shell.write("exit shell model!");
				alive = false;
				return true;
			}
			readed = true;
			shell.nextLine();
			ps.println();
			ps.flush();
		} else {
			ps.print(buffered);
			ps.flush();
		}
		shell.write(new String(buffered));
		return true;
	}

	@Override
	public void enterCommand(String commandLine) {
		currentThread = Thread.currentThread();
		String[] commandStrs = commandLine.split(" ");
		if (commandStrs.length == 1)
			shell.write(AnsiWrapper.newWrapper().wrapperColor(32).wrapperColor(41).wrapperFlash()
					.wrapperContent("enter sh model").wrapperEnd().toString());
		shell.nextLine();
		shell.write("sh:");
		try {
			File wd = new File(shell.getPath());
			Runtime runtime = Runtime.getRuntime();
			process = runtime.exec("/bin/bash", null,wd);
			new Thread(new Runnable() {
				@Override
				public void run() {
					InputStream is = process.getInputStream();
					while (alive) {
						readed = false;
						byte[] temp = new byte[1024];
						int len;
						try {
							while (is.available() > 0 & alive) {
								readed = true;
								len = is.read(temp);
								for (int i = 0; i < len; i++) {
									if (temp[i] == '\n' || temp[i] == '\r') {
										shell.nextLine();
									} else {
										shell.writeNotFlush(temp[i]);
									}
								}
								shell.flush();
								System.out.println(new String(temp));
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
						if (readed) {
							shell.write("sh:");
						}

					}
				}
			}).start();
			new Thread(new Runnable() {
				@Override
				public void run() {
					InputStream is = process.getErrorStream();
					while (alive) {
						boolean ereaded = false;
						byte[] temp = new byte[1024];
						int len;
						try {
							while (is.available() > 0 & alive) {
								ereaded = true;
								len = is.read(temp);
								for (int i = 0; i < len; i++) {
									if (temp[i] == '\n' || temp[i] == '\r') {
										shell.nextLine();
									} else {
										shell.write(temp[i]);
									}
								}
								System.out.println(new String(temp));
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
						if (ereaded) {
							shell.write("sh:");
						}
					}
				}
			}).start();
		} catch (IOException e1) {
			e1.printStackTrace();
			shell.write(e1.getMessage());
		}
		while (alive) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				shell.write(e.getMessage());
			}
		}
	}

	@Override
	public boolean destory() {
		try {
			System.out.println("进程停止");
			process.destroy();
			alive = false;
			return true;
		}finally{
			currentThread.interrupt();
			currentThread.stop();
		}
		
	}

}
