package br.com.manoelegidio.tp3.taskmanager.repository;

import br.com.manoelegidio.tp3.taskmanager.domain.model.Priority;
import br.com.manoelegidio.tp3.taskmanager.domain.model.Task;
import br.com.manoelegidio.tp3.taskmanager.domain.model.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByStatus(TaskStatus status);

    List<Task> findByPriority(Priority priority);

    List<Task> findByCategoryId(Long categoryId);

    @Query("SELECT t FROM Task t WHERE " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:priority IS NULL OR t.priority = :priority) AND " +
           "(:categoryId IS NULL OR t.category.id = :categoryId)")
    List<Task> findByFilters(
            @Param("status") TaskStatus status,
            @Param("priority") Priority priority,
            @Param("categoryId") Long categoryId);

    @Query("SELECT t FROM Task t WHERE " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:priority IS NULL OR t.priority = :priority) AND " +
           "(:categoryId IS NULL OR t.category.id = :categoryId)")
    Page<Task> findByFiltersPaged(
            @Param("status") TaskStatus status,
            @Param("priority") Priority priority,
            @Param("categoryId") Long categoryId,
            Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.dueDate IS NOT NULL AND t.dueDate < :now AND t.status <> 'COMPLETED'")
    List<Task> findOverdueTasks(@Param("now") LocalDateTime now);

    long countByStatus(TaskStatus status);

    long countByPriority(Priority priority);

    long countByCategoryId(Long categoryId);
}
