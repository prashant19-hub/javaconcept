package com.prashant.companyms.service;

import com.prashant.companyms.exception.BusinessRuleException;
import com.prashant.companyms.exception.ResourceNotFoundException;
import com.prashant.companyms.model.Attendance;
import com.prashant.companyms.model.Employee;
import com.prashant.companyms.repository.AttendanceRepository;
import com.prashant.companyms.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    public AttendanceService(AttendanceRepository attendanceRepository,
                             EmployeeRepository employeeRepository) {
        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional(readOnly = true)
    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAllByOrderByAttendanceDateDescEmployeeNameAsc();
    }

    public Attendance saveAttendance(Attendance attendance) {
        Employee employee = employeeRepository.findById(attendance.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with id: " + attendance.getEmployeeId()));
        if (attendanceRepository.existsByEmployeeIdAndAttendanceDate(
                attendance.getEmployeeId(), attendance.getAttendanceDate())) {
            throw new BusinessRuleException("Attendance for this employee and date already exists");
        }
        attendance.setEmployeeName(employee.getName());
        attendance.setStatus(attendance.getStatus() == null || attendance.getStatus().isBlank()
                ? "PRESENT" : attendance.getStatus().toUpperCase());
        return attendanceRepository.save(attendance);
    }

    public void deleteAttendance(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found with id: " + id));
        attendanceRepository.delete(attendance);
    }
}
