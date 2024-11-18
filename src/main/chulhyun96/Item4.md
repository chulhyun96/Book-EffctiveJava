## Item4 -> 인스턴스화를 막으려거든 private 생성자를 사용하라

---

### 서론
해당 Item에서 말하고자 하는 **내용은 정적 메서드와 정적 필드만을 담은, 즉 객체를 생성해서 사용할 필요가 없는
유틸 클래스가 이따금 필요할 때가 있다는 것이다.** 
이 때 객체의 생성을 방지하기 위해 `abstract` 키워드를 사용하여 추상 클래스로 만드는데 이는 잘못된 사용 방식이라는 것이다.

### 본론
만약 `abstract` 키워드를 사용하여 정적 유틸 클래스를 만들 경우 아래 처럼

```java
public abstract class AmountDivider {

    public static int divideLottoAmount(int amount) {
        return amount / DIVISION_UNIT;
    }
}

public class AmountDividerExtendsClass extends AmountDivider {

    public AmountDividerExtendsClass() {
        System.out.println("Hi~~");
    }
}

public class Main {
    public static void main(String[] args) {
        AmountDividerExtendsClass amountDividerExtendsClass = new AmountDividerExtendsClass();
    }
}
```
![img.png](img.png)

상속받을 자식 클래스를 생성하고 Main 메서드에서 처럼 결국에는 객체를 생성할 수가 있다. 그리고 `abstract`키워드 자체가 추상 클래스이기 때문에
다른 개발자 입장에서는 상속받아서 자식 클래스를 생성해서 사용하라는 것처럼 보일 수도 있다.

```java
public final class AmountDivider {
    private AmountDivider {
        throw new AssertionError("해당 클래스는 객체 생성을 하면 안되요~");
    }
    public static int divideLottoAmount(int amount) {
        return amount / DIVISION_UNIT;
    }
}

public class AmountDividerExtendsClass extends AmountDivider{
    public AmountDividerExtendsClass() {
        System.out.println("Hi~~");
    }
}

```
그러니 유틸 클래스를 생성하거나 객체 생성이 불필요한 클래스를 설계할 때는 객체의 생성을 방지하고자 `abstract` 키워드를 사용하지말고 `final` 키워드와 함께
생성자를 `private`로 막는게 확실한 방법이 될 것이다.

`final`로 생성된 클래스는 클래스에서 상속이 불가능하다. 이것으로 1차적인 피해를 막을 수 있으며 다른 클래스를 상속할 경우에 컴파일 에러가 나타난다.
거기에 추가적으로 `private` 생성자를 만들어주고 `AssertionError`를 던져서 예기치 못한 추가적인 2차피해도 막을 수 있다.

유틸리티 클래스에서는 주로 **“절대 호출되지 않아야 할 생성자”**를 차단하기 위해 `private` 생성자에서 `AssertionError`를 활용한다.
이 방식은 유틸리티 클래스 설계의 표준 패턴이며, 다른 `java.util` 클래스들을 살펴보자.

```java
public class Collections {
    // Suppresses default constructor, ensuring non-instantiability.
    private Collections() {
    }
}

public final class Objects {
    private Objects() {
        throw new AssertionError("No java.util.Objects instances for you!");
    }
}
```
자바의 유틸 클래스인 Collections와 Objects 클래스들이다. Colletions는 final 키워드가 없긴하지만,
확실히 객체의 생성을 방지하기 위해 private로 막아뒀다.

그리고 두번째인 Objects 클래스는 final 키워드와 private 생성자 그리고 에러를 던짐으로써 어떠한 상황에서든
절대 객체가 생성되면 안되는 것을 뜻하고 있다.


## 결론
이따금 유틸 클래스를 생성할 때 추상 클래스 만으로는 객체의 생성을 완벽히 제지하지 못하기 때문에
final 키워드 + 생성자를 private로 막아둠으로써 객체의 생성을 완전히 방지할 수 있다.

유틸 클래스를 만들 때는 3가지를 고려해서 만들어주자.
1. 인스턴스화 방지 -> 유틸리티 클래스는 모두 private 생성자를 통해 인스턴스화를 방지.

2. 정적 메서드만 제공 -> 클래스의 모든 기능은 정적 메서드로 제공되게 하면 객체를 생성할 필요가 없다.
 
3. throw new AssertionError() -> 실수로라도 내부에서 호출되지 않도록 명시적으로 예외를 던진다.

물론 이에 따른 장단점도 존재한다. 여기에서 말하는 장단점은 유틸클래스의 장단점이다.

`final` 키워드와 `private`로 객체 생성을 완전히 방지함으로써, **단순성과, 안전성, 불변성을 제공한다.**
**하지만 이는 확장성을 저하시키므로 OCP 확장의 가능성이 생긴다면 인터페이스로 만들어서 구현체를 확장시키는 방법을 고려**해야한다.
게다가 많은 정적 유틸리티 메서드를 가진 클래스는 메모리를 과도하게 사용할 가능성이 있기 때문에 볼륨이 너무 커지지 않도록 제한하거나,
**볼륨이 너무 커질경우 상태를 가지게 함으로써 객체로 관리하는 경우를 고려해보는 것이 좋다.**








