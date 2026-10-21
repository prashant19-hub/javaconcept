package com.prashant.companyms.repository;

import com.prashant.companyms.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByNameContainingIgnoreCaseOrDepartmentContainingIgnoreCaseOrderByNameAsc(
            String name, String department);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    long countByStatusIgnoreCase(String status);
}