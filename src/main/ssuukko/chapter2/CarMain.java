package main.ssuukko.chapter2;

public class CarMain {

    public static void main(String[] args) {
        Car car1 = new Car("Hyundai", 1000, "red");
        Car car2 = new Car("Hyundai", 1000, "blue");
        System.out.println(car1); // Object의 기본 toString (Car@3796751b)

        // 비교 - color가 다름 - 다만 재정의할 때 color은 따로 재정의 하지 않아서 뭐가 다른지 판단하기 쉽지 않다.
/*
        boolean equals = car1.equals(car2);
        System.out.println(equals);
*/
    }
}
