package trungtamngoaingu.hcmute.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import trungtamngoaingu.hcmute.entity.UserAccount;
import java.util.List;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Integer> {
    @Query("SELECT u FROM UserAccount u")
    List<UserAccount> myGetAll();

    // Tìm kiếm tài khoản dựa vào Username (Tài khoản phải Active)
    @Query("SELECT u FROM UserAccount u WHERE u.username = :username AND u.status = 'Active'")
    java.util.Optional<UserAccount> findByUsername(@org.springframework.data.repository.query.Param("username") String username);
}
