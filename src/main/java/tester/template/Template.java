package tester.template;

interface Template {

  default void before() {

  }

  void environmentSetup();

  void perform();

  void checkResponse();

  void checkDbState();

  default void clean() {

  }
}
