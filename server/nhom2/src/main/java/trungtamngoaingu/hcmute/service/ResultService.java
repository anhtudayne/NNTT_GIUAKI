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

    public Optional<Result> getResultById(Integer id) {
        return resultRepository.findById(id);
    }

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
}
