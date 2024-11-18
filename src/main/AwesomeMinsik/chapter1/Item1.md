## Item1. 생성자 대신 정적 팩터리 메서드를 고려하라 

사용자가 프로그램내부 클래스의 인스턴스를 public 생성자를 통해 얻을 수 있게 하는 수단 이외에도
정적 팩터리 메서드를 통해 인스턴스를 제공할 수있다.

정적 팩터리 메서드를 제공하는 방식에는 몇가지 장점과 단점이 있다.

### 1.생성자와 다르게 이름을 가질 수 있다.


객체의 인스턴스를 생성자를 통해 얻을 때 생성자 만으로는 해당 인스턴스의 특징이나 상태를 유추하기 어렵다.
하지만 정적 팩터리 메서드를 사용하여 메서드 이름을 통해 생성될 인스턴스의 특징, 상태를 좀 더 자세히 설명할 수있다.

API 를 사용하는 개발자에게 특정 객체를 생성자로 사용하게 한다면 하나의 시그니처로 여러 객체를 사용할 때
이름의 구분 없이 매개변수의 순서만 다르게 생성자를 오버로딩하여 사용한다면 해당 객체를 사용하는 개발자 입장에서
모든 생성자의 이름이 같기 때문에 혼란이 생길 수 있다. 

하지만 정적 팩터리 메서드의 이름을 각 생성자의 특징을 구분할 수있도록 정의 하면
개발자 입장에서 사용하는데 전혀 문제가 생기지 않는다.



ㅎ생성자는 똑같은 타입을 파라미터로 받는 생성자 두 개를 만들 수 없다.

```java
public class Person {
    String name;
    String address;

    public Person(String name) {
        this.name = name;
    }

//    public Person(String address) {
//        this.address = address;
//    }
}
```
위 생성자는 불가능하다.
하지만 정적 팩토리 메서드는 가능하다.
```java
public class Person {
    String name;
    String address;

    private Person() {
    }

    private Person(String name) {
        this.name = name;
    }

    public static Person withName(String name) {
        return new Person(name);
    }

    public static Person withAddress(String address) {
        Person person = new Person();
        person.address = address;
        return person;
    }
    //...
}
```
생성자가 이름을 통해 인스턴스의 특징을 설명하는것이 이런 장점이 있다.


### 2.호출될 때 마다 인스턴스를 새로 생성하지는 않아도 된다.
    
같은 입력값에 대해 계속 인스턴스를 새로 생성할 필요가 없는 경우에 
인스턴스를 미리 만들어 놓거나 캐싱하여 재활용하는 식으로 불필요한 객체 생성을 피할 수 있다.
인스턴스 생성비용이 큰 객체일 경우 성능에 장점이있다.

반복되는 요청에 같은 객체를 재활용하는 정적 팩터리 방식의 클래스는 인스턴스의 생명주기를 통제할 수 있다.

인스턴스 생명주기를 통제하면 클래스를 싱글톤 또는 인스턴스화 불가로 만들 수있다.
불변 클래스에서는 인스턴스가 한개임을 보장한다.

### 3.반환 타입의 하위 타입 객체 반환 가능

```java
public interface Car {

  //...
}
public class ElectricCar implements Car {
    int position = 0;
    
    public static Car create(int position) {
        return new ElectricCar(position);
    }
    //...
}
```

정적 팩터리 클래스를 통해 인스턴스 반환 타입을 인터페이스 타입으로 지정하여 실제 구현체를 숨길 수 있다
즉, 클라이언트는 인터페이스만 사용하므로 구현체 존재에 대해 알 필요가 없다.

반환 타입의 하위타입 객체를 반환하게 되면 유연성이 좋아진다. 
인터페이스 타입을 반환하면 구현체 노출없이 객체를 반환할 수 있게 된다. 따라서 더 작은 API 를 유지할 수 있게 한다

인터페이스만 노출하는 API 는 개념적 무게를 줄이고 개발자가 알아야 하는 갯수와 난이도가 줄어든다.

### 4.입력 매개변수에 따라 다른 클래스의 객체를 반환할 수 있다.

3번째 장점의 연장선으로 반환타입의 하위타입 이면 어떤 클래스의 객체던 반환가능하다.


### 5.정적 팩터리 메서드를 작성하는 시점에는 반환할 객체의 클래스가 존재하지 않아도 된다.

인터페이스, 클래스가 만들어지는 시점에 상속, 구현하는 하위클래스가 없더라도 어차피 인터페이스, 부모클래스의 타입으로
반환할 수 있기 때문에 언제든 의존성 주입을 통해 사용이 가능하다. 따라서 정적팩터리 메서드에서도 변경없이 사용이 가능하다.

```java
public class racingCar extends Car {
    //...
}
```
누군가 나중에 이런 하위클래스를 만든다 하더라도 이미 상위 클래스 타입으로 반환하는 
정적팩터리 메서드에서 사용은 아무런 문제가 없다.

## 단점

1. 정적 팩터리 메서드 만으로는 하위 클래스를 만들 수 없다.
클래스 상속을 하기 위해서는 super 호출을 통해 부모타입의 public 또는 protected 생성자가 필요하므로
정적 팩터리 메서드만 제공하면 하위 클래스를 만들 수 없는 것이 단점이다.
하지만 상속은 부모,자식 클래스간 강한 결합도 문제가 있으므로 
컴포지션을 사용하도록 유도한다면 단점이아닌 장점으로 활용 할 수있다.

2. 개발자가 찾기 힘들다
생성자는 Javadoc 이 자동으로 상단에 모아서 보여주지만 정적 팩터리메서드는 그렇지 않다.



### 컨벤션
- from : 하나의 매개 변수를 받아서 객체를 생성
- ex. Date date = Date.from(instant);
- of : 여러개의 매개 변수를 받아서 객체를 생성
- valueOf: from과 of의 더 자세한 버전
- BigInteger prime = BigInteger.valueOf(Integer.MAX_VALUE);
- getInstance | instance : 인스턴스를 생성. 이전에 반환했던 것과 같을 수 있음.
- newInstance | create : 새로운 인스턴스를 생성
- get[OtherType] : 다른 타입의 인스턴스를 생성. 이전에 반환했던 것과 같을 수 있음.
- new[OtherType] : 다른 타입의 새로운 인스턴스를 생성.