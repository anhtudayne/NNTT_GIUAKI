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
}
