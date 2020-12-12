package $package$

import munit.CatsEffectSuite

class HelloSuite extends CatsEffectSuite {
  test("hello") {
    assertEquals(Hello.hello("you"), "Hello you")
  }
}
