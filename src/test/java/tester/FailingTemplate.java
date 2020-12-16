package tester;

import tester.template.BaseTemplate;

public class FailingTemplate extends BaseTemplate {

  @Override
  public void before() {
    System.out.println("before");
  }

  @Override
  public void environmentSetup() {
    System.out.println("environmentSetup");
  }

  @Override
  public void perform() {
    System.out.println("perform");
  }


  @Override
  public void checkResponse() {
    throw new RuntimeException("Failing.");
  }

  @Override
  public void checkDbState() {
    System.out.println("checkDbState");
  }

  @Override
  public void clean() {
    System.out.println("clean");
  }
}
