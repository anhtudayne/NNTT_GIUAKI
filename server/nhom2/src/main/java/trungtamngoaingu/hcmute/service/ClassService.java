package trungtamngoaingu.hcmute.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import trungtamngoaingu.hcmute.entity.Class;
import trungtamngoaingu.hcmute.repository.ClassRepository;
import trungtamngoaingu.hcmute.repository.EnrollmentRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClassService {
    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    public List<Class> getAllClasses() {
        return classRepository.myGetAll();
    }

    public Page<Class> getClassesPaged(Pageable pageable) {
        return classRepository.findAll(pageable);
    }

    public Optional<Class> getClassById(Integer id) {
        return classRepository.findByClassId(id);
    }

    // 2. Tạo mới lớp học (vẫn dùng phương thức save của Repository)
    public Class createClass(Class clazz) {
        return classRepository.save(clazz);
    }

    public Class updateClass(Integer id, Class clazz) {
        if (classRepository.existsById(id)) {
            clazz.setClassId(id);
            return classRepository.save(clazz);
        }
        return null;
    }

    public void deleteClass(Integer id) {
        classRepository.deleteById(id);
    }

    public List<Class> searchClassesByName(String name) {
        return classRepository.searchByName(name);
    }

    public List<Class> getClassesByStudentId(Integer studentId) {
        return enrollmentRepository.findByStudent_StudentId(studentId).stream()
                .map(enrollment -> enrollment.getClassEntity())
                .collect(Collectors.toList());
    }

    public List<Class> getClassesByTeacherId(Integer teacherId) {
        return classRepository.findByTeacher_TeacherId(teacherId);
    }
}
