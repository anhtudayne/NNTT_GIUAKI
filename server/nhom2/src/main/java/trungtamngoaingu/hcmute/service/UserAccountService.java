package trungtamngoaingu.hcmute.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trungtamngoaingu.hcmute.entity.UserAccount;
import trungtamngoaingu.hcmute.repository.UserAccountRepository;
import java.util.List;
import java.util.Optional;

@Service
public class UserAccountService {
    @Autowired
    private UserAccountRepository userAccountRepository;

    public List<UserAccount> getAllUserAccounts() {
        return userAccountRepository.myGetAll();
    }

    public Optional<UserAccount> getUserAccountById(Integer id) {
        return userAccountRepository.findById(id);
    }

    public UserAccount createUserAccount(UserAccount userAccount) {
        return userAccountRepository.save(userAccount);
    }

    public UserAccount updateUserAccount(Integer id, UserAccount userAccount) {
        if (userAccountRepository.existsById(id)) {
            userAccount.setUserId(id);
            return userAccountRepository.save(userAccount);
        }
        return null;
    }

    public void deleteUserAccount(Integer id) {
        userAccountRepository.deleteById(id);
    }
}
