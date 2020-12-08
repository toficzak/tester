package tester;

import org.junit.jupiter.api.Test;

class AutomaticTesterTest {

  @Test
  void test() {
    AutomaticTester tester = new AutomaticTester();
    tester.runAllScenarios();

    new AutomaticTester().runTestMethod(SimpleTest.class, "test3");
  }

}
