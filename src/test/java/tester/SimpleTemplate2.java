package tester;

import tester.template.BaseTemplate;

public class SimpleTemplate2 extends BaseTemplate {

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
    System.out.println("checkResponse");
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
