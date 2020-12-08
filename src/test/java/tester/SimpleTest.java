package tester;

import tester.annotation.Scenario;
import tester.annotation.Scenarios;

@Scenarios
public class SimpleTest {

  @Scenario(ignore = "Blocked by other thing.")
  public void test() {}

  @Scenario(ignore = "Blocked by other thing.")
  public void test2() {}

  @Scenario
  public void test3() {}

}
