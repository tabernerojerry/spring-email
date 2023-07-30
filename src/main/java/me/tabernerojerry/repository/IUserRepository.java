package me.tabernerojerry.repository;

import me.tabernerojerry.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {

    User findByEmailIgnoreCase(String email);

    Boolean existsByEmail(String email);

}
