package main.chulhyun96.chapter2;

import java.math.BigDecimal;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        TreeSet<Person> set = new TreeSet<>();
        set.add(new Person("ssuukko", 30));
        set.add(new Person("ssuukko", 25)); // 동일 이름, 다른 나이
        set.add(new Person("AwesomeMinsik", 40));
        set.add(new Person("tjfwltn", 40));

        System.out.println("TreeSet 결과: " + set);

        Set<BigDecimal> treeSet = new TreeSet<>();
        Set<BigDecimal> hashSet = new HashSet<>();

        BigDecimal decimal = new BigDecimal("1.0");
        BigDecimal decimal1 = new BigDecimal("1.00");

        treeSet.add(decimal);
        treeSet.add(decimal1);

        hashSet.add(decimal);
        hashSet.add(decimal1);
        System.out.println("treeSet = " + treeSet);
        System.out.println("hashSet = " + hashSet);
    }
}
