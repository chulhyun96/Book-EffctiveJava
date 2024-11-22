## equals를 재정의할 때는 hashCode도 재정의하라
**`equals`를 재정의한 클래스 모두에서 `hashCode`도 재정의해야 한다.**
그렇지 않으면 `hashCode` 일반 규약을 어기게 되어서 해당 클래스의 인스턴스를
`HashMap`이나 `HashSet`같은 컬렉션의 Key로 사용할 떄 문제를 일으킬 것이다.

### Object 클래스의 hashCode 규약
- `equals` 비교에 사용되는 정보가 변경되지 않았다면, 애플리케이션이 실행되는 동안
그 객체의 `hashCode` 메서드는 몇 번을 호출해도 일관되게 항상 같은 값을 반환해야 한다.
- `equals(Object)`가 두 객체를 같다고 판단했으면, 두 객체의 `hashCode`는 같은 값을 반환해야 한다.
- `equals(Object)`가 두 객체를 다르다고 판단했더라도, 두 객체의 `hashCode`가 서로 다른 값을
반환할 필요는 없다. 단, 다른 객체에 대해서는 다른 값을 반환해야 해시테이블의 성능이 좋아진다.(충돌 최소화)

#### 컬렉션 문제 예시
```java
import java.util.HashMap;
import java.util.Map;

public static void main(String[] args) {
    Map<PhoneNumber, String> m = new HashMap<>();
    m.put(new PhoneNumber(010, 8678, 5309), "김영한");
    
    m.get(new PhoneNumber(010, 8678, 5039)); // null
}
```
위 코드에서 `m.get(new PhoneNumber(010, 8678, 5039));`을 실행했을 때 논리적으로 생각하면
`김영한`이 나와야 할 것 같지만, 실제로는 `null`을 반환한다.
이유는 `PhoneNumber` 클래스에 `hashCode`를 재정의하지 않았기 때문에 논리적으로 동등한 두 객체가
서로 다른 해시코드를 반환하기 때문이다. 두 번째 규약을 지키지 않은 것이다.

#### 최악의 hashCode 구현 예시
```java
@Override
public int hashCode() { return 42; }
```
위 문제를 해결하기 위해 이렇게 `hashCode`를 구현하면 원하는 결과를 얻을 수 있기는 하다.
하지만 모든 `PhoneNumber` 클래스 객체에게 똑같은 값만 내어주므로 모든 객체가 해시테이블
하나의 버킷에 담겨 마치 `LinkedList`처럼 동작한다. 그 결과 평균 수행 시간이 O(1)인 해시테이블이
O(n)으로 느려져, 객체가 많아지면 엄청난 성능 저하가 발생한다.

#### 성능 저하 예시

#### 위 hashCode를 사용했을 때
```java
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
```
```Console
s = 5000
Time taken: 519200ms
```

#### hashCode를 올바르게 재정의 했을 때
```Console
s = 5000
Time taken: 24000ms
```
이것이 세 번째 규약을 지켜야 하는 이유이다. (충돌 최소화, 성능 최적화를 위함)
그리고 만약 `equals`를 IDE의 힘을 빌리지 않고 본인이 재정의할 때 `equals`에서
사용하지 않는 필드라면 `hashCode`에서도 **반드시 제외해야 한다.** 이렇게 하지 않으면
`hashCode` 규약 두 번째를 어기게 될 위험이 있다.

### 클래스가 불변이고 해시코드를 계산하는 비용이 클 때
클래스가 불변이고 해시코드를 계산하는 비용이 크다면, 매번 새로 계산하기 보다는
클래스의 필드로 만들어 두고 캐싱하는 방식을 고려할 수 있다.
`hashCode`가 처음 불릴 때 계산하는 지연 초기화 전략의 예시를 들 수 있다.
(필드를 지연 초기화하려면 그 클래스를 스레드 안전하게 만들도록 신경 써야 한다. - item83)
#### hashCode를 지연 초기화하는 hashCode 메서드

```java
import java.util.Objects;

public class PhoneNumber {
    private int first;
    private int second;
    private int third;
    private int hashCode;
    
    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
           hashCode = Objects.hash(first, second, third);
        }
        return hashCode;
    }
}
```
`PhoneNumber` 클래스에서 `hashCode`라는 필드를 만들어 0으로 초기화 시켜놓고,
`hashCode()`메서드를 쓸 때, 캐싱하고 변수에 담아 넣는 방식이다.

### 결론
이 아이템에서는 `equals`를 재정의할거면 `hashCode`도 같이 재정의해야 하는 이유와 `hashCode` 규약,
재정의하는 방법을 알려준다. 이렇게 하지 않으면 실제로 프로그램에서 생각하지 못하는 에러나 성능 저하를
만날 수 있기 때문에 꼭 같이 재정의해주자. 
 