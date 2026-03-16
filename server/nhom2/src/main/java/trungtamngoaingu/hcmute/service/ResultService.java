package trungtamngoaingu.hcmute.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<Result> getResultsPaged(Pageable pageable) {
        return resultRepository.findAll(pageable);
    }

    public Optional<Result> getResultById(Integer id) {
        return resultRepository.findById(id);
    }

    // 2. Tạo mới kết quả (vẫn lưu xuống DB qua repository)
    public Result createResult(Result result) {
        return resultRepository.save(result);
    }

    public Result updateResult(Integer id, Result result) {
        if (resultRepository.existsById(id)) {
            result.setResultId(id);
            return resultRepository.save(result);
        }
        return null;
    }

    public void deleteResult(Integer id) {
        resultRepository.deleteById(id);
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
