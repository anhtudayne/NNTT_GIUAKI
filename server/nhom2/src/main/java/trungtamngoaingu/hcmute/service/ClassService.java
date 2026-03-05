package trungtamngoaingu.hcmute.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trungtamngoaingu.hcmute.entity.Class;
import trungtamngoaingu.hcmute.repository.ClassRepository;
import java.util.List;
import java.util.Optional;

@Service
public class ClassService {
    @Autowired
    private ClassRepository classRepository;

    public List<Class> getAllClasses() {
        return classRepository.myGetAll();
    }

    public Optional<Class> getClassById(Integer id) {
        return classRepository.findById(id);
    }

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
}
