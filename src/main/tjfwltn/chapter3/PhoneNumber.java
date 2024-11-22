package main.tjfwltn.chapter3;

import java.util.Objects;

public class PhoneNumber {
    private int first;
    private int second;
    private int third;

    public PhoneNumber(int first, int second, int third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        PhoneNumber that = (PhoneNumber) object;
        return first == that.first && second == that.second && third == that.third;
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, third);
    }
}
