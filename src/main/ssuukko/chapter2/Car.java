package main.ssuukko.chapter2;

import java.util.Objects;

public class Car {
    private String Brand;
    private int Price;
    private String color;

    public Car(String brand, int price, String color) {
        Brand = brand;
        Price = price;
        this.color = color;
    }


/*
    @Override
    public String toString() {
        return "Car{" +
                "Brand='" + Brand + '\'' +
                ", Price=" + Price +
                ", color='" + color + '\'' +
                '}';
    }
*/

    // 일부 정보만을 재정의 ()
/*
    @Override
    public String toString() {
        return "Car{" +
                "Brand='" + Brand + '\'' +
                ", Price=" + Price +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        return Price == car.Price && Objects.equals(Brand, car.Brand) && Objects.equals(color, car.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Brand, Price, color);
    }
*/
    
}
