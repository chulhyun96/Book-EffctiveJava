## Item1. 생성자 대신 정적 팩터리 메서드를 고려하라 

사용자가 프로그램내부 클래스의 인스턴스를 public 생성자를 통해 얻을 수 있게 하는 수단 이외에도
정적 팩터리 메서드를 통해 인스턴스를 제공할 수있다.

정적 팩터리 메서드를 제공하는 방식에는 몇가지 장점과 단점이 있다.

### 1.생성자와 다르게 이름을 가질 수 있다.

BigInteger 클래스는 정수 연산을 위한 Java 표준 라이브러리 중 하나입니다. BigInteger 는 두 가지 생성자가 있다.
```java
BigInteger bigInt1 = new BigInteger("12345");
BigInteger bigInt2 = new BigInteger(1, new byte[]{1, 2, 3});
```
이 두 생성자는 각각 문자열이나 바이트 배열을 이용해 BigInteger 객체를 만든다. 
그러나 생성자만 보면 첫 번째 인수가 양수인지, 음수인지, 또는 바이트 배열이 무엇을 의미하는지 쉽게 알 수 없다.
반면, BigInteger 는 정적 팩터리 메서드인 valueOf()를 제공한다

```java
BigInteger positiveBigInt = BigInteger.valueOf(12345);
BigInteger negativeBigInt = BigInteger.valueOf(-12345);
```
valueOf() 메서드를 사용하면, 이 메서드가 단순히 값을 설정하기 위한 것임을 알 수 있다.
즉, 생성자의 역할을 구체적으로 설명하지 않아도 메서드 이름만으로 객체의 특징을 파악할 수 있게 된다.

이와같이 객체의 인스턴스를 생성자를 통해 얻을 때 생성자 만으로는 해당 인스턴스의 특징이나 상태를 유추하기 어렵다.
하지만 정적 팩터리 메서드를 사용하여 메서드 이름을 통해 생성될 인스턴스의 특징, 상태를 좀 더 자세히 설명할 수있다.

API 를 사용하는 개발자에게 특정 객체를 생성자로 사용하게 한다면 하나의 시그니처로 여러 객체를 사용할 때
이름의 구분 없이 생성자를 오버로딩하여 사용한다면 해당 객체를 사용하는 개발자 입장에서
모든 생성자의 이름이 같기 때문에 혼란이 생길 수 있다.

다음 예시에서도 정적 팩토리가 이름을 가지는 장점을 알 수 있다.

생성자는 똑같은 타입을 파라미터로 받는 생성자 두 개를 만들 수 없다.
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

같은 입력값에 대한 인스턴스를 계속 생성할 필요가 없는 경우
인스턴스를 미리 만들어 놓거나 캐싱하여 재활용하는 식으로 불필요한 객체 생성을 피할 수 있다.
인스턴스 생성비용이 큰 객체일 경우 성능에 장점이있다.

반복되는 요청에 같은 객체를 재활용하는 정적 팩터리 방식의 클래스는 인스턴스의 생명주기를 통제할 수 있다.

인스턴스 생명주기를 통제하면 클래스를 싱글톤 또는 인스턴스화 불가로 만들 수있다.
불변 클래스에서는 인스턴스가 한개임을 보장한다.

### 3.반환 타입의 하위 타입 객체 반환 가능

정적 팩터리 클래스를 통해 인스턴스 반환 타입을 인터페이스 타입으로 지정하여 실제 구현체를 숨길 수 있다
즉, 클라이언트는 인터페이스만 사용하므로 구현체 존재에 대해 알 필요가 없다.


그 예시로 자바 컬렉션 프레임워크의 List 인터페이스를 반환하는 코드에서 
내부적으로는 Collections 의 UnmodifiableList 구현체를 반환하지만, 클라이언트는 이를 알 필요가 없다

```java
List<String> immutableList = Collections.unmodifiableList(Arrays.asList("A", "B", "C"));
```
실제 클라이언트는 반환타입인 List<String> 에 의존하기 때문에 구현체 변경 시 영향을 받지 않는다.


이렇게 반환 타입의 하위타입 객체를 반환하게 되면 유연성이 좋아진다. 
인터페이스 타입을 반환하면 구현체 노출없이 객체를 반환할 수 있게 된다. 따라서 더 작은 API 를 유지할 수 있게 한다

구현체를 숨기고 인터페이스만 노출하는 API 는 개념적 무게를 줄이고 개발자가 알아야 하는 갯수와 난이도가 줄어든다.

### 4.입력 매개변수에 따라 다른 클래스의 객체를 반환할 수 있다.

자바 파일 I/O 작업 API를 제공하는 java.nio.file.Files 클래스의
Files.getFileStore() 메서드는 입력으로 전달된 파일 시스템 객체에 따라 다른 파일 스토어 구현체를 반환한다
```java
public static void main(String[] args) {
    
Path localPath = Paths.get("/tmp/test.txt");
Path networkPath = Paths.get("//network-share/test.txt");

FileStore localFileStore = Files.getFileStore(localPath);
FileStore networkFileStore = Files.getFileStore(networkPath);

System.out.println("Local FileStore Type: " + localFileStore.type());
System.out.println("Network FileStore Type: " + networkFileStore.type());
}
```

        Local FileStore Type: ext4
        Network FileStore Type: nfs
로컬 파일 네트워크 파일 각각의 다른 객체를 입력 매개변수로 전달했을 때 
반환타입은 fileStore 추상클래스로 동일하지만, 

내부적으로 각각 다른 하위 클래스를 반환하므로 사용자 입장에서 반환 타입인 상위 클래스 또는 인터페이스만
알면 사용할 수 있고, 구현체 변경에 영향이 없는 장점과 연결된다.


### 5.정적 팩터리 메서드를 작성하는 시점에는 반환할 객체의 클래스가 존재하지 않아도 된다.


```java
public enum PaymentType {
}

public interface PaymentProcessor {
    void processPayment(double amount);
}

public class PaymentProcessorFactory {
    public static PaymentProcessor getProcessor(PaymentType type) {
        //나중에 구현
        return null;
    }
}
```

결제 인터페이스만 정의한 상태에서 실제 구현은 하지않아도 
정적 팩터리 메서드를 작성하는 시점에서는 반환될 구현체에 의존할 필요가 없다.

```java
import java.util.Objects;

public enum PaymentType {
    CREDIT
}

//나중에 작성된 구현체
public class CreditCardPaymentProcessor implements PaymentProcessor {
    @Override
    public void processPayment(double amount) {
        System.out.println("Processing credit card payment of $" + amount);
    }
}

public class PaymentProcessorFactory {
    public static PaymentProcessor getProcessor(PaymentType type) {
        if (Objects.requireNonNull(type) == PaymentType.CREDIT) {
            return new CreditCardPaymentProcessor();
        }
        return new IllegalArgumentException("Unknown payment type: " + type);
    }
}
```

나중에 이런 구현체 또는 하위클래스가 추가되도 이미 상위 클래스 타입으로 반환하는 
정적팩터리 메서드에서 사용은 아무런 문제가 없다.

## 단점


```java
public class Parent {
    private Parent() { // private 생성자
    }
    
    public static Parent createInstance() {
        return new Parent();
    }
}

// 하위 클래스
public class Child extends Parent {
    // 부모 클래스의 private 생성자 때문에 하위 클래스를 생성할 수 없다.
}
```
1. 정적 팩터리 메서드 만으로는 하위 클래스를 만들 수 없다.

하위 클래스 상속을 하기 위해서는 super 호출을 통해 부모타입의 public 또는 protected 생성자가 필요하므로
부모 클래스의 인스턴스를 정적 팩터리 메서드만 제공하면 하위 클래스를 만들 수 없는 것이 단점이다.


```java
public class Child {
    private final Parent parent;

    public Child() {
        this.parent = Parent.createInstance(); // Parent 인스턴스를 내부적으로 사용
    }
}
```
하지만 상속은 부모,자식 클래스간 강한 결합도 문제가 있으므로 
컴포지션을 사용하도록 유도한다면 단점이아닌 장점으로 활용 할 수있다.

2. 개발자가 찾기 힘들다
생성자는 Javadoc 이 자동으로 상단에 모아서 보여주지만 정적 팩터리메서드는 그렇지 않다.
```java
// API 사용자에게 제공되는 클래스
public class Car {
    private String model;

    private Car(String model) {
        this.model = model;
    }

    // 정적 팩터리 메서드
    public static Car createSedan() {
        return new Car("Sedan");
    }

    public static Car createSUV() {
        return new Car("SUV");
    }
}

```


### 결론

결국 인스턴스 생성에 대한 방식문제로 보았을 때 정적 팩터리 메서드와 public 생성자는 각각의 상대적인 장단점이 있다.
무작정 public 생성자를 사용하던 습관은 정적 팩터리 메서드의 장점이 훨 씬 더 많으므로 버리는 것이 좋다.




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