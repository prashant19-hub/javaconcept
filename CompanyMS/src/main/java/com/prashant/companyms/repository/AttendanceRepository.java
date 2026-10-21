package com.prashant.companyms.repository;

import com.prashant.companyms.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findAllByOrderByAttendanceDateDescEmployeeNameAsc();

    boolean existsByEmployeeIdAndAttendanceDate(Long employeeId, java.time.LocalDate attendanceDate);

    boolean existsByEmployeeId(Long employeeId);
}
