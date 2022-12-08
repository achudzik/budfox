package me.chudzik.recruitment.vivus.repository;

import me.chudzik.recruitment.vivus.model.Loan;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {

}
