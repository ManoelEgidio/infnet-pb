package br.com.manoelegidio.tp5.taskmanager.repository;

import br.com.manoelegidio.tp5.taskmanager.domain.model.ActionType;
import br.com.manoelegidio.tp5.taskmanager.domain.model.TaskHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskHistoryRepository extends JpaRepository<TaskHistory, Long> {

    List<TaskHistory> findByTaskIdOrderByChangedAtDesc(Long taskId);

    List<TaskHistory> findByActionTypeOrderByChangedAtDesc(ActionType actionType);

    @Query("SELECT h FROM TaskHistory h WHERE h.taskId = :taskId AND h.fieldChanged = :field ORDER BY h.changedAt DESC")
    List<TaskHistory> findFieldHistoryByTaskId(@Param("taskId") Long taskId, @Param("field") String field);

    long countByTaskId(Long taskId);
}
