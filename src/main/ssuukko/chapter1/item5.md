## 자원을 직접 명시하지 말고 의존 객체 주입을 사용하라

클래스가 하나 이상의 자원에 의존한다면? 거의 대부분의 클래스를 하나 이상의 자원에 의존한다.

### 정적 유틸리티 클래스를 사용한 예
***
```java
public class SpellChecker {
    private static final Lexicon dictionary = ...; //① Ex. new koreanDictionary()

    private SpellChecker() {}

    public static boolean isValid(String word) { ...}

    public static List<String> suggestions(String typo) { ...}
}
```

### 싱글톤을 사용한 예
***
```java
public class SpellChecker {
    private final Lexicon dictionary = ...;//① Ex. new koreanDictionary()

    private SpellChecker(...) {}

    public static INSTANCE = new SpellChecker(...);

    public boolean isValid(String word) {...}

    public List<String> suggestions(String typo) {...}
}
```
위 두개의 클래스 `SpellChecker`는 `①` 단 한가지 사전에만 의존한다. 이는 아래의 단점을 발생시킨다.
- 예를 들어 실전에서는 한국어 사전, 일본어 사전, 중국어 사전 등 언어별로 따로 있고 특수 어휘용 사전을 별도로 두기도 한다.
- 단 한가지 사전으로 위의 모든 것을 충당할 수 없다.  
**즉, 사용하는 자원에 따라 동작이 달라지는 클래스에는 정적 유틸리티 클래스나 싱글턴 방식이 적합하지 않다.**

### dictionary 필드에서 `final`을 제거하고 다른 사전으로 교체하는 메서드`(changeDictionary)`를 추가
***

```java
public class SpellChecker {
    private Lexicon dictionary = ...;//①

    private SpellChecker(...) {}

    public boolean isValid(String word) {...}

    public List<String> suggestions(String typo) {...}

    public void changeDictionary(Lexion newDictionary) {
        this.dictionary = newDictionary;
    }
}
```
```java
public static void main(String[] args) {
    // 초기 사전
    Lexicon initialDictionary = new Lexicon(List.of("apple", "banana", "cherry"));
    SpellChecker.initializeDictionary(initialDictionary);

    // 사전 변경
    Lexicon newDictionary = new Lexicon(List.of("dog", "cat", "fish"));
    SpellChecker.changeDictionary(newDictionary);
```
위 `정적 유틸리티 클래스를 사용한 예`와 `싱글톤을 사용한 예`의 문제점을 해결하기 위와 같은 코드로 수정  
단순하게 기존 dictionary 필드에서 `final`을 제거하고 다른 사전으로 교체하는 메서드`(changeDictionary)`를 추가

**문제점**
- 어색하고 오류를 내기 쉬우며 멀티스레드 환경에서 쓸 수 없다.
```java
public static class DictionaryUpdater implements Runnable {
    @Override
    public void run() {
        Lexicon newDictionary = new Lexicon(List.of("dog", "cat"));
        SpellChecker.changeDictionary(newDictionary);
    }
}

public static void main(String[] args) {
    Lexicon initialDictionary = new Lexicon(List.of("apple", "banana"));
    SpellChecker.initializeDictionary(initialDictionary);

    // 한 스레드는 dictionary를 변경
    Thread updater = new Thread(new DictionaryUpdater());
    updater.start();

    // 메인 스레드는 동시에 dictionary를 읽음
    System.out.println(SpellChecker.isValid("apple")); // 결과가 예측 불가
}
```

### 의존 객체 주입의 한 형태로, 클래스를 생성할 대 의존객체를 주입
```java
public class SpellChecker {
    private final Lexicon dictionary;
    
    public SpellChecker(Lexicon dictionary) {
        this.dictionary = Object.requireNonNull(dictionary);
    }

    public boolean isValid(String word) {...}
    public List<String> suggestions(String typo){...}
}
```
위와 같이 `main`에서 `SpellChecker`라는 객체를 생성할때 `dictionary`라는 의존 객체를 주입해준다면 아래와 같은 장점이 발생한다.
- 자원이 몇 개든 의존 객체가 상관없이 잘 작동한다.
- 불변을 보장한다.
- 여러 클라이언트가 의존 객체들을 안심하고 공유할 수 있다.
  
다만 이러한 `의존 객체 주입`이 유연성 위와 같은 장점을 제공해주지만, 의존성이 수천 개나 되는 큰 프로젝트에서는 코드를 어지럽게 만들기도 한다.  
`대거(Dagger)`, `주스(Guice)`, `스프링(Spring)`과 같은 `의존 객체 주입 프레임워크` 사용해 코드의 어지러움을 해결할 수 있다.




