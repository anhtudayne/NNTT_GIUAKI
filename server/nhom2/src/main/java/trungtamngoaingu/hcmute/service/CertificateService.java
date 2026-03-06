package trungtamngoaingu.hcmute.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trungtamngoaingu.hcmute.entity.Certificate;
import trungtamngoaingu.hcmute.repository.CertificateRepository;
import java.util.List;
import java.util.Optional;

@Service
public class CertificateService {
    @Autowired
    private CertificateRepository certificateRepository;

    public List<Certificate> getAllCertificates() {
        return certificateRepository.myGetAll();
    }

    // 1. Lấy chứng chỉ theo ID bằng Stream
    public Optional<Certificate> getCertificateById(Integer id) {
        return certificateRepository.myGetAll().stream()
                .filter(c -> c.getCertificateId().equals(id))
                .findFirst();
    }

    // 2. Tạo mới chứng chỉ
    public Certificate createCertificate(Certificate certificate) {
        return certificateRepository.save(certificate);
    }

    // 3. Cập nhật chứng chỉ bằng cách kiểm tra tồn tại qua anyMatch
    public Certificate updateCertificate(Integer id, Certificate certificate) {
        boolean exists = certificateRepository.myGetAll().stream()
                .anyMatch(c -> c.getCertificateId().equals(id));

        if (exists) {
            certificate.setCertificateId(id);
            return certificateRepository.save(certificate);
        }
        return null;
    }

    // 4. Xóa chứng chỉ dựa trên kết quả lọc của Stream
    public void deleteCertificate(Integer id) {
        certificateRepository.myGetAll().stream()
                .filter(c -> c.getCertificateId().equals(id))
                .findFirst()
                .ifPresent(c -> certificateRepository.deleteById(c.getCertificateId()));
    }

    // public Optional<Certificate> getCertificateById(Integer id) {
    //     return certificateRepository.findById(id);
    // }

    // public Certificate createCertificate(Certificate certificate) {
    //     return certificateRepository.save(certificate);
    // }

    // public Certificate updateCertificate(Integer id, Certificate certificate) {
    //     if (certificateRepository.existsById(id)) {
    //         certificate.setCertificateId(id);
    //         return certificateRepository.save(certificate);
    //     }
    //     return null;
    // }

    // public void deleteCertificate(Integer id) {
    //     certificateRepository.deleteById(id);
    // }
}
