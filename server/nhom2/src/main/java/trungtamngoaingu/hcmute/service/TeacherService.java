package trungtamngoaingu.hcmute.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trungtamngoaingu.hcmute.entity.Teacher;
import trungtamngoaingu.hcmute.repository.TeacherRepository;
import java.util.List;
import java.util.Optional;

@Service
public class TeacherService {
    @Autowired
    private TeacherRepository teacherRepository;

    public List<Teacher> getAllTeachers() {
        return teacherRepository.myGetAll();
    }

    public Optional<Teacher> getTeacherById(Integer id) {
        return teacherRepository.findById(id);
    }

    public Teacher createTeacher(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    public Teacher updateTeacher(Integer id, Teacher teacher) {
        if (teacherRepository.existsById(id)) {
            teacher.setTeacherId(id);
            return teacherRepository.save(teacher);
        }
        return null;
    }

    public void deleteTeacher(Integer id) {
        teacherRepository.deleteById(id);
    }
}
