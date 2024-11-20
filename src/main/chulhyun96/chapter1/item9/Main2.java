package main.chulhyun96.chapter1.item9;

public class Main2 {
    public static void main(String[] args) {
        try(MyResource  myResource1 = new MyResource();
            MyResource myResource2 = new MyResource()){
            myResource1.run();
            myResource2.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
