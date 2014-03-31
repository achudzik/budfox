package me.chudzik.recruitment.vivus.repository;

import me.chudzik.recruitment.vivus.model.Client;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {

}
