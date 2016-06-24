package net.evenh.versionmonitor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;


@RunWith(JUnit4.class)
@SpringApplicationConfiguration(classes = App.class)
@WebAppConfiguration
public class AppTest {
  @Test
  public void should_add_more_tests() {
    assert(true);
  }
}
