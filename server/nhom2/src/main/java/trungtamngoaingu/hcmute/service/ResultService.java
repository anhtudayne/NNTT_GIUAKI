package trungtamngoaingu.hcmute.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trungtamngoaingu.hcmute.entity.Result;
import trungtamngoaingu.hcmute.repository.ResultRepository;
import java.util.List;
import java.util.Optional;

@Service
public class ResultService {
    @Autowired
    private ResultRepository resultRepository;

    public List<Result> getAllResults() {
        return resultRepository.myGetAll();
    }

    // 1. Lấy kết quả theo ID bằng Stream
    public Optional<Result> getResultById(Integer id) {
        return resultRepository.myGetAll().stream()
                .filter(r -> r.getResultId().equals(id))
                .findFirst();
    }

    // 2. Tạo mới kết quả (vẫn lưu xuống DB qua repository)
    public Result createResult(Result result) {
        return resultRepository.save(result);
    }

    // 3. Cập nhật kết quả bằng cách kiểm tra tồn tại qua Stream
    public Result updateResult(Integer id, Result result) {
        boolean exists = resultRepository.myGetAll().stream()
                .anyMatch(r -> r.getResultId().equals(id));

        if (exists) {
            result.setResultId(id);
            return resultRepository.save(result);
        }
        return null;
    }

    // 4. Xóa kết quả dựa trên Stream filter
    public void deleteResult(Integer id) {
        resultRepository.myGetAll().stream()
                .filter(r -> r.getResultId().equals(id))
                .findFirst()
                .ifPresent(r -> resultRepository.deleteById(r.getResultId()));
    }

    // public Optional<Result> getResultById(Integer id) {
    //     return resultRepository.findById(id);
    // }

    // public Result createResult(Result result) {
    //     return resultRepository.save(result);
    // }

    // public Result updateResult(Integer id, Result result) {
    //     if (resultRepository.existsById(id)) {
    //         result.setResultId(id);
    //         return resultRepository.save(result);
    //     }
    //     return null;
    // }

    // public void deleteResult(Integer id) {
    //     resultRepository.deleteById(id);
    // }
}
