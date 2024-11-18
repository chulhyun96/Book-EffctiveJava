## private 생성자나 열거 타입(enum)으로 싱글톤임을 보증하라
싱글톤이란 인스턴스를 오직 하나만 생성할 수 있는 클래스이다.
보통 싱글톤 패턴이 필요한 경우는 그 객체가 리소스를 많이 차지하는 무거운 클래스일 때 적합하다.

#### 싱글톤 패턴의 단점
- 테스트하기가 어려워진다.
- 의존관계상 클라이언트가 구체 클래스에 의존한다. -> DIP 위반.
- 클라이언트가 구체 클래스를 의존하여 OCP 원칙을 위반할 가능성이 높다.
- 내부 속성을 변경하거나 초기화하기 어렵다.
- 결론적으로 유연성이 떨어진다.

#### 싱글톤 패턴이 사용되는 예시
1. 데이터베이스 연결 모듈 등 무거운 일회성 작업
2. 스프링부트의 싱글톤 컨테이너

### 싱글톤을 만드는 방식
#### 1. public static final 필드 방식의 싱글톤
```java
class Singleton {
    // 싱글톤 클래스 객체를 담을 인스턴스 변수
    public static final Singleton INSTANCE = new Singleton();

    // 생성자를 private로 선언 (외부에서 new 사용 X)
    private Singleton() {}
    
}
```
**장점**
- 해당 클래스가 싱글턴인게 API에서 명백히 드러난다는 것,
`public static` 필드가 `final`이기 때문에 절대로 다른 객체를 참조할 수 없다.
- 간결하다.

#### 2. 정적 팩토리 방식의 싱글톤
```java
class Singleton {
    private static final Singleton INSTANCE = new Singleton();

    private Singleton() {}

    public static Singleton getInstance() {
        return INSTANCE;
    }
}
```
**장점**
- API를 바꾸지 않고도 싱글턴이 아니게 변경할 수 있다. 유일한 인스턴스를 반환하던 팩토리 메서드가
  호출하는 스레드별로 다른 인스턴스를 넘겨주게 할 수 있다.
- 원한다면 정적 팩토리를 제네릭 싱글턴 팩토리로 만들 수 있다.
```java
public class GenericSingletonFactory {
    // 제네릭 싱글턴 인스턴스
    private static final GenericSingletonFactory INSTANCE = new GenericSingletonFactory();

    private GenericSingletonFactory() {}

    // 제네릭 타입의 싱글턴 인스턴스를 반환하는 메서드
    @SuppressWarnings("unchecked")
    public static <T> T getInstance(Class<T> type) {
        if (type == String.class) {
            return (T) "Singleton String Instance"; // 예: String 타입의 싱글턴 인스턴스
        } else if (type == Integer.class) {
            return (T) Integer.valueOf(42); // 예: Integer 타입의 싱글턴 인스턴스
        }
        throw new IllegalArgumentException("No singleton instance available for type: " + type);
    }
}
```
- 정적 팩토리의 메서드 참조를 공급자(함수형 인터페이스)로 사용할 수 있다.

#### 1,2의 단점
1. 리플렉션 공격 취약 : 권한이 있는 클라이언트는 `Reflection API`의 `AccessibleObject.setAccessible`을 사용하여 
private 생성자를 호출할 수 있다. 이러한 경우를 막으려면 생성자를 수정하여 두 번째 객체가 생성될 때
예외를 던지게 해야 한다.
2. 직렬화 위험성 : 직렬화된 인스턴스를 역직렬화 할 때 마다 새로운 인스턴스가 생겨난다. 따라서
`readResolve` 메서드를 제공해야 한다.
```java
public class Singleton {
    private static final Singleton INSTANCE = new Singleton();

    // 두 번째 객체 생성 시 예외를 방지하기 위한 플래그
    private static boolean instanceCreated = false;

    // 생성자 예외 코드 추가
    private Singleton() {
        if (instanceCreated) {
            throw new RuntimeException("Cannot create a second instance of Singleton");
        }
        instanceCreated = true;
    }

    // 인스턴스를 반환하는 정적 메서드
    public static Singleton getInstance() {
        return INSTANCE;
    }
}
```

#### 3. 원소가 하나인 enum 타입을 선언하는 방식
public 필드 방식과 비슷하다. **대부분의 상황에서는 이 방식이 싱글톤을 만드는 가장 좋은 방법이다.**
```java
enum SingletonEnum {
  INSTANCE;
  
  public static SingletonEnum getInstance() {
    return INSTANCE;
  }
  
}
```
**장점**
1. 클라이언트에서의 리플렉션 공격에도 안전하다. 
2. 추가의 코드 없이 직렬화할 수 있다.

**단점**
1. 클래스 상속이 필요할 때, enum 외에 클래스 상속이 불가능하다.