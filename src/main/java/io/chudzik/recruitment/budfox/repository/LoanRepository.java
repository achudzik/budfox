package io.chudzik.recruitment.budfox.repository;

import io.chudzik.recruitment.budfox.model.Loan;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {

}
