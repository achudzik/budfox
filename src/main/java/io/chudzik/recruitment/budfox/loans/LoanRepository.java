package io.chudzik.recruitment.budfox.loans;

import org.springframework.data.jpa.repository.JpaRepository;

interface LoanRepository extends JpaRepository<Loan, Long> { }
