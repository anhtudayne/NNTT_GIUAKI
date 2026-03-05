package trungtamngoaingu.hcmute.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trungtamngoaingu.hcmute.entity.Branch;
import trungtamngoaingu.hcmute.repository.BranchRepository;
import java.util.List;
import java.util.Optional;

@Service
public class BranchService {
    @Autowired
    private BranchRepository branchRepository;

    public List<Branch> getAllBranches() {
        return branchRepository.myGetAll();
    }

    public Optional<Branch> getBranchById(Integer id) {
        return branchRepository.findById(id);
    }

    public Branch createBranch(Branch branch) {
        return branchRepository.save(branch);
    }

    public Branch updateBranch(Integer id, Branch branch) {
        if (branchRepository.existsById(id)) {
            branch.setBranchId(id);
            return branchRepository.save(branch);
        }
        return null;
    }

    public void deleteBranch(Integer id) {
        branchRepository.deleteById(id);
    }
}
