## Item14 : Comparable을 구현할지 고려하라

---

## 서론

---
Comparable 인터페이스를 구현했다는 것은 해당 클래스 인스턴스들에는 자연적인 순서가 있음을 뜻한다.
그래서 Comparable을 구현한 객체들의 배열은 손쉽게 정렬할 수 있다. 따라서 문자열의 사전적인 순서, 숫자의 오름차순 혹은 내림차순, 연대같이 순서가 명확한
값 클래스를 작성하고자 한다면 반드시 Comparable 인터페이스를 구현하자.
___
## 본론

---
Comparable의 인터페이스는 compareTo메서드만 가지고 있는 인터페이스이다.
```java
public interface Comparable<T> {
    public int compareTo(T o);
}
```
compareTo 메서드에는 간단히 3가지 규약이 존재한다. 
1. this < other = -1을 리턴한다. -> 현재 객체(this)가 비교 대상 객체(other)보다 작을 경우, 정렬 시 현재 객체가 앞에 있어야 한다.
2. this == other : 0을 리턴한다. -> 현재 객체(this)가 비교 대상 객체(other)와 같을 경우, 두 객체가 동일한 값이나 상태를 가지고 있어야 한다.
3. this > other : 1을 리턴한다. -> 현재 객체(this)가 비교 대상 객체(other)보다 클 경우, 정렬 시 현재 객체가 뒤쪽에 있어야 한다.

**compareTo 메서드 정의시 주의사항이 존재한다.**
compareTo 메서드로 수행한 동치성 테스트 결과가 equals와 같아야 한다. 해당 부분이 지켜지지 않는 경우에는 반드시 명시해줘야 한다.
왜냐하면 지켜지지 않는 경우에는 컬렉션 인터페이스들인 Collection,Set,Map에서 예상과는 다른 값이 나오기 떄문이다.
TreeSet, TreeMap..등등 정렬 기능을 자체적으로 구성하는 컬렉션들은 compareTo를 기반으로 정렬 및 중복 검사를 진행한다.

•	compareTo는 객체를 정렬하기 위한 비교 메서드이다.
•	equals는 객체의 “같음”을 정의한다.
•	둘이 다른 기준으로 구현되면, 컬렉션에서 동일한 객체를 다르게 인식하거나 중복이 발생할 수 있다.

```java
public class Main {
    public static void main(String[] args) {
        Set<BigDecimal> treeSet = new TreeSet<>();
        Set<BigDecimal> treeSet = new TreeMap<Integer,Integer>();
        Set<BigDecimal> hashSet = new HashSet<>();
        
        BigDecimal decimal = new BigDecimal("1.0");
        BigDecimal decimal1 = new BigDecimal("1.00");

        treeSet.add(decimal);
        treeSet.add(decimal1);

        hashSet.add(decimal);
        hashSet.add(decimal1);        
    }
}
```
해당 코드를 보면 TreeSet은 자체적으로 정렬하는 기능을 가지고있지만 HashSet은 정렬기능이 없다.
그럴 경우 1.0과 1.00 값을 출력할 경우 HashSet과 TreeSet의 결과값을 예상해보자

두 자료구조에서 저장된 값은 서로 다르다. TreeSet은 1.0이라는 하나의 값 만을 저장하고, HashSet은 값 두개를 저장한다.
그 이유는 TreeSet같은 경우 compareTo 메서드를 사용하여 객체들을 정렬하고 중복을 제거하기 때문이다.  따라서 BigDecimal("1.0")과 BigDecimal("1.00")은
값이 같으므로 중복을 허용하지 않으며 하나만 저장된다.

HashSet은 equals를 사용하여 객체의 중복을 제거한. 객체의 정확한 동일성을 기준으로 비교하며 1.0과 1.00은 같은 값이지만 그 표현 방식이 다르기 때문에 중복처리가 되지 않는다.

두번째 예시를 보자
```java

public class Person implements Comparable<Person> {
    private final String name;
    private final int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public int compareTo(Person other) {
        return Integer.compare(this.age, other.age); // 객체의 출력 순서는 나이 순
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person person = (Person) obj;
        return Objects.equals(name, person.name); // 이름으로 비교해서 중복값은 제거
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name + " (" + age + ")";
    }
}
```
```java
public class Main {
    public static void main(String[] args) {
        TreeSet<Person> set = new TreeSet<>();
        set.add(new Person("ssuukko", 30));
        set.add(new Person("ssuukko", 25)); // 동일 이름, 다른 나이
        set.add(new Person("AwesomeMinsik", 40));
        set.add(new Person("tjfwltn", 40));

        System.out.println("TreeSet 결과: " + set);
    }
}
```
TreeSet은 객체의 중복 및 정렬 순서를 compareTo메서드를 통해 정의한다고 했다.
그럼 ssuukko TreeSet에 저장이 됐을까 안됐을까?, 그럼 tjfwltn는 저장이 됐을까 안됐을까?

tjfwltn는 저장이 안된다. ssuukko는 저장이 된다.
그 이유는 앞서 말했든디 TreeSet은 compareTo 메서드로 객체의 중복과 정렬을 결정하기 떄문에 이 점을 주의해서 잘 사용해야 한다.

**compareTo 메서드 작성 요령**
- compareTo 메서드는 서론에서 말한것처럼 객체의 순서를 비교하기 위해사용된다. 그래서 순서가 필요한 객체에 TreeSet과 compareTo 메서드에서
객체의 동등성과 값의 순서가 결정되는 컬렉션을 사용할 떄는 둘다 재정의를 해줘야 한다.

- compareTo 메서드 구현시에는 다이아몬드 연산자 (<,>) 를 사용하지말고 박싱된 기본타입 클래스의 정적 메서드 compare를 이용하자.

```java
import java.util.Comparator;

static Comparator<Object> hashCodeOrder = new Comparator<>() {
    public int compare(Object o1, Object o2) {
        return Integer.compare(o1.hashCode(), o2.hashCode());
    }
};
static Comparator<Object> hashCodeOrder = Comparator.comparingInt(Object::hashCode);

public class Person implements Comparable<Person> {
    private final String name;
    private final int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public int compareTo(Person other) {
        return Integer.compare(this.age, other.age); // 나이로 비교
    }
}
```
---
## 결론

---
순서를 고려해야 하는 갑사 클래스를 작성한다면 꼭 Comparable 인터페이스를 구현하여 해당 인스턴스들을 쉽게 정렬하고 검색하고, 비교 기능을 제공하는
컬렉션과 어우러 지도록 해야한다. compareTo 메서드에서 필드의 값을 비교할 때 `<>`와 `연산자 (<, >, <=, >=, ==)`는 쓰지 말아야 한다.
그 이유는 기본 타입을 직접 비교하기 위해 <> 연산자를 사용하면, **null**을 처리하지 못하는 경우가 발생할 수 있다. -> 오버플로우 , 부동소수점 계산 방식에서 오류가 발생할 수 있다.


**잘못된 방식**
```java
@Override
public int compareTo(Person other) {
    if (this.age < other.age) {
        return -1;
    } else if (this.age > other.age) {
        return 1;
    } else {
        return 0;
    }
}
```

박싱된 기본 타입 클래스 (Integer, Double, BigDecimal 등)는 compareTo() 메서드를 이미 정의해두었고, 이를 통해 비교할 수 있다.
compareTo()는 null 값을 처리할 때 안전하게 비교할 수 있다. 예를 들어, Integer나 BigDecimal에서 제공하는 정적 compare() 메서드를 사용하면 null 값을 안전하게 처리하면서 비교를 수행할 수 있다.

그 대신 박싱된 기본 타입 클래스가 제공하는 정적 compare 메서드나 Comparator 인터페이스가 제공하는 비교자 생성 메서드를 사용하라.

비교값이 같아서 순서를 정할 수 없다면, thenComparing을 이용하자.
```java
public class Apple implements Comparable<Apple> {

    private int weight;  //Integer
    private Color color; //Enum.compare
    private String variety; //String.compare

    @Override
    public int compareTo(Apple o) {
        return Integer.compare(o.weight,weight); //박싱된 클래스에 있는 compare 를 이용하자
    }
    /*Apple 클래스에서 weight 가 핵심이여서 먼저 정렬 -> 만약 무게가 같으면 -> 두번쨰로 중요한 Color 필드를 비교 -> 색깔도 같다면 - > variety 필드 비교*/
    @Override
    public int compareTo(Apple o) {
        Comparator<Apple> comparator = Comparator.comparingInt(Apple::getWeight)
                .thenComparing(Apple::getColor)
                .thenComparing(Apple::getVariety);
        return comparator.compare(this,o);
    }
}
```
클래스에 핵심필드가 여러개라면 핵심 필드부터 우선 비교하자.












