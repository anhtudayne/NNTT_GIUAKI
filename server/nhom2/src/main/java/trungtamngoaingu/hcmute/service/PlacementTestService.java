package trungtamngoaingu.hcmute.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trungtamngoaingu.hcmute.entity.PlacementTest;
import trungtamngoaingu.hcmute.repository.PlacementTestRepository;
import java.util.List;
import java.util.Optional;

@Service
public class PlacementTestService {
    @Autowired
    private PlacementTestRepository placementTestRepository;

    public List<PlacementTest> getAllPlacementTests() {
        return placementTestRepository.myGetAll();
    }

    // 1. Lấy thông tin bài test theo ID bằng Stream
    public Optional<PlacementTest> getPlacementTestById(Integer id) {
        return placementTestRepository.myGetAll().stream()
                .filter(t -> t.getTestId().equals(id))
                .findFirst();
    }

    // 2. Tạo mới bài test (Vẫn giữ nguyên phương thức lưu vào DB)
    public PlacementTest createPlacementTest(PlacementTest placementTest) {
        return placementTestRepository.save(placementTest);
    }

    // 3. Cập nhật bài test bằng cách kiểm tra tồn tại qua Stream
    public PlacementTest updatePlacementTest(Integer id, PlacementTest placementTest) {
        boolean exists = placementTestRepository.myGetAll().stream()
                .anyMatch(t -> t.getTestId().equals(id));

        if (exists) {
            placementTest.setTestId(id);
            return placementTestRepository.save(placementTest);
        }
        return null;
    }

    // 4. Xóa bài test dựa trên kết quả lọc của Stream
    public void deletePlacementTest(Integer id) {
        placementTestRepository.myGetAll().stream()
                .filter(t -> t.getTestId().equals(id))
                .findFirst()
                .ifPresent(t -> placementTestRepository.deleteById(t.getTestId()));
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
}
