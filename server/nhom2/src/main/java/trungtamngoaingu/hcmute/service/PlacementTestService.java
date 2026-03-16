package trungtamngoaingu.hcmute.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trungtamngoaingu.hcmute.dto.PlacementTestRecommendationDTO;
import trungtamngoaingu.hcmute.entity.Class;
import trungtamngoaingu.hcmute.entity.Course;
import trungtamngoaingu.hcmute.entity.Enrollment;
import trungtamngoaingu.hcmute.entity.PlacementTest;
import trungtamngoaingu.hcmute.repository.ClassRepository;
import trungtamngoaingu.hcmute.repository.CourseRepository;
import trungtamngoaingu.hcmute.repository.EnrollmentRepository;
import trungtamngoaingu.hcmute.repository.PlacementTestRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlacementTestService {
    @Autowired
    private PlacementTestRepository placementTestRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    public List<PlacementTest> getAllPlacementTests() {
        return placementTestRepository.myGetAll();
    }

    public Optional<PlacementTest> getPlacementTestById(Integer id) {
        return placementTestRepository.findById(id);
    }

    // 2. Tạo mới bài test (Vẫn giữ nguyên phương thức lưu vào DB)
    public PlacementTest createPlacementTest(PlacementTest placementTest) {
        // Nếu chưa có recommendedLevel nhưng có score, tự tính
        if (placementTest.getRecommendedLevel() == null && placementTest.getScore() != null) {
            placementTest.setRecommendedLevel(computeRecommendedLevel(placementTest.getScore()));
        }
        return placementTestRepository.save(placementTest);
    }

    public PlacementTest updatePlacementTest(Integer id, PlacementTest placementTest) {
        if (placementTestRepository.existsById(id)) {
            placementTest.setTestId(id);
            if (placementTest.getRecommendedLevel() == null && placementTest.getScore() != null) {
                placementTest.setRecommendedLevel(computeRecommendedLevel(placementTest.getScore()));
            }
            return placementTestRepository.save(placementTest);
        }
        return null;
    }

    public void deletePlacementTest(Integer id) {
        placementTestRepository.deleteById(id);
    }

    // public Optional<PlacementTest> getPlacementTestById(Integer id) {
    //     return placementTestRepository.findById(id);
    // }

    // public PlacementTest createPlacementTest(PlacementTest placementTest) {
    //     return placementTestRepository.save(placementTest);
    // }

    // public PlacementTest updatePlacementTest(Integer id, PlacementTest placementTest) {
    //     if (placementTestRepository.existsById(id)) {
    //         placementTest.setTestId(id);
    //         return placementTestRepository.save(placementTest);
    //     }
    //     return null;
    // }

    // public void deletePlacementTest(Integer id) {
    //     placementTestRepository.deleteById(id);
    // }

    /**
     * Gợi ý khóa học/lớp dựa trên PlacementTest.
     * - Dùng recommendedLevel nếu đã có; nếu chưa thì tính từ score.
     * - Course: cùng level, status = Active.
     * - Class: thuộc các course đó, status = Pending hoặc Ongoing.
     */
    public Optional<PlacementTestRecommendationDTO> getRecommendations(Integer testId) {
        Optional<PlacementTest> opt = getPlacementTestById(testId);
        if (opt.isEmpty()) return Optional.empty();
        PlacementTest test = opt.get();

        String level = test.getRecommendedLevel();
        if (level == null && test.getScore() != null) {
            level = computeRecommendedLevel(test.getScore());
            test.setRecommendedLevel(level);
            placementTestRepository.save(test);
        }
        if (level == null) {
            return Optional.of(new PlacementTestRecommendationDTO(test, null, List.of(), List.of()));
        }

        Course.Level courseLevel = mapToCourseLevel(level);
        if (courseLevel == null) {
            return Optional.of(new PlacementTestRecommendationDTO(test, level, List.of(), List.of()));
        }

        // Đẩy filter xuống database: chỉ lấy các course Active theo level
        List<Course> suggestedCourses = courseRepository.findByStatusAndLevel(Course.Status.Active, courseLevel);

        List<Integer> courseIds = suggestedCourses.stream()
                .map(Course::getCourseId)
                .toList();

        // Đẩy filter xuống database: chỉ lấy class thuộc courseIds và status phù hợp
        List<Class> suggestedClasses = courseIds.isEmpty()
                ? List.of()
                : classRepository.findByCourse_CourseIdInAndStatusIn(
                        courseIds,
                        List.of(Class.Status.Pending, Class.Status.Ongoing)
                );

        return Optional.of(new PlacementTestRecommendationDTO(test, level, suggestedCourses, suggestedClasses));
    }

    /**
     * Ghi danh học viên (từ PlacementTest) vào một lớp.
     * - Tạo Enrollment mới: student lấy từ test, class theo classId, ngày hôm nay, status Registered, result Pending.
     */
    public Optional<Enrollment> enrollFromPlacementTest(Integer testId, Integer classId) {
        if (testId == null || classId == null) return Optional.empty();
        Optional<PlacementTest> optTest = getPlacementTestById(testId);
        if (optTest.isEmpty() || optTest.get().getStudent() == null) return Optional.empty();

        Optional<Class> optClass = classRepository.findById(classId);
        if (optClass.isEmpty()) return Optional.empty();

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(optTest.get().getStudent());
        enrollment.setClassEntity(optClass.get());
        enrollment.setEnrollmentDate(LocalDate.now());
        enrollment.setStatus(Enrollment.Status.Registered);
        enrollment.setResult(Enrollment.Result.Pending);

        Enrollment saved = enrollmentRepository.save(enrollment);
        return Optional.of(saved);
    }

    private String computeRecommendedLevel(BigDecimal score) {
        if (score == null) return null;
        double s = score.doubleValue();
        if (s < 4.0) return "Beginner";
        if (s < 6.0) return "Intermediate";
        return "Advanced";
    }

    private Course.Level mapToCourseLevel(String recommendedLevel) {
        if (recommendedLevel == null) return null;
        return switch (recommendedLevel) {
            case "Beginner" -> Course.Level.Beginner;
            case "Intermediate" -> Course.Level.Intermediate;
            case "Advanced" -> Course.Level.Advanced;
            default -> null;
        };
    }
}
