package ru.startupbase.service;

import javax.inject.Inject;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;
import ru.startupbase.entity.Example;

public class ExampleService {
  private final SessionFactory sessionFactory;

  @Inject
  public ExampleService(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Transactional
  public Example getRecordById(Integer id) {
    return sessionFactory.getCurrentSession().get(Example.class, id);
  }
}
