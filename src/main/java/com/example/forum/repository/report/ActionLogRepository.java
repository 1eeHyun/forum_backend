package com.example.forum.repository.report;

import com.example.forum.model.report.ReportActionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionLogRepository extends JpaRepository<ReportActionLog, Long> {
}
