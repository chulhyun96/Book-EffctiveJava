package main.chulhyun96.chapter1.item9;

public class Main {
    public static void main(String[] args) throws Exception {
        MyResource myResource = null;
        try {
            myResource = new MyResource();
            myResource.run(); // -> 해당 부분 오류 발생
            MyResource resource = null;
            try {
                resource = new MyResource();
                resource.run(); // -> 해당 부분 오류 발생
            } finally {
                if (resource != null) {
                    resource.close(); // -> 자원을 닫아줌 (닫는 과정에 오류가 발생함)
                }
            }
        } finally {
            if (myResource != null) {
                myResource.close(); // -> 자원을 닫아줌 (닫는 과정에 오류가 발생함(
            }

        }
    }
}
