package me.chudzik.recruitment.vivus.repository;

import me.chudzik.recruitment.vivus.model.Client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClientRepository extends JpaRepository<Client, Long> {

    @Query("SELECT c FROM Client c JOIN FETCH c.loans WHERE c.id = (:id)")
    Client getClientLoans(@Param("id") Long id);
    Client findByLoansId(Long id);
}
