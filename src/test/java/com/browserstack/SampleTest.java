package com.browserstack;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class SampleTest extends BrowserStackJUnitTest {

  @Test
  public void test_google_BrowserStack() throws Exception {
    driver.get("https://www.google.com/ncr");
    WebElement element = driver.findElement(By.name("q"));
    element.sendKeys("BrowserStack");
    element.submit();
    Thread.sleep(5000);

    assertEquals("BrowserStack - Google Search", driver.getTitle());
  }

  @Test
  public void test_google_Google() throws Exception {
    driver.get("https://www.google.com/ncr");
    WebElement element = driver.findElement(By.name("q"));
    element.sendKeys("Google");
    element.submit();
    Thread.sleep(5000);

    assertEquals("Google - Google Search", driver.getTitle());
  }

}
