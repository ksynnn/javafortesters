package ru.stqa.pft.addressbook.appmanager;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import ru.stqa.pft.addressbook.model.ContactData;
import ru.stqa.pft.addressbook.model.Contacts;
import ru.stqa.pft.addressbook.model.GroupData;
import ru.stqa.pft.addressbook.model.Groups;

import java.util.List;

/**
 * Created by fedotk on 4/4/2016.
 */
public class DbHelper {

  private final SessionFactory sessionFactory;

  public DbHelper() {
    // A SessionFactory is set up once for an application!
    final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .configure() // configures settings from hibernate.cfg.xml
            .build();

    sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
  }

  public Groups groups() {
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    List<GroupData> result = session.createQuery("from GroupData").list();
    for (GroupData group : result) {
      System.out.println(group);
    }
    session.getTransaction().commit();
    session.close();
    return new Groups(result);
  }

  public Contacts contacts() {
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    java.util.List<ContactData> result = session.createQuery("from ContactData where deprecated = '0000-00-00'").list();
    for (ContactData contact : result) {
      System.out.println(contact);
      System.out.println(contact.getGroups());
    }
    session.getTransaction().commit();
    session.close();
    return new Contacts(result);
  }

  public ContactData getContactById(int id) {
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    ContactData result = (ContactData)session.createQuery(String.format("from ContactData where id = '%s'", id)).list().get(0);
//    for (ContactData contact : result) {
//      System.out.println(contact);
//      System.out.println(contact.getGroups());
//    }
    session.getTransaction().commit();
    session.close();
    return result;
  }


  public ContactData getContactByAddress(String address) {
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    ContactData result = (ContactData)session.createQuery(String.format("from ContactData where address = '%s'", address)).list().get(0);
//    for (ContactData contact : result) {
//      System.out.println(contact);
//      System.out.println(contact.getGroups());
//    }
    session.getTransaction().commit();
    session.close();
    return result;
  }

  public GroupData getGroupByName(String groupName) {
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    GroupData result = (GroupData) session.createQuery(String.format("from GroupData where name = '%s'", groupName)).list().get(0);
    session.getTransaction().commit();
    session.close();
    return result;
  }
}



