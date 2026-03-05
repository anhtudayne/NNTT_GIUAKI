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

    public Optional<PlacementTest> getPlacementTestById(Integer id) {
        return placementTestRepository.findById(id);
    }

    public PlacementTest createPlacementTest(PlacementTest placementTest) {
        return placementTestRepository.save(placementTest);
    }

    public PlacementTest updatePlacementTest(Integer id, PlacementTest placementTest) {
        if (placementTestRepository.existsById(id)) {
            placementTest.setTestId(id);
            return placementTestRepository.save(placementTest);
        }
        return null;
    }

    public void deletePlacementTest(Integer id) {
        placementTestRepository.deleteById(id);
    }
}
