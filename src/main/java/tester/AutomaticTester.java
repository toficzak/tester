package tester;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.reflections.Reflections;
import krzysztof.property.reader.PropertyReader;
import tester.annotation.Scenario;
import tester.annotation.Scenarios;
import tester.helper.DatabaseCleaner;
import tester.helper.SessionCleaner;

public class AutomaticTester {

  private List<Class<?>> testClasses = new ArrayList<>();

  private DatabaseCleaner databaseCleaner;
  private SessionCleaner sessionCleaner;
  private final String scanRootPackage;

  private int overallPassed = 0;
  private int overallFailed = 0;
  private int overallIgnoredMethods = 0;
  private int overallMethods = 0;


  public AutomaticTester() {
    PropertyReader propertyReader =
        new PropertyReader("config.properties", this.getClass().getClassLoader());
    scanRootPackage = propertyReader.getProperty("scan.root.package");
  }

  public void addDatabaseCleaner(DatabaseCleaner cleaner) {
    this.databaseCleaner = cleaner;
  }

  public void addSessionCleaner(SessionCleaner cleaner) {
    this.sessionCleaner = cleaner;
  }

  public void runTestClass(Class<?> clazz) {
    Method[] methods = clazz.getMethods();

    List<Method> testMethods = new ArrayList<>();

    for (Method m : methods) {
      if (m.isAnnotationPresent(Scenario.class)) {
        testMethods.add(m);
      }
    }
    System.out.println("");
    System.out.println(String.format("Launching %s [%d test%s]", clazz.getSimpleName(),
        testMethods.size(), testMethods.size() > 1 ? "s" : ""));

    int passed = 0;
    int failed = 0;
    int ignored = 0;

    Object initializedTestClass = null;
    try {
      initializedTestClass = clazz.newInstance();
    } catch (InstantiationException | IllegalAccessException e1) {
      e1.printStackTrace();
    }

    overallMethods = testMethods.size();

    for (Method testMethod : testMethods) {
      try {
        if (!testMethod.getAnnotation(Scenario.class).ignore().isEmpty()) {
          ignored++;
          System.out.println(
              String.format("-x %s... ignored. Reason: %s.",
                  testMethod.getName(),
                  testMethod.getAnnotation(Scenario.class).ignore()));
          continue;
        }
        System.out.print(String.format("-> %s... ", testMethod.getName()));
        testMethod.invoke(initializedTestClass);
        System.out.println("passed.");
        passed++;
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        System.out.println("failed.");
        e.printStackTrace();
        failed++;
      } finally {
        if (this.sessionCleaner != null) {
          sessionCleaner.clear();
        }
        if (this.databaseCleaner != null) {
          databaseCleaner.clean();
        }
      }
    }

    System.out.println("--------------------");
    System.out.println(String.format("%s results:", clazz.getSimpleName()));
    System.out.println(String.format("> tests: %d", testMethods.size()));
    System.out.println(String.format("> passed: %d", passed));
    System.out.println(String.format("> failed: %d", failed));
    System.out.println(String.format("> ignored: %d", ignored));
    if (!testMethods.isEmpty()) {
      System.out.println(String.format("> overall: %d/%d (%s%%)", passed, failed,
          (float) passed / (passed + failed) * 100f));
    }

    overallPassed += passed;
    overallFailed += failed;
    overallIgnoredMethods += ignored;

    System.out.println("####################");
  }

  public void runTestMethod(Class<?> clazz, String method) {
    Method[] methods = clazz.getMethods();

    List<Method> testMethods = new ArrayList<>();

    for (Method m : methods) {
      if (m.isAnnotationPresent(Scenario.class)) {
        testMethods.add(m);
      }
    }
    System.out.println("");
    System.out.println(String.format("Launching %s [%d test%s]", clazz.getSimpleName(),
        testMethods.size(), testMethods.size() > 1 ? "s" : ""));

    int passed = 0;
    int failed = 0;
    int ignored = 0;

    Object initializedTestClass = null;
    try {
      initializedTestClass = clazz.newInstance();
    } catch (InstantiationException | IllegalAccessException e1) {
      e1.printStackTrace();
    }

    overallMethods = testMethods.size();

    Optional<Method> optMethod = testMethods.stream()
        .filter(m -> m.getName().equals(method))
        .findFirst();

    if (optMethod.isEmpty()) {
      throw new IllegalStateException("No such method.");
    }

    Method testMethod = optMethod.get();

    try {
      if (!testMethod.getAnnotation(Scenario.class).ignore().isEmpty()) {
        ignored++;
        System.out.println(
            String.format("-x %s... ignored. Reason: %s.",
                testMethod.getName(),
                testMethod.getAnnotation(Scenario.class).ignore()));
      } else {
        System.out.print(String.format("-> %s... ", testMethod.getName()));
        testMethod.invoke(initializedTestClass);
        System.out.println("passed.");
        passed++;
      }
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      System.out.println("failed.");
      e.printStackTrace();
      failed++;
    } finally {
      if (this.sessionCleaner != null) {
        sessionCleaner.clear();
      }
      if (this.databaseCleaner != null) {
        databaseCleaner.clean();
      }
    }

    System.out.println("--------------------");
    System.out.println(String.format("%s results:", clazz.getSimpleName()));
    System.out.println(String.format("> tests: %d", testMethods.size()));
    System.out.println(String.format("> passed: %d", passed));
    System.out.println(String.format("> failed: %d", failed));
    System.out.println(String.format("> ignored: %d", ignored));
    if (!testMethods.isEmpty()) {
      System.out.println(String.format("> overall: %d/%d (%s%%)", passed, failed,
          (float) passed / (passed + failed) * 100f));
    }

    overallPassed += passed;
    overallFailed += failed;
    overallIgnoredMethods += ignored;

    System.out.println("####################");
  }

  public void runAllScenarios() {

    Set<Class<?>> scenarios = new Reflections(scanRootPackage)
        .getTypesAnnotatedWith(Scenarios.class);

    for (Class<?> scenario : scenarios) {
      this.testClasses.add(scenario);
    }

    System.out.println(String.format("%s: Launching e2e tests.", LocalDateTime.now()));

    for (Class<?> clazz : testClasses) {
      this.runTestClass(clazz);
    }

    System.out.println();
    System.out.println(String.format("%s: Ended e2e tests.", LocalDateTime.now()));
    System.out.println(
        String.format("> tests: %d", overallPassed + overallFailed + overallIgnoredMethods));
    System.out.println(String.format("> passed: %d", overallPassed));
    System.out.println(String.format("> failed: %d", overallFailed));
    System.out.println(String.format("> ignored: %d", overallIgnoredMethods));
    if (overallPassed + overallFailed - overallIgnoredMethods > 0) {
      System.out.println(String.format("> overall: %d/%d (%s%%)", overallPassed, overallFailed,
          (float) overallPassed / (overallPassed + overallFailed) * 100f));
    }
  }

}
