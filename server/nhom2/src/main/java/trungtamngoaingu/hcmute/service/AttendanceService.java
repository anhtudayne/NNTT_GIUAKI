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
}
