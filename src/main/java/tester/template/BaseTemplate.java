package tester.template;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseTemplate implements Template {

  protected Map<String, Object> artefacts = new HashMap<>();

  @Override
  public void environmentSetup() {

  }

  @Override
  public void perform() {

  }

  @Override
  public void checkResponse() {

  }

  @Override
  public void checkDbState() {

  }

}
