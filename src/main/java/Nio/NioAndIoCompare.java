package Nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 比较结果：
 * io读和nio读(循环10次，文件倒叙367M 总大小3670M): 7181ms:4506ms nio性能高0.59倍 底层native方法不同
 * io写和nio写(234kb,循环100次)：1379ms:1124ms 性能差不多
 * io copy和nio copy（367M 循环10次）：23912ms:7865ms nio性能高 nio取决于ByteBuffer大小 太大太小都不好
 * io copy2和nio copy（367M 循环10次）：23912ms:8535ms nio性能高2.80倍 因为nio利用了缓冲区直接copy地址copy，io却是一个个字节copy
 *
 * 如果文件大小不大 io和nio差别不大,nio在文件不大的情况下，selector才是其优势
 * io读和nio读（8kb 循环469760次 总大小3670M）：21104ms:18743ms nio性能高0.12倍
 * io copy和nio copy（8kb 循环10000次）:22569ms:23034ms
 * io copy2和nio copy（8kb 循环10000次）:25433ms:22567ms
 * @author dataochen
 * @Description
 * @date: 2019/5/20 20:59
 */
public class NioAndIoCompare {
    public static void main(String[] args) throws Exception {

        File file = new File("D:\\study\\test\\test.txt");
        File file2 = new File("D:\\study\\test\\test2.txt");
        StringBuilder writeContent = new StringBuilder("test");
//        for (int i = 0; i < 10000; i++) {
//            writeContent.append("testtesttesttesttesttest");
//        }
//        System.out.println(writeContent.toString().length());
        long l1 = System.currentTimeMillis();
        for (int i = 0; i < 469760; i++) {
//            write4Io(writeContent.toString(), file);
            read4Io(file);
//            readAWrite4Io(file, file2);

        }
        long l2 = System.currentTimeMillis();
        System.out.println("==== io cost" + (l2 - l1));
        long l3 = System.currentTimeMillis();
        for (int i = 0; i < 469760; i++) {
//            write(writeContent.toString(), file);
            read(file);
//            readAWrite(file, file2);
//            readAWrite2(file, file2);

        }
        long l4 = System.currentTimeMillis();
        System.out.println("==== nio cost" + (l4 - l3));


    }

    //21724 20711
    public static void write(String content, File file) throws IOException {
        ByteBuffer allocate = ByteBuffer.allocate(content.length());
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        FileChannel channel = fileOutputStream.getChannel();
        allocate.put(content.getBytes());
        allocate.flip();
        while (allocate.hasRemaining()) {
            channel.write(allocate);
        }
        allocate.clear();
        channel.close();
        fileOutputStream.close();
    }

    //    435 5566
    public static void read(File file) throws IOException {
        ByteBuffer allocate = ByteBuffer.allocate((int) file.length());
        FileInputStream fileInputStream = new FileInputStream(file);
        FileChannel channel = fileInputStream.getChannel();
        int read = channel.read(allocate);
//        System.out.println(read);
        allocate.flip();
//        while (allocate.hasRemaining()) {
//            System.out.println((char) allocate.get());
//        }
        allocate.clear();
        channel.close();
        fileInputStream.close();
    }

    //    18548 append:83278
    public static void readAWrite(File file, File file2) throws IOException {
        ByteBuffer allocate = ByteBuffer.allocate(1024 * 1024);
        FileInputStream fileInputStream = new FileInputStream(file);
        FileOutputStream fileOutputStream = new FileOutputStream(file2);
        FileChannel channel = fileInputStream.getChannel();
        FileChannel channel2 = fileOutputStream.getChannel();
        while (true) {
            allocate.clear();
            int read = channel.read(allocate);
            if (read == -1) {
                break;
            }
            allocate.flip();
            while (allocate.hasRemaining()) {
                channel2.write(allocate);
            }
        }
        channel.close();
        channel2.close();
        fileInputStream.close();
        fileOutputStream.close();
    }

    //    19839 367M:1838 3670M:10529
    public static void readAWrite2(File file, File file2) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        FileOutputStream fileOutputStream = new FileOutputStream(file2);
        FileChannel channel = fileInputStream.getChannel();
        FileChannel channel2 = fileOutputStream.getChannel();
        channel.transferTo(0, channel.size(), channel2);
        fileInputStream.close();
        fileOutputStream.close();
    }

    //363 7283 7146
    public static void read4Io(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        int len;
        byte[] bytes = new byte[1024];
        while ((len = fileInputStream.read(bytes)) != -1) {
        }
        fileInputStream.close();

    }

    //    22118
    public static void write4Io(String content, File file) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(content.getBytes());
        fileOutputStream.close();
    }

    //16026 367M:2960 3670M:25983
    public static void readAWrite4Io(File file, File file2) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        FileOutputStream fileOutputStream = new FileOutputStream(file2);
        int len;
        byte[] bytes = new byte[1024];
        while ((len = fileInputStream.read(bytes)) != -1) {
            fileOutputStream.write(bytes, 0, len);
        }
        fileInputStream.close();
        fileOutputStream.close();

    }

}
