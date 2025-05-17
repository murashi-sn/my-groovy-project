package my.example

import jakarta.persistence.*
import my.example.db.entity.Employee

import java.time.Instant

println "Hello Groovy"

try (def factory = Persistence.createEntityManagerFactory("my-app")) {
    try (EntityManager em = factory.createEntityManager()) {
        String jpql = "SELECT p FROM Employee p";
        def employees = em.createQuery(jpql, Employee.class).getResultList()
        employees.forEach { println it.name }
        def newEmployee = new Employee()
        newEmployee.name = "employee" + Instant.now().toEpochMilli()
        def transaction = em.getTransaction();
        transaction.begin();
        em.persist(newEmployee);
        transaction.commit();
    }
}