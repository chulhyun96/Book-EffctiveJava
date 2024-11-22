package main.tjfwltn.chapter3;

import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        Map<PhoneNumber, String> map = new HashMap<>();

        for (int i = 0; i < 50000; i++) {
            map.put(new PhoneNumber(i, 2, 3), String.valueOf(i));
        }

        long startTime = System.nanoTime();
        String s = map.get(new PhoneNumber(5000, 2, 3));
        long endTime = System.nanoTime();
        System.out.println("s = " + s);
        System.out.println("Time taken: " + (endTime - startTime) + "ms");
    }
}
