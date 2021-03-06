package ru.stqa.pft.addressbook.appmanager;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import ru.stqa.pft.addressbook.model.ContactData;
import ru.stqa.pft.addressbook.model.Contacts;

import java.util.List;

/**
 * Created by Ксюшенька on 01.03.2016.
 */
public class ContactHelper extends HelperBase {

  public ContactHelper(WebDriver wd) {
    super(wd);
  }

  public int count() {
    return wd.findElements(By.name("selected[]")).size();
  }

  public void fillContactForm(ContactData contactData, boolean creation) {
    type(By.name("firstname"), contactData.getFirstname());
    type(By.name("lastname"), contactData.getLastname());
    type(By.name("email"), contactData.getEmail());
//    attach(By.name("photo"), contactData.getPhoto());

    if (creation) {
      if (contactData.getGroups().size() > 0) {
        Assert.assertTrue(contactData.getGroups().size() == 1);
        new Select(wd.findElement(By.name("new_group"))).selectByVisibleText(contactData.getGroups().iterator().next().getName());
      }
    } else {
      Assert.assertFalse(isElementPresent(By.name("new_group")));
    }
  }

  public void submitContactCreation() {
    click(By.name("submit"));
  }

  public void initContactCreation() {
    click(By.linkText("add new"));
  }

  public void selectContact(int index) {
    wd.findElements(By.name("selected[]")).get(index).click();
  }

  public void selectContactById(int id) {
    wd.findElement(By.cssSelector("input[value='" + id + "']")).click();
  }

  private void returnToHomePage() {
    click(By.linkText("home"));
  }

  public void submitContactDeletion() {
    click(By.xpath("//div[@id='content']/form[2]/div[2]/input"));
    wd.switchTo().alert().accept();

  }

  private void selectElement(By name) {
    wd.findElement(name);
  }


  public void initContatModificationById(int id) {
//        wd.findElement(By.xpath("//div/div[4]/form[2]/table/tbody/tr[2]/td[7]/a/img")).click();
//        wd.findElement(By.xpath("//table[@id='maintable']/tbody/tr[2]/td[7]/a/img")).click();
    wd.findElement((By.cssSelector(String.format("a[href='edit.php?id=%s']", id)))).click();
//        wd.findElement(By.xpath(String.format("//table[@id='maintable']/tbody/tr[2]/td['%s']/a/img", id))).click();
  }

  public void submitModification() {
    click(By.name("update"));
  }

  public boolean isThereAContact() {
    return isElementPresent(By.name("selected[]"));
  }

  public void create(ContactData contact) {
    initContactCreation();
    fillContactForm(contact, true);
    submitContactCreation();
    contactCache = null;
    returnToHomePage();
  }


  public void modify(ContactData contact) {
    initContatModificationById(contact.getId());
    fillContactForm(contact, false);
    submitModification();
    contactCache = null;
    returnToHomePage();
  }

  public void delete(ContactData contact) {
    selectContactById(contact.getId());
    submitContactDeletion();
    contactCache = null;
    returnToHomePage();
  }


  private Contacts contactCache = null;

  public Contacts all() {
    if (contactCache != null) {
      return new Contacts(contactCache);
    }
    contactCache = new Contacts();

    List<WebElement> rows = wd.findElements(By.tagName("tr"));
    for (int i = 1; i < rows.size(); i++) {
//      List<WebElement> cells = row.findElements(By.tagName("td"));
      int id = Integer.parseInt(rows.get(i).findElement(By.tagName("input")).getAttribute("value"));
      String lastname = rows.get(i).findElements(By.tagName("td")).get(1).getText();
      String firstname = rows.get(i).findElements(By.tagName("td")).get(2).getText();
      String allAddresses = rows.get(i).findElements(By.tagName("td")).get(3).getText();
      String allEmails = rows.get(i).findElements(By.tagName("td")).get(4).getText();
      String allPhones = rows.get(i).findElements(By.tagName("td")).get(5).getText();

      ContactData contact = new ContactData().withId(id).withFirstname(firstname).withLastname(lastname)
              .withAllPhones(allPhones).withAllAddresses(allAddresses).withAllEmails(allEmails);
      contactCache.add(contact);
    }
    return new Contacts(contactCache);
  }

  private void initContactDetailsFormById(int id) {
//        wd.findElement(By.xpath("//table[@id='maintable']/tbody/tr[2]/td[7]/a/img")).click();
    wd.findElement(By.xpath(String.format("//table[@id='maintable']/tbody/tr[2]/td['%s']/a/img", id))).click();
//        wd.findElement((By.xpath(String.format("//div/div[4]/form[2]/table/tbody/tr[2]/td[%s]", id)))).click();
//        wd.findElement((By.cssSelector(String.format("a[href='view.php?id=%s']", id)))).click();
  }


//    }

  public ContactData infoFromEditForm(ContactData contact) {
    returnToHomePage();
    initContatModificationById(contact.getId());
    String firstname = wd.findElement(By.name("firstname")).getAttribute("value");
    String lastname = wd.findElement(By.name("lastname")).getAttribute("value");
    String home = wd.findElement(By.name("home")).getAttribute("value");
    String mobile = wd.findElement(By.name("mobile")).getAttribute("value");
    String work = wd.findElement(By.name("work")).getAttribute("value");
    String address = wd.findElement(By.name("address")).getAttribute("value");
    String email = wd.findElement((By.name("email"))).getAttribute("value");
    String email2 = wd.findElement((By.name("email2"))).getAttribute("value");
    String email3 = wd.findElement((By.name("email3"))).getAttribute("value");
    wd.navigate().back();
    return new ContactData().withId(contact.getId()).withFirstname(firstname).withLastname(lastname)
            .withHomePhone(home).withMobilePhone(mobile).withWorkPhone(work).withAllAddresses(address)
            .withEmail(email).withEmail2(email2).withEmail3(email3);
  }

  public ContactData infoFromDetailsForm(ContactData contact) {
    initContactDetailsFormById(contact.getId());
    String allDetails = wd.findElement(By.cssSelector("div#content")).getText();
    String[] details = allDetails.split("\n");
    String firstname = details[0].replaceAll(" .+", "");
    String lastname = details[0].replaceAll(".+ ", "");
    String address = details[1];
    String home = details[3].replaceAll("H: ", "");
    String mobile = details[4].replaceAll("M: ", "");
    String work = details[5].replaceAll("W: ", "");
    String email = details[7].replaceAll(" \\(.+\\)", "");
    String email1 = details[8].replaceAll(" \\(.+\\)", "");
    String email2 = details[9].replaceAll(" \\(.+\\)", "");

    return new ContactData().withId(contact.getId()).withFirstname(firstname).withLastname(lastname)
            .withAllAddresses(address).withHomePhone(home).withMobilePhone(mobile)
            .withWorkPhone(work).withEmail(email).withEmail2(email1).withEmail3(email2);

  }

  public void addToGroup(ContactData contact, String groupName) {
    selectContactById(contact.getId());
    chooseGroup(groupName, "to_group");
    click(By.name("add"));

  }


  public void removeFromGroup(ContactData contact, String groupName) {
    chooseGroup(groupName, "group");
    selectContactById(contact.getId());
    click(By.name("remove"));
  }

  public void chooseGroup(String groupName, String locator) {
    Select dropList = new Select(wd.findElement(By.name(locator)));
    dropList.selectByVisibleText(groupName);
  }
}
