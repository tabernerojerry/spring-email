package me.tabernerojerry.repository;

import me.tabernerojerry.domain.Confirmation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IConfirmationRepository extends JpaRepository<Confirmation, Long> {

    Confirmation findByToken(String token);

}
