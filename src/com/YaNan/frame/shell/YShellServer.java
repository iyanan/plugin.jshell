package com.YaNan.frame.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import org.apache.sshd.common.channel.ChannelListener;
import org.apache.sshd.common.util.GenericUtils;
import org.apache.sshd.common.util.OsUtils;
import org.apache.sshd.common.util.ValidateUtils;
import org.apache.sshd.common.util.logging.AbstractLoggingBean;
import org.apache.sshd.common.util.security.SecurityUtils;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.AsyncAuthException;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.password.PasswordChangeRequiredException;
import org.apache.sshd.server.auth.pubkey.AcceptAllPublickeyAuthenticator;
import org.apache.sshd.server.command.AbstractDelegatingCommandFactory;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.command.CommandFactory;
import org.apache.sshd.server.config.keys.DefaultAuthorizedKeysAuthenticator;
import org.apache.sshd.server.forward.AcceptAllForwardingFilter;
import org.apache.sshd.server.keyprovider.AbstractGeneratorHostKeyProvider;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.shell.InteractiveProcessShellFactory;
import org.apache.sshd.server.shell.InvertedShell;
import org.apache.sshd.server.shell.InvertedShellWrapper;
import org.apache.sshd.server.shell.ProcessShell;
import org.apache.sshd.server.shell.ProcessShellFactory;
import org.apache.sshd.server.shell.ShellFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YShellServer {
	private static String key_path = "/Users/yanan/Desktop/未命名文件夹/key.ssh";
	private static Logger log = LoggerFactory.getLogger(YShellServer.class);

	public static void main(String[] args) throws IOException {
		 embedding();
		SshServer sshServer = SshServer.setUpDefaultServer();
		sshServer.setPort(4280);
		sshServer.setHost("localhost");
		AbstractGeneratorHostKeyProvider hostKeyProvider = null;
		Path hostKeyFile = null;
		if(SecurityUtils.isBouncyCastleRegistered()) {
			hostKeyFile = new File(key_path).toPath();
			hostKeyProvider = SecurityUtils.createGeneratorHostKeyProvider(hostKeyFile);
		}else {
			hostKeyFile = new File(key_path).toPath();
	        hostKeyProvider = new SimpleGeneratorHostKeyProvider(hostKeyFile);
		}
		sshServer.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
		sshServer.setPasswordAuthenticator(new PasswordAuthenticator() {

			@Override
			public boolean authenticate(String username, String password, ServerSession session)
					throws PasswordChangeRequiredException, AsyncAuthException {
								boolean flag = false;
								if(username.equals(username) && password.equals(password)) {
									flag = true;
								}
								log.info("[YunSShServer.run][response]username:{},pwd:{},flag:{}",username,password,flag);
								return flag;
			}
			});
//		if (OsUtils.isUNIX()) { 
//			sshServer.setShellFactory(new ProcessShellFactory(new String[] { "/bin/sh", "-i", "-l" }) {
//				 public void setCommand(String... command) {
//					 System.out.println("命令字符："+Arrays.toString(command));
//				        setCommand(GenericUtils.isEmpty(command) ? Collections.emptyList() : Arrays.asList(command));
//				    }
//			    protected List<String> resolveEffectiveCommand(List<String> original) {
//			    	System.out.println("命令："+original);
//			        if (!OsUtils.isWin32()) {
//			            return original;
//			        }
//
//			        // Turns out that running a command with no arguments works just fine
//			        if (GenericUtils.size(original) <= 1) {
//			            return original;
//			        }
//
//			        // For windows create a "cmd.exe /C "..."" string
//			        String cmdName = original.get(0);
//			        if (OsUtils.WINDOWS_SHELL_COMMAND_NAME.equalsIgnoreCase(cmdName)) {
//			            return original;    // assume callers knows what they're doing
//			        }
//
//			        return Arrays.asList(OsUtils.WINDOWS_SHELL_COMMAND_NAME, "/C", GenericUtils.join(original, ' '));
//			    }
//			});
//       }
		sshServer.setShellFactory( new EchoShellFactory());
		sshServer.setPublickeyAuthenticator(AcceptAllPublickeyAuthenticator.INSTANCE);
		sshServer.setForwardingFilter(AcceptAllForwardingFilter.INSTANCE);
//		sshServer.setShellFactory(new ProcessShellFactory("java"));
		sshServer.setPublickeyAuthenticator(new DefaultAuthorizedKeysAuthenticator(false));
		sshServer.start();
		log.info("[YunSShServer.run][response]result:{},host:{},port:{}","SSHD服务启动成功",sshServer.getHost(),sshServer.getPort());
	}

    /**
     * 一直有效
     */
    static void embedding(){
        new Thread(new Runnable(){
 
            public void run() {
                while(true){
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
             
        }).start();
    }
}
