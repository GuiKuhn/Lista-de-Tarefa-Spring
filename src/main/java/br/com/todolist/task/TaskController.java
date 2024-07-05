package br.com.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/tasks")
public class TaskController {
    
    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request){
        var userId = (UUID) request.getAttribute("userId");
        taskModel.setUserId(userId);
        var now = LocalDateTime.now();
        if (now.isAfter(taskModel.getStartAt()) || now.isAfter(taskModel.getEndAt())) {
            return ResponseEntity.badRequest().body("A data de início/fim não pode ser menor que a data atual");
        }
        if (taskModel.getEndAt().isBefore(taskModel.getStartAt())) {
            return ResponseEntity.badRequest().body("A data de fim não pode ser menor que a data de início");
        }
        
        var task = this.taskRepository.save(taskModel); 
        return ResponseEntity.ok(task);
    }

    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request){
        var userId = (UUID) request.getAttribute("userId");
        var tasks = this.taskRepository.findByUserId(userId);
        return tasks;
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable UUID id, @RequestBody TaskModel taskModel, HttpServletRequest request){
        var task = this.taskRepository.findById(id).orElse(null);

        if (task == null) {
            return ResponseEntity.notFound().build();
        }
        if(!task.getUserId().equals((UUID) request.getAttribute("userId"))){
            return ResponseEntity.status(403).body("Você não tem permissão para alterar essa tarefa");
        }
        
        Utils.copyProperties(taskModel, task);
        return ResponseEntity.ok(this.taskRepository.save(task));
    }
}
