package br.com.todolist.task;

import java.util.UUID;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITaskRepository extends JpaRepository<TaskModel, UUID>{
    public List<TaskModel> findByUserId(UUID userId);
}
