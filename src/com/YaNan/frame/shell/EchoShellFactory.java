package com.YaNan.frame.shell;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.sshd.common.Factory;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.command.Command;

import com.YaNan.frame.path.ResourceManager;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.shell.command.CommandListener;

/**
 * @author Apache MINA SSHD Project
 */
public class EchoShellFactory implements Factory<Command> {
	public Command create() {
		return new EchoShell();
	}

	public static class EchoShell implements Command, Runnable {
		private Map<Object,Object> attribute = new HashMap<Object,Object>();
		private ConcurrentHashMap<Object, Object> syncAttribute = new ConcurrentHashMap<Object, Object>();
		private String suffix;
		private InputStream in;
		private OutputStream out;
		private OutputStream err;
		private ExitCallback callback;
		private Environment environment;
		private Thread thread;
		private EchoShell _shell = this;
		private String path = ResourceManager.getPathExress("classpath:");
		public void setAttribute(Object key,Object value) {
			attribute.put(key, value);
		}
		public void getAttribute(Object key) {
			attribute.get(key);
		}
		public void removeAttribute(Object key) {
			attribute.remove(key);
		}
		public void setSecurityAttribute(Object key,Object value) {
			syncAttribute.put(key, value);
		}
		public void getSecurityAttribute(Object key) {
			syncAttribute.get(key);
		}
		public void removeSecurityAttribute(Object key) {
			syncAttribute.remove(key);
		}
		public String getSuffix() {
			return suffix;
		}

		public void setSuffix(String suffix) {
			this.suffix = suffix;
		}
		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		//		public static void main(String[] args) {
//			System.out.println(path);
//			System.out.println(ResourceManager.getResource(path));
//		}
		public InputStream getIn() {
			return in;
		}

		public OutputStream getOut() {
			return out;
		}

		public OutputStream getErr() {
			return err;
		}

		public Environment getEnvironment() {
			return environment;
		}

		public void setInputStream(InputStream in) {
			this.in = in;
		}

		public void setOutputStream(OutputStream out) {
			this.out = out;
		}

		public void setErrorStream(OutputStream err) {
			this.err = err;
		}

		public void setExitCallback(ExitCallback callback) {
			this.callback = callback;
		}

		public void start(Environment env) throws IOException {
			environment = env;
			thread = new Thread(this, "EchoShell");
			thread.start();
		}
		public void writeSuffix() {
			write(suffix);
		}
		public void nextLine() {
			write("\r\n");
		}
		public void writeLine() {
			write("\r\n"+suffix);
		}
		public void moveFocusToPreLine() {
			write("\33[1A");
		}
		public void focusMoveLeft(int n) {
			write("\33["+n+"D");
		}
		public void focusMoveRight(int n) {
			write("\33["+n+"C");
		}
		public void write(String content) {
			try {
				this.out.write(content.getBytes());
				this.out.flush();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		public void write(char... content) {
			try {
				for(char c : content) {
					this.out.write(c);
					this.out.flush();
				}
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		public void write(byte... content) {
			try {
					this.out.write(content);
					this.out.flush();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		public void writeNotFlush(byte... content) {
			try {
				for(byte c : content) {
					this.out.write(c);
				}
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		public void flush() {
			try {
					this.out.flush();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		public String getANSIContent(String content,int bgColor,int fontColor) {
			return "\33["+bgColor+";"+fontColor+";5m"+content+"\33[0m";
		}
		public void saveFocus() {
			write("\33[s");
		}
		public void recoveryFocus() {
			write("\33[u");
		}
		public void hideFocus() {
			write("\33[?25l");
		}
		public void showFocus() {
			write("\33[?25h");
		}
		public void writeWelcome() {
			write("Welcome to YShell Server Plamfat");
			nextLine();
			write("Version:1.0");
			nextLine();
			write("Type \"help\" to get more help info!");
			nextLine();
		}
		public String getBackspaceContent(int len) {
			String bs = "\b \b";
			for(int i = 1;i<len;i++) {
				bs+="\b";
			}
			return bs;
		}
		public String getLineEndContent(String content) {
			
			return "\r\n"+getBackspaceContent(content.length())+content;
		}
		public void writeLineEnd(String content) {
			write(getLineEndContent(content));
		}
		public void cleanLine() {
			write("\33[2K\r");
		}
		public void cleanScreen() {
			write("\33[2J");
		}
		public void writeBackspace(int len) {
			write(getBackspaceContent(len));
		}
		public void clearLineAndWrite(String content) {
			cleanLine();
			writeLineEnd(content);
		}
		public void destroy() {
			thread.interrupt();
		}
		public void run() {
			try {
			suffix = "$ "+environment.getEnv().get("USER")+":";
			writeWelcome();
			writeLine();
//			hideFocus();
//			for(int i = 0;i<101;i++) {
//				cleanLine();
//		    	writeSuffix();
//				writeLineEnd(getANSIContent(i+"%",41,37));
////				moveFocusToPreLine();
//				Thread.sleep(200);
//			}
//			
//			  cleanScreen();
//			writeLine();
//			String li = "[=";
//		     for(int i = 0;i<100;i++) {
//		    	 li +="=";
//		    	 cleanLine();
//		    	 writeSuffix();
//		    	 write(li+"]");
//		    	 writeLineEnd(i+"%");
//				Thread.sleep(500);
//			}
//		     showFocus();
			@SuppressWarnings("resource")
			ShellBuffered shellBuffered = new ShellBuffered(new InputStreamReader(in));
			shellBuffered.setListener(new ReadListenrer() {
				@Override
				public void onRead(char[] buffered,StringBuffer stringBuffer,int dst,int len) {
					try {
						String temp = new String(buffered);
//						System.out.println("得到数据："+(int)buffered[0]+(int)buffered[1]);
						//回退
						if(shellBuffered.getCommand()==null){
							if(temp.indexOf("\b")>-1) {
//								for(int i=0;i<len;i++) {
//									buffered[i]=0;
//								}
								len = len-1;
								if(stringBuffer!=null&&stringBuffer.length()>0) {
									int l = stringBuffer.toString().length();
									stringBuffer.setLength(l-2);
									writeBackspace(1);
								}
								return ;
							
							}else if(temp.indexOf("\t")>-1) {
								CommandListener command = PlugsFactory.getPlugsInstanceByAttributeStrict(CommandListener.class,stringBuffer.toString().trim());
								if(command!=null) {
									shellBuffered.setCommand(command,stringBuffer.toString().trim());
									shellBuffered.setCommandCheck(true);
									command.accept(stringBuffer.toString().trim());
									command.setShell(_shell);
								}
								stringBuffer.setLength(stringBuffer.length()-1);
								return ;
								//光标移动符号
							} else if(temp.indexOf("[D")>-1){
								focusMoveLeft(1);
							} else if(temp.indexOf("[C")>-1){
								focusMoveRight(1);
							}else if(temp.indexOf(" ")>-1) {
								CommandListener command = PlugsFactory.getPlugsInstanceByAttributeStrict(CommandListener.class,stringBuffer.toString().trim());
								if(command!=null) {
									shellBuffered.setCommand(command,stringBuffer.toString().trim());
									shellBuffered.setCommandCheck(true);
									command.accept(stringBuffer.toString().trim());
									command.setShell(_shell);
								}
							}
							write(temp);
						}else if(shellBuffered.getCommand()!=null) {
							if(stringBuffer!=null&&stringBuffer.length()==1&&(buffered[0]=='\b')) {
								shellBuffered.setCommand(null, null);
								stringBuffer.setLength(0);
								shellBuffered.setInvoke(false);
								return;
							}
							if((int)buffered[0]==3) {
								if(shellBuffered.getCommand().destory()) {
									shellBuffered.setCommand(null, null);
									stringBuffer.setLength(0);
									writeLine();
									shellBuffered.setInvoke(false);
									return;
								}
							}
							if(shellBuffered.isCommandCheck()&&!stringBuffer.toString().startsWith(shellBuffered.getCommandStr())) {
								shellBuffered.setCommand(null,null);
								this.onRead(buffered, stringBuffer, dst, len);
							}else  {
								CommandListener command =shellBuffered.getCommand();
								if(!command.acceptParam(buffered,stringBuffer,dst,len)) {
									shellBuffered.setCommand(null,shellBuffered.getCommandStr());
									this.onRead(buffered, stringBuffer, dst, len);
									shellBuffered.setCommand(command,shellBuffered.getCommandStr());
								}
							}
						} else {
							write(temp);
						}
					} catch (RuntimeException e) {
						e.printStackTrace();
					}
				}
			});
				for (;;) {
					String str = shellBuffered.readLine();
					nextLine();
					if (str == null) {
						return;
					}
					if(shellBuffered.getCommand()!=null) {
						if(!shellBuffered.isInvoke()) {
							shellBuffered.setCommandCheck(false);
							shellBuffered.setInvoke(true);
							new Thread(new Runnable() {
								@Override
								public void run() {
									shellBuffered.getCommand().enterCommand(str);
									shellBuffered.setCommand(null,null);
									writeLine();
									shellBuffered.setInvoke(false);
								}
							}).start();
							shellBuffered.setInvoke(true);
						}
					}else{
						//退出
						if ("exit".equals(str.trim())) {
							write("bye!");
							return;
						}
						String[] commandStrs = str.trim().split(" ");
						if(commandStrs[0].trim().equals("")) {
							writeLine();
						}else {
							CommandListener command = PlugsFactory.getPlugsInstanceByAttributeStrict(CommandListener.class,commandStrs[0]);
							if(command==null) {
								write("Unknown command:"+str);
								writeLine();
							}else {
								shellBuffered.setCommand(command,commandStrs[0]);
								shellBuffered.setCommandCheck(false);
								shellBuffered.setInvoke(true);
								command.setShell(_shell);
								Thread exeThread = new Thread(new Runnable() {
									@Override
									public void run() {
										command.enterCommand(str);
										shellBuffered.setCommand(null,null);
										writeLine();
										shellBuffered.setInvoke(false);
									}
								});
								exeThread.start();
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				callback.onExit(0);
			}
		}
		public void writeln(String content) {
			write(content);
			nextLine();
		}
	}
}
