package main.chulhyun96.chapter1.item9;

public class MyResource implements AutoCloseable {
    public void run() {
        System.out.println("Hello World!");
        throw new RuntimeException("실행 도중 오류 발생");
    }
    @Override
    public void close() throws Exception {
        System.out.println("AutoClosing");
        throw new RuntimeException("AutoClosing 중 오류 발생");
    }
}
