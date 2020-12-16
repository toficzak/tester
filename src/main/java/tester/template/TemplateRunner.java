package tester.template;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.reflections.Reflections;
import krzysztof.property.reader.PropertyReader;

public class TemplateRunner {

  private List<Template> testScenarios = new ArrayList<>();
  private String scanRootPackage;

  int success = 0;
  int failures = 0;

  public TemplateRunner() {
    PropertyReader propertyReader =
        new PropertyReader("config.properties", this.getClass().getClassLoader());
    scanRootPackage = propertyReader.getProperty("scan.root.package");
  }

  public void run(Template scenario) {
    this.testScenarios.add(scenario);
    this.performTests();
  }

  public void run(Collection<Template> scenarios) {
    this.testScenarios.addAll(scenarios);
    this.performTests();
  }

  @SuppressWarnings("unchecked")
  public void runAll() {
    new Reflections(scanRootPackage).getSubTypesOf(Template.class)
        .stream()
        .map(c -> (Class<Template>) c)
        .filter(c -> !c.isAssignableFrom(BaseTemplate.class))
        .map(this::instantiate)
        .forEach(this.testScenarios::add);
    this.performTests();
  }

  private void performTests() {
    this.testScenarios.forEach(this::performTest);
    System.out.println();
    System.out.println("Performed: " + (success + failures) + " tests.");
    System.out.println("Result: " + success + " succeedded, " + failures + " failed.");
  }

  private void performTest(Template template) {
    System.out.println("______________________________");
    String startMessage =
        String.format("Performing: %s", template.getClass().getSimpleName());
    System.out.println(startMessage);

    try {
      template.before();
      template.environmentSetup();
      template.perform();
      template.checkResponse();
      template.checkDbState();
      template.clean();

      System.out.println();
      System.out.println("Test passed.");
      this.success++;
    } catch (Exception e) {
      System.out.println("Test failed due to:");
      e.printStackTrace();
      this.failures++;
      return;
    } finally {
      System.out.println("______________________________");
    }
  }

  private Template instantiate(Class<Template> clazz) {
    try {
      return clazz.getConstructor().newInstance();
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
        | InvocationTargetException | NoSuchMethodException | SecurityException e) {
      e.printStackTrace();
      throw new RuntimeException();
    }
  }

}
