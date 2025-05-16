package id.ac.ui.cs.advprog.manajemen_iklan.repository;

import id.ac.ui.cs.advprog.manajemen_iklan.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserModel, String> {
    Optional<UserModel> findByUsername(String username);
}