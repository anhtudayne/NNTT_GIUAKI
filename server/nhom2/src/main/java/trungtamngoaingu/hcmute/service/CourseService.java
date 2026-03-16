package trungtamngoaingu.hcmute.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import trungtamngoaingu.hcmute.entity.Course;
import trungtamngoaingu.hcmute.repository.CourseRepository;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    public List<Course> getAllCourses() {
        return courseRepository.myGetAll();
    }

    public Page<Course> getCoursesPaged(Pageable pageable) {
        return courseRepository.findAll(pageable);
    }

    public Optional<Course> getCourseById(Integer id) {
        return courseRepository.findById(id);
    }

    // Tạo mới khóa học (vẫn dùng save để lưu xuống DB)
    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    public Course updateCourse(Integer id, Course course) {
        if (courseRepository.existsById(id)) {
            course.setCourseId(id);
            return courseRepository.save(course);
        }
        return null;
    }

    public void deleteCourse(Integer id) {
        courseRepository.deleteById(id);
    }

    // public Optional<Course> getCourseById(Integer id) {
    //     return courseRepository.findById(id);
    // }

    // public Course createCourse(Course course) {
    //     return courseRepository.save(course);
    // }

    // public Course updateCourse(Integer id, Course course) {
    //     if (courseRepository.existsById(id)) {
    //         course.setCourseId(id);
    //         return courseRepository.save(course);
    //     }
    //     return null;
    // }

    // public void deleteCourse(Integer id) {
    //     courseRepository.deleteById(id);
    // }
}
