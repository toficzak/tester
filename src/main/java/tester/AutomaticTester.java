package tester;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import krzysztof.db.connector.DatabaseCleaner;

public class AutomaticTester {

  private List<Class<?>> testClasses = new ArrayList<>();

  private DatabaseCleaner databaseCleaner;

  public void addDatabaseCleaner(DatabaseCleaner cleaner) {
    this.databaseCleaner = cleaner;
  }

  public void addTestClass(Class<?> testClass) {
    this.testClasses.add(testClass);
  }

  public void testAll() {

    System.out.println(String.format("%s: Launching e2e tests.", LocalDateTime.now()));

    int overallMethods = 0;
    int overallPassed = 0;
    int overallFailed = 0;

    for (Class<?> clazz : testClasses) {
      Method[] methods = clazz.getMethods();

      List<Method> testMethods = new ArrayList<>();
      for (Method m : methods) {
        if (m.isAnnotationPresent(Scenario.class)) {
          testMethods.add(m);
          overallMethods++;
        }
      }
      System.out.println("");
      System.out.println(String.format("Launching %s [%d test%s]", clazz.getSimpleName(),
          testMethods.size(), testMethods.size() > 1 ? "s" : ""));

      int passed = 0;
      int failed = 0;

      Object initializedTestClass = null;
      try {
        initializedTestClass = clazz.newInstance();
      } catch (InstantiationException | IllegalAccessException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }

      for (Method testMethod : testMethods) {
        try {
          System.out.print(String.format("-> %s...", testMethod.getName()));
          testMethod.invoke(initializedTestClass);
          System.out.println(" passed.");
          passed++;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
          System.out.println(" failed.");
          e.printStackTrace();
          failed++;
        } finally {
          databaseCleaner.clean();
        }
      }

      System.out.println("--------------------");
      System.out.println(String.format("%s results:", clazz.getSimpleName()));
      System.out.println(String.format("> tests: %d", testMethods.size()));
      System.out.println(String.format("> passed: %d", passed));
      System.out.println(String.format("> failed: %d", failed));
      if (!testMethods.isEmpty()) {
        System.out.println(String.format("> overall: %d/%d (%s%%)", passed, failed,
            (float) passed / testMethods.size() * 100f));
      }

      overallPassed += passed;
      overallFailed += failed;

      System.out.println("####################");
    }

    System.out.println();
    System.out.println(String.format("%s: Ended e2e tests.", LocalDateTime.now()));
    System.out.println(String.format("> tests: %d", overallMethods));
    System.out.println(String.format("> passed: %d", overallPassed));
    System.out.println(String.format("> failed: %d", overallFailed));
    if (overallMethods > 0) {
      System.out.println(String.format("> overall: %d/%d (%s%%)", overallPassed, overallFailed,
          (float) overallPassed / overallMethods * 100f));
    }
  }

}
