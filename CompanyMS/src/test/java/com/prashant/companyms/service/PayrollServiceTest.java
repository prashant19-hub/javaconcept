package com.prashant.companyms.service;

import com.prashant.companyms.exception.BusinessRuleException;
import com.prashant.companyms.model.Employee;
import com.prashant.companyms.model.Payroll;
import com.prashant.companyms.repository.EmployeeRepository;
import com.prashant.companyms.repository.PayrollRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PayrollServiceTest {

    @Mock
    private PayrollRepository payrollRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private PayrollService payrollService;

    @Test
    void calculatesNetSalaryAndUsesEmployeeSnapshot() {
        Employee employee = new Employee();
        employee.setId(7L);
        employee.setName("Asha Sharma");
        employee.setDepartment("Engineering");
        when(employeeRepository.findById(7L)).thenReturn(Optional.of(employee));
        when(payrollRepository.existsByEmployeeIdAndMonthIgnoreCaseAndYear(7L, "February", 2026))
                .thenReturn(false);
        when(payrollRepository.save(any(Payroll.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payroll payroll = payroll(7L, "February", 2026, "50000", "5000", "2000");
        Payroll saved = payrollService.savePayroll(payroll);

        assertEquals("Asha Sharma", saved.getEmployeeName());
        assertEquals("Engineering", saved.getDepartment());
        assertEquals(new BigDecimal("53000"), saved.getNetSalary());
        assertEquals("PENDING", saved.getPaymentStatus());
    }

    @Test
    void rejectsDeductionGreaterThanEarnings() {
        Employee employee = new Employee();
        employee.setId(7L);
        when(employeeRepository.findById(7L)).thenReturn(Optional.of(employee));

        Payroll payroll = payroll(7L, "February", 2026, "1000", "0", "1200");

        assertThrows(BusinessRuleException.class, () -> payrollService.savePayroll(payroll));
        verify(payrollRepository, never()).save(any(Payroll.class));
    }

    @Test
    void rejectsDuplicateEmployeeMonthPayroll() {
        Employee employee = new Employee();
        employee.setId(7L);
        when(employeeRepository.findById(7L)).thenReturn(Optional.of(employee));
        when(payrollRepository.existsByEmployeeIdAndMonthIgnoreCaseAndYear(7L, "February", 2026))
                .thenReturn(true);

        Payroll payroll = payroll(7L, "February", 2026, "50000", "0", "0");

        assertThrows(BusinessRuleException.class, () -> payrollService.savePayroll(payroll));
        verify(payrollRepository, never()).save(any(Payroll.class));
    }

    private Payroll payroll(Long employeeId, String month, int year,
                            String basicSalary, String bonus, String deduction) {
        Payroll payroll = new Payroll();
        payroll.setEmployeeId(employeeId);
        payroll.setMonth(month);
        payroll.setYear(year);
        payroll.setBasicSalary(new BigDecimal(basicSalary));
        payroll.setBonus(new BigDecimal(bonus));
        payroll.setDeduction(new BigDecimal(deduction));
        payroll.setPaymentStatus("PENDING");
        return payroll;
    }
}
