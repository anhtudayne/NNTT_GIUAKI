package trungtamngoaingu.hcmute.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trungtamngoaingu.hcmute.entity.Attendance;
import trungtamngoaingu.hcmute.repository.AttendanceRepository;
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
        // Lấy toàn bộ giao dịch Ghi danh
        List<trungtamngoaingu.hcmute.entity.Enrollment> allEnrollments = enrollmentRepository.findAll();
        
        // Dùng Stream API lọc ra những em thuộc Lớp đó, rồi trích xuất nguyên cụm Đối tượng Student
        return allEnrollments.stream()
                .filter(e -> e.getClassEntity() != null && e.getClassEntity().getClassId().equals(classId))
                .filter(e -> e.getStatus() == trungtamngoaingu.hcmute.entity.Enrollment.Status.Studying || e.getStatus() == trungtamngoaingu.hcmute.entity.Enrollment.Status.Registered)
                .map(trungtamngoaingu.hcmute.entity.Enrollment::getStudent)
                .distinct() // Lọc trùng lặp phòng hờ
                .collect(java.util.stream.Collectors.toList());
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
