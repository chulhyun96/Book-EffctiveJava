## 생성자에 매개변수가 많다면 빌더를 고려하라
정적 팩터리 메서드와 생성자는 선택적 매개변수가 많을 때 적절히 대응하기 어렵다는 공통점을 갖는다.  
선택적 매개변수가 많을 때 활용할 수 있는 방법은 아래와 같다.
- 점층적 생성자 패턴(telescoping constructor pattern)
- 자바빈즈 패턴(JavaBeans pattern)
- 빌더 패턴(Builder pattern)


### 점층적 생성자 패턴(Telescoping Constructor Pattern)
***
- 필수 매개변수만 받는 생성자
- 필수 매개변수와 선택 매개변수를 한개만 받는 생성자
- 필수 매개변수와 선택 매개변수를 두개 받는 생성자 ... 이처럼 생성자를 늘려가는 형태

**단점**
- 확장하기 힘들다.
- 매개변수가 연달아 늘어있기 때문에 클라이언트의 실수로 매개변수의 순서를 바꿔도 컴파일러가 알아채지 못하고, 결국 런타임에 엉뚱한 동작 실행

```java
public class NutritionFacts {
    private final int servingSize;  // 필수
    private final int servings;     // 필수
    private final int calories;     // 선택
    private final int fat;          // 선택
    private final int sodium;       // 선택
    private final int carbohydrate; // 선택

    public NutritionFacts(int servingSize, int servings) {
        this(servingSize, servings, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories) {
        this(servingSize, servings, calories, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories, int fat) {
        this(servingSize, servings, calories, fat, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories, int fat, int sodium) {
        this(servingSize, servings, calories, fat, 0);
    }
    
    public NutritionFacts(int servingSize, int servings, int calories, int fat, int sodium, int carbohydrate) {
        this.servingSize = servingSize;
        this.servings = servings;
        this.calories = calories;
        this.fat = fat;
        this.sodium = sodium;
        this.carbohydrate = carbohydrate;
    }
}
```

### 자바빈즈 패턴(JavaBeans Pattern)
***
- 매개변수가 없는 `기본 생성자`를 생성하고, `setter`를 호출해 원하는 값을 설정
- `점층적 패턴`에 비해 가독성이 좋다.

**단점**
- 객체 한개를 만들려면 여러개의 메서드를 호출 해야함
- 객체가 완전해지기 전까지는 `일관성(consistency)`이 무너지는 상태에 놓임
- 클래스를 불변으로 유지할 수 없다.

```java
public class NutritionFacts {
    private int servingSize = -1;  // 필수
    private int servings = -1;     // 필수
    private int calories = 0;
    private int fat = 0;
    private int sodium = 0;
    private int carbohydrate = 0;

    public NutritionFacts() {
    }

    public void setServingSize(int val) {
        servingSize = val;
    }

    public void setServings(int val) {
        servings = val;
    }

    public void setCalories(int val) {
        calories = val;
    }

    public void setFat(int val) {
        fat = val;
    }

    public void setSodium(int val) {
        sodium = val;
    }

    public void setCarbohydrate(int val) {
        carbohydrate = val;
    }
}
```
```java
// 점층적 생성자 패턴에 비해 확장하기 쉽고, 인스턴스를 만들기 쉽고, 가독성이 좋아진다.
NutritinFacts cocaCola = new NutritionFacts();
cocaCola.setServingSize(240);
cocaCola.setServings(8);
cocaCola.setCalories(100);
cocaCola.setSodium(35);
cocaCola.setCarbohydrate(27);
```

### 빌더 패턴(Builder Pattern)
***
- 점층적 생성자 패턴의 **안정성**과 자바빈즈 패턴의 **가독성**을 겸비한 빌더 패턴
- 클라이언트는 필요한 객체를 직접 만드는 대신, 필수 매개 변수만으로 생성자를 호출해 빌더 객체를 얻는다.
- 그 다음 `builder` 객체가 제공하는 일종의 `setter` 메서드들로 원하는 선택 매개변수들을 설정한다.
- 마지막으로 매개변수가 없는 `build` 메서드를 호출해서 객체를 얻는다.
  - 빌더는 생성할 클래스 안에 정적 멤버 클래스로 만들어두는 것이 보통이다.

**단점**
- 선택적 매개변수를 많이 받는 객체를 생성하기 위해서는 먼저 빌더 클래스부터 정의해야한다. 빌더의 생성비용이 크지는 않지만, 성능에 민감한 상황에서는 문제가 될 수 있다.
- 매개변수가 4개보다 적다면, `점층적 생성자 패턴`을 사용하는 것이 더 좋다.

```java
public class NutritionFacts {
    private final int servingSize;
    private final int servings;
    private final int calories;
    private final int fat;
    private final int sodium;
    private final int carbohydrate;

    public static class Builder {
        private final int servingSize;  // 필수
        private final int servings;     // 필수
        private int calories = 0;
        private int fat = 0;
        private int sodium = 0;
        private int carbohydrate = 0;

        public Builder(int servingSize, int servings) {
            this, servingSize = serginsSize;
            this.servings = servings;
        }

        public Builder fat(int val) {
            fat = val;
            return this;
        }

        public Builder sodium(int val) {
            sodium = val;
            return this;
        }

        public Builder carbohydrate(int val) {
            carbohydrate = val;
            return this;
        }

        public NutritionFacts build() {
            return new NutritionFacts(this);
        }
    }

    private NutirionFacts(Builder builder) {
        servingSize = builder.servingSize;
        servings = builder.servings;
        calories = builder.calories;
        fat = builder.fat;
        sodium = builder.fat;
        carbohydrate = builder.carbohydrate;
    }
}
```

자기 자신을 반환하기 때문에 연쇄적인 호출이 가능하고, 이러한 방식을 `Fluent API` 혹은 `Method chaining`이라고 한다.
```java
NutritionFacts cocaCola = new Builder(240, 8)
        .calories(100).sodium(35).carbohydrate(27).build();
```
