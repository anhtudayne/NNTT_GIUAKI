package trungtamngoaingu.hcmute.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trungtamngoaingu.hcmute.entity.Staff;
import trungtamngoaingu.hcmute.repository.StaffRepository;
import java.util.List;
import java.util.Optional;

@Service
public class StaffService {
    @Autowired
    private StaffRepository staffRepository;

    public List<Staff> getAllStaff() {
        return staffRepository.myGetAll();
    }

    public Optional<Staff> getStaffById(Integer id) {
        return staffRepository.findById(id);
    }

    public Staff createStaff(Staff staff) {
        return staffRepository.save(staff);
    }

    public Staff updateStaff(Integer id, Staff staff) {
        if (staffRepository.existsById(id)) {
            staff.setStaffId(id);
            return staffRepository.save(staff);
        }
        return null;
    }

    public void deleteStaff(Integer id) {
        staffRepository.deleteById(id);
    }

    // --- CÁC HÀM XỬ LÝ DỮ LIỆU BẰNG STREAM API ---

    /**
     * TÌM KIẾM NHÂN SỰ THEO TÊN BẰNG YÊU CẦU STREAM API
     * 1. allStaff.stream(): Chuyển đổi List (Collection) sang dòng dữ liệu Stream.
     * 2. .filter(Predicate Lambda): Dùng biểu thức Lambda (staff -> ...) để duyệt qua từng phần tử.
     *     So sánh chuỗi bằng .contains(lowerCaseName) để lấy ra những staff hợp lệ.
     * 3. .toList(): Gom các staff vượt qua bộ lọc thành một ArrayList mới (Hỗ trợ từ Java 16).
     */
    public List<Staff> searchStaffByName(String name) {
        List<Staff> allStaff = staffRepository.myGetAll();
        if (name == null || name.trim().isEmpty()) {
            return allStaff;
        }
        String lowerCaseName = name.toLowerCase();
        return allStaff.stream()
                .filter(staff -> staff.getFullName().toLowerCase().contains(lowerCaseName))
                .toList(); 
    }

    /**
     * LỌC NHÂN SỰ THEO CHỨC VỤ (ROLE) BẰNG STREAM API
     * - Khởi tạo Stream từ danh sách tổng.
     * - .filter() sử dụng phép so sánh Enum (staff.getRole() == targetRole)
     * - .toList() đóng băng stream và kết xuất danh sách cuối cùng báo cáo về cho Client.
     */
    public List<Staff> getStaffByRole(String roleStr) {
        List<Staff> allStaff = staffRepository.myGetAll();
        try {
            Staff.Role targetRole = Staff.Role.valueOf(roleStr);
            return allStaff.stream()
                    .filter(staff -> staff.getRole() == targetRole)
                    .toList();
        } catch (IllegalArgumentException e) {
            return List.of(); // Trả về list rỗng nếu Role không hợp lệ
        }
    }
}
