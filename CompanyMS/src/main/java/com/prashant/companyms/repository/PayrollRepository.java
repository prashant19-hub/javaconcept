package com.prashant.companyms.repository;

import com.prashant.companyms.model.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {

    List<Payroll> findAllByOrderByYearDescMonthAsc();

    boolean existsByEmployeeIdAndMonthIgnoreCaseAndYear(Long employeeId, String month, Integer year);

    boolean existsByEmployeeId(Long employeeId);
}