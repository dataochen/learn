package Aio;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author dataochen
 * @Description
 * @date: 2019/6/3 16:39
 */
public class Main {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
//        write();
        read();
    }

    private static void write() throws IOException, ExecutionException, InterruptedException {
        File file = new File("cdt.txt");
        file.createNewFile();
        Path path1 = file.toPath();
        AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(path1, StandardOpenOption.WRITE);
        ByteBuffer allocate = ByteBuffer.allocate(1024);
        allocate.put("12".getBytes());
        allocate.flip();

        Future<Integer> write = asynchronousFileChannel.write(allocate, 0);
        System.out.println(2);
        Integer integer = write.get();
        System.out.println("end="+integer);
    }

    private static void read() throws IOException, ExecutionException, InterruptedException {
        File file = new File("cdt.txt");
        file.createNewFile();
        Path path1 = file.toPath();
        AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(path1, StandardOpenOption.READ);
        ByteBuffer allocate = ByteBuffer.allocate(1024);
        asynchronousFileChannel.read(allocate, 0, allocate, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                System.out.println("completed");
                System.out.println(allocate.toString());

            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                System.out.println("failed");

            }
        });
        System.out.println(2);
        allocate.flip();
        System.out.println(allocate.toString());

    }
}
