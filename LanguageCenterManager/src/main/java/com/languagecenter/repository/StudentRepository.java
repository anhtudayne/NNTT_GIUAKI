package com.languagecenter.repository;

import com.languagecenter.model.Student;
import com.languagecenter.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class StudentRepository {

    // Lấy tất cả student
    public List<Student> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // JPQL query
            return em.createQuery("SELECT s FROM Student s", Student.class).getResultList();
        } finally {
            em.close();
        }
    }

    // Thêm mới student
    public void save(Student student) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(student);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // Khởi tạo một vài dữ liệu mẫu nếu DB trống
    public void initSampleData() {
        if (findAll().isEmpty()) {
            save(new Student("Nguyen Van A", "nva@mail.com", "Active"));
            save(new Student("Tran Thi B", "ttb@mail.com", "Active"));
            save(new Student("Le Van C", "lvc@mail.com", "Inactive"));
        }
    }
}
