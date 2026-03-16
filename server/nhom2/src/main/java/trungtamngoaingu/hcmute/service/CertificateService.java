package trungtamngoaingu.hcmute.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<Certificate> getCertificatesPaged(Pageable pageable) {
        return certificateRepository.findAll(pageable);
    }

    public Optional<Certificate> getCertificateById(Integer id) {
        return certificateRepository.findById(id);
    }

    // 2. Tạo mới chứng chỉ
    public Certificate createCertificate(Certificate certificate) {
        return certificateRepository.save(certificate);
    }

    public Certificate updateCertificate(Integer id, Certificate certificate) {
        if (certificateRepository.existsById(id)) {
            certificate.setCertificateId(id);
            return certificateRepository.save(certificate);
        }
        return null;
    }

    public void deleteCertificate(Integer id) {
        certificateRepository.deleteById(id);
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
