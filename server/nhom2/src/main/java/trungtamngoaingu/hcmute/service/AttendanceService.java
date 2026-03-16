package trungtamngoaingu.hcmute.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trungtamngoaingu.hcmute.entity.Attendance;
import trungtamngoaingu.hcmute.entity.Enrollment;
import trungtamngoaingu.hcmute.repository.AttendanceRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {
    @Autowired
    private AttendanceRepository attendanceRepository;

    public List<Attendance> getAllAttendances() {
        return attendanceRepository.myGetAll();
    }

    public Optional<Attendance> getAttendanceById(Integer id) {
        return attendanceRepository.findById(id);
    }

    public Attendance createAttendance(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    public Attendance updateAttendance(Integer id, Attendance attendance) {
        if (attendanceRepository.existsById(id)) {
            attendance.setAttendanceId(id);
            return attendanceRepository.save(attendance);
        }
        return null;
    }

    public void deleteAttendance(Integer id) {
        attendanceRepository.deleteById(id);
    }
    
    @Autowired
    private trungtamngoaingu.hcmute.repository.EnrollmentRepository enrollmentRepository;

    // LẤY DANH SÁCH HỌC VIÊN CỦA 1 LỚP BẰNG STREAM API
    public List<trungtamngoaingu.hcmute.entity.Student> getStudentsByClassId(Integer classId) {
        // Dùng query có điều kiện ở database thay vì quét toàn bộ bảng Enrollment
        List<Enrollment.Status> statuses = Arrays.asList(
                Enrollment.Status.Studying,
                Enrollment.Status.Registered
        );

        List<Enrollment> enrollments = enrollmentRepository
                .findByClassEntity_ClassIdAndStatusIn(classId, statuses);

        return enrollments.stream()
                .map(Enrollment::getStudent)
                .distinct() // Lọc trùng phòng hờ
                .collect(java.util.stream.Collectors.toList());
    }

    // LẤY DANH SÁCH ĐIỂM DANH CỦA 1 LỚP TRONG 1 NGÀY BẰNG STREAM API
    public List<Attendance> getAttendancesByClassIdAndDate(Integer classId, String date) {
        // Lọc trực tiếp trong database theo classId + date (LocalDate)
        LocalDate targetDate = LocalDate.parse(date);
        return attendanceRepository.findByClassEntity_ClassIdAndDate(classId, targetDate);
    }

    // LƯU HÀNG LOẠT (BATCH SAVE) DANH SÁCH ĐIỂM DANH
    public List<Attendance> saveBatchAttendances(List<Attendance> attendances) {
        // Dùng Stream kiểm qua xem có record rỗng không trước khi lưu
        List<Attendance> validAttendances = attendances.stream()
                .filter(a -> a.getStudent() != null && a.getClassEntity() != null && a.getStatus() != null)
                .collect(java.util.stream.Collectors.toList());
                
        // JPA hỗ trợ lưu hàng loạt vào Database tốc độ cao
        return attendanceRepository.saveAll(validAttendances);
    }
}
