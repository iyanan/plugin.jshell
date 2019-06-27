package test;

import java.util.concurrent.CountDownLatch;

import org.apache.sshd.server.command.Command;

import com.YaNan.frame.shell.EchoCommandFactory;


public class TestEchoCommandFactory extends EchoCommandFactory {

    @Override
    public Command createCommand(String command) {
        return new TestEchoCommand(command);
    }

    public static class TestEchoCommand extends EchoCommand {
        public static CountDownLatch latch = new CountDownLatch(1);

        public TestEchoCommand(String command) {
            super(command);
            System.out.println("command:"+command);
        }

        @Override
        public void destroy() {
//            if (latch != null) {
//                latch.countDown();
//            }
//            super.destroy();
        }
    }
}