## Item 10. equals는 일반 규약을 지켜 재정의하라

equals 메서드는 기본적으로 두 객체가 동일한지를 확인하기 위해 사용된다. 기본적으로 메모리 주소를 비교하여 두 객체가 같은 인스턴스인지를 비교하지만,

객체의 인스턴스가 달라도 논리적인 상태를 비교해야 하는 경우에는 
개발자의 equals 메서드 재정의를 통해 논리적인 동등성 비교를 위해 사용할 때가 있다.

근데 재정의를 하지않고 Object의 기본 equals 메서드를 사용하는것이 더 적합한 경우도 많다.

```java
public static void main(String[] args) {
Thread t1 = new Thread();
Thread t2 = new Thread();
System.out.println(t1.equals(t2)); // false (기본 구현으로 메모리 주소 비교)
}
```
스레드(Thread)**는 자바에서 병렬 작업을 실행하는데, 각 스레드는 고유한 작업 실행 흐름을 가지고 있기 때문에, 같은 인스턴스인지가 중요하다.
이는 스레드 객체가 단순히 데이터를 표현하는 것이 아니라 동작 자체를 나타내기 때문이다.

같은 작업을 수행한다고 하더라도, 서로 다른 스레드 객체는 독립적으로 실행되는데,
이때 두 스레드가 동일한지 비교할 때 논리적으로 비교하는 것은 의미가 없다. 메모리에서 같은객체를 참조하는지 비교하는 것이 적절하다.

```java
class calculator {
    public int addition(int a, int b) {
        return a + b; // 동작만 제공
    }
}

public static void main(String[] args) {
    
MathOperator op1 = new calculator();
MathOperator op2 = new calculator();
System.out.println(op1.equals(op2));
}
```
    정적 메서드를 통해 동작만 제공하는 유틸리티 클래스는 어떤 경우에도 동일하게 동작하기 때문에
    논리적인 비교는 필요없다.

상태가 없는 클래스는 비교 자체가 무의미하다. 논리적 동등성 비교는 상태가 있는 경우에만 의미를 가지므로
무상태 클래스에서 equals를 재정의하면, 오히려 코드 복잡성이 증가하고 의미없는 코드만 늘어난다.


### equals를 재정의 해야할 때는 언제일까?

객체 메모리 주소 동일성 비교가 아닌 상태를 가지는 클래스의 논리적인 동등성을 비교하는데 이때, 상위 클래스의 equals 가 논리적 동등성을 비교하기 위한
equals 재정의를 하지않았을 경우이다.

```java
class Person {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
}

public class Main {
    public static void main(String[] args) {
        Person p1 = new Person("Alice", 25);
        Person p2 = new Person("Alice", 25);

        // 동일한 값을 가진 두 객체를 키로 사용
        HashMap<Person, String> map = new HashMap<>();
        map.put(p1, "Developer");

        System.out.println(map.get(p2)); // null 반환 (동등성 비교 실패)
    }
}
```

HashMap은 키를 비교할 때 기본적인 equals 메서드를 사용한다. 논리적인 값을 비교하기 위해 HashMap의 equals 를 재정의하지 않으면
값이 동일해도 서로 다른 객체 메모리 주소를 비교하기 때문에 동등성 비교는 실패한다. 이런경우에는 재정의 하는것이 적절하다


equals 를 재정의 할 경우에는 지켜야할 규약이 있다.

### Java Object 에 적혀있는 equals 동치관계 규약
- 반사성(reflexive): 객체는 자신과 항상 같아야 한다. (x.equals(x)는 항상 true)
- 대칭성(symmetric): 두 객체의 비교 결과는 일관되어야 한다. (x.equals(y)와 y.equals(x)는 항상 동일)
- 추이성(transitive): 논리적으로 동등한 객체의 비교 결과는 일관되어야 한다. (x.equals(y)가 true이고, y.equals(z)가 true라면, x.equals(z)도 true)
- 일관성(consistent): 동일한 입력으로 equals를 호출할 때 결과가 변하지 않아야 한다.
- null 비교: null은 항상 false를 반환해야 한다. (x.equals(null)는 항상 false)


위의 규칙에 어긋나는 경우 equals 가 재정의된 해당 객체를 사용하는 다른 객체가 어떻게 반응할지 예측할 수 없다.

위의 규칙중 대칭성을 위반한 equals 재정의 예시 코드를 보면

```java
import java.util.Objects;

public final class CaseInsensitiveString {
    private final String s;

    public CaseInsensitiveString(String s) {
        this.s = Objects.requireNonNull(s);
    }
    @Override
    public boolean equals(Object o){
        if(o instanceof CaseInsensitiveString)
            return s.equalsIgnoreCase(((CaseInsensitiveString)o).s);
        if (o instanceof  String){
            return s.equalsIgnoreCase((String) 0);
        }
    }
}

public static void main(String[] args) {
    CaseInsensitiveString cis = new CaseInsensitiveString("Polish");
    String s = "polish";
}
```
여기서 cis.equals(s) =true 이다 하지만 s.equals(cis)는 false 를 반환한다.

CaseInsensitiveString 클래스는 String 과 논리적 비교를 하기 위해 equals 를 재정의 했지만
정작 반대로 String s 는 이를 알지 못하기 때문에 대칭성에서 오류가 생긴다.

대칭성에 따르면 CaseInsensitiveString 클래스에서 equals 메서드를 String 타입과 비교하지 않고,
같은 클래스 타입끼리만 비교하도록 재정의해야 한다.


```java
public static void main(String[] args) {
    Point p = new Point(1,2);
    ColorPoint cp = new ColorPoint(1,2,Color.red);
}

@Override
public boolean equals(Object o){
    if(!(o instanceof Point)){
        return false;
    }
    if(!(o instanceof ColorPoint))
        return o.equals(this);
    return super.equals(o)&&((ColorPoint) o).color == color;
}

```

만약 `a.equals(b)가 true`이고 `b.equals(c)가 true`라면, `a.equals(c)는 반드시 true`여야 한다.
하지만 이 코드에서는 `o가 ColorPoint 인스턴스가 아닌 경우`, `o.equals(this)` 를 호출하는 방식으로 비교한다.
 
```java
public static void main(String[] args) {
    Point p1 = new Point(1, 2);
    ColorPoint cp1 = new ColorPoint(1, 2, Color.red);
    ColorPoint cp2 = new ColorPoint(1, 2, Color.red);

    System.out.println(cp1.equals(cp2)); // true
    System.out.println(cp2.equals(p1));  // true
    System.out.println(cp1.equals(p1));  // false
}

```


논리적 동등성을 비교해야 하는 경우에만 equals 메서드를 재정의하며, 상태가 없는 클래스에서는 굳이 재정의하지 않아도 된다.
equals 재정의를 할 때 규약을 따르지 않는다면 논리적 동등성 비교에 혼란을 줄 수있으므로
예기치 않은 결과가 발생할 수 있으므로, 객체 비교 방식에 대한 명확한 규칙을 지키는 것이 중요하다.