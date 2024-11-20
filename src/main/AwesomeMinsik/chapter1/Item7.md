## ITEM 7. 다쓴 객체 참조를 해제하라
참조가 없는 객체를 관리해주는 가비지 컬렉터를 사용하는 언어 특히 Java 를 사용하는 사람은
C, C++ 같이 수동으로 직접 메모리를 관리해야 할 필요가 없어 자칫 메모리 누수에 대한 신경을 안쓰게 될 수 있다.

가비지 컬렉터의 객체를 회수하는 조건은 객체에 참조가 없는 상태이다.
하지만 프로그램에서 사용이 끝난 객체의 참조를 여전히 가지고 있다면 해당 객체는 가비지 컬렉터의 대상이 아니므로
사용하지 않는데도 여전히 메모리에 남아 문제를 야기한다.

```java
public class Stack {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public Stack() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(Object e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public Object pop() {
        if (size == 0)
            throw new EmptyStackException();
        return elements[--size];
    }

    /**
     * 원소를 위한 공간을 적어도 하나 이상 확보한다.
     * 배열 크기를 늘려야 할 때마다 대략 두 배씩 늘린다.
     */
    private void ensureCapacity() {
        if (elements.length == size)
            elements = Arrays.copyOf(elements, 2 * size + 1);
    }

}
```
스택을 구현한 예제 클래스에서 앞서 말한 가비지 컬렉터가 처리하지 못하는 다쓴 객체로 메모리 누수문제를 볼 수있다.

배열 elements 의 크기는 16으로 초기화되며 고정값이기 때문에 
데이터의 갯수가 배열의 최대 길이만큼 도달하면 현재 크기의 2배+1 만큼 새로운 배열을 생성하고 기존 배열을 추가한다.
(이 때, 기존 배열은 더이상 객체를 참조하지 않게되어 가비지컬렉터에 의해 메모리에서 회수된다.)

문제는 pop() 메서드에 있다. LIFO 형식으로 마지막으로 저장한 값을
호출을 통해 size 를 줄이면서 배열 내부 값의 사용 범위를 제한하려고 한다.

여기서 중요한 것은 Java 배열은 생성될 때 크기가 정해지며, 크기는 변경할 수 없다.
elements 는 배열 참조이며,pop() 메서드가 동작하는 방식은 배열 크기를 줄이는 것이 아니라 데이터를 사용하는 범위를 제한하는 것이다.

size 는 배열의 크기가 아니라, 스택에 유효한 데이터의 개수를 추적하는 변수이기 때문에
값이 줄어든다고 배열이 줄어드는 것이 아니다. 

--size 를 통해 줄어든 값 이상의 데이터는 더 이상 접근할 수 없으므로 제거된 것처럼 보인다.
논리적으로 보았을 때 stack 의 크기를 감소시키는 것으로 보이지만, 물리적으로 메모리에는 elements 배열의 크기는 고정이기 때문에 데이터 접근이 제한된 것 뿐이지
여전히 배열에서 객체를 참조하고 있다. 즉, 가비지 컬렉터의 대상에서 제외되고 사용하지 않음에도 불구하고 메모리에 남는다.

이런 경우에는 활성영역(size 값 이하의 인덱스 내부영역) 외부의 다 쓴 객체를 명시적으로 참조 해제할 수 있다.

```java
public class Stack {
    //...
    // 코드 7-2 제대로 구현한 pop 메서드 (37쪽)
    public Object pop() {
        if (size == 0)
            throw new EmptyStackException();
        Object result = elements[--size];
        elements[size] = null; // 다 쓴 참조 해제
        return result;
    }
}
```
객체 참조를 null 을 통해 해제하는 방법으로 설계하면,  배열의 활성영역 외부에 있는 데이터에 접근할 경우
NullPointerException 을 발생시킨다. 논리적으로 코드를 잘못 작성했음을 알 수있게 해준다.

하지만 객체 참조 해제를 null 값으로 처리하는 방식은 예외적인 경우에 사용하는 것이 좋다

다 쓴 참조를 해제하는 가장 좋은 방법은 그 참조를 담은 변수를 필요한 범위 내에서만 사용하고,
변수의 유효 범위(scope)가 끝나는 시점에 자동으로 참조가 해제되게 만드는 것이다.

지역 변수는 메서드 실행이 끝나면 자동으로 유효 범위를 벗어나면서 참조가 사라지고, 객체가 가비지 컬렉터의 대상이 된다.

이런식으로 메모리를 직접 관리해야 하는 클래스는 항상 메모리 누수에 주의해야 한다.

### 캐시 또한 메모리 누수를 일으키는 주범이다

```java
Object key1 = new Object();
Object value1 = new Object();

Map<Object, List> cache = new HashMap<>();
cache.put(key1, value1);
```
위 코드에서 캐시를 통해 객체를 참조하고 캐시를 통해 객체를 참조한 사실을 잊는 경우에는
key1,value1 의 참조를 해제한다 하더라고 여전히 캐시에서 객체를 참조하고 있기 때문에
가비지 컬렉터의 대상에서 제외된다.
객체 참조를 캐시에 넣고 나중에 잊어버린다면, 객체를 다 쓴 다음에는 메모리 누수로 남는다.

이런 경우에는 더 이상 필요하지 않은 객체를 캐시에서 명시적으로 제거해야한다.


```java
import java.util.Map;
import java.util.WeakHashMap;

public class WeakHashMapExample {
    public static void main(String[] args) {
        Map<Object, String> map = new WeakHashMap<>();

        // 키 객체 생성
        Object key1 = new Object();
        Object key2 = new Object();

        // 엔트리 추가
        map.put(key1, "Value1");
        map.put(key2, "Value2");

        System.out.println("Before GC: " + map); // 모든 엔트리 출력

        // key1에 대한 강한 참조 제거
        key1 = null;

        // 가비지 컬렉션 강제 호출
        System.gc();

        // 잠시 대기하여 GC가 실행될 시간 제공
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("After GC: " + map); // key1에 해당하는 엔트리 제거
    }
}

```
이렇게 캐시를 WeakHashMap 을 사용하면 강한 참조를 하고있는 key1 가 참조 해제될 경우
가비지 컬렉터의 대상이 되고 해당 참조의 정보를 ReferenceQueue 알린다
이 때 WeakHashMap 이 이를 감지하여 관련 엔트리를 제거한다.

책에서는 entry 가 살아있는 캐시 즉,사용중인 항목이 필요한 경우라면 WeakHashMap 을 사용하는 캐시로 만들면
다쓴 entry 는 자동으로 제거된다. 이런경우에는 WeakHashMap 이 유용하다.
