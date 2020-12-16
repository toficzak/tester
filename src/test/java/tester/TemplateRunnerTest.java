package tester;

import java.util.List;
import org.junit.jupiter.api.Test;
import tester.template.TemplateRunner;

public class TemplateRunnerTest {

  @Test
  public void testOne() {
    new TemplateRunner().run(new SimpleTemplate());
  }

  @Test
  public void testMultiple() {
    new TemplateRunner()
        .run(List.of(new SimpleTemplate(), new SimpleTemplate(), new SimpleTemplate2()));
  }

  @Test
  public void testAll() {
    new TemplateRunner().runAll();
  }
}
