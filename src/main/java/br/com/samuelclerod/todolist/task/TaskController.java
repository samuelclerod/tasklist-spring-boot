package br.com.samuelclerod.todolist.task;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.samuelclerod.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

  @Autowired
  private ITaskRepository taskRepository;

  @PostMapping("/")
  public ResponseEntity create(@RequestBody TaskModel task, HttpServletRequest request) {

    if (task == null) {
      return ResponseEntity.badRequest().body("Task is required");
    }

    var currentDate = LocalDateTime.now();
    if (currentDate.isAfter(task.getStartAt())) {
      return ResponseEntity.badRequest().body("StartAt should not be in the past.");
    }
    if (task.getStartAt().isAfter(task.getEndAt())) {
      return ResponseEntity.badRequest().body("StartAt should not be after EndAt.");
    }

    var userId = UUID.fromString(request.getAttribute("userId").toString());
    task.setUserId(userId);

    this.taskRepository.save(task);

    return ResponseEntity.status(HttpStatus.CREATED).body(task);
  }

  @GetMapping("/")
  public ResponseEntity list(HttpServletRequest request) {
    var userId = UUID.fromString(request.getAttribute("userId").toString());
    var tasks = this.taskRepository.findByUserId(userId);
    return ResponseEntity.ok(tasks);
  }

  @PutMapping("/{id}")
  public ResponseEntity update(@RequestBody TaskModel taskModel, @PathVariable UUID id, HttpServletRequest request) {

    UUID currentUserId = UUID.fromString(request.getAttribute("userId").toString());

    var task = this.taskRepository.findById(id).orElse(null);

    if (task == null) {
      return ResponseEntity
          .status(HttpStatus.NOT_FOUND)
          .body("Task not found");
    }

    if (!task.getUserId().equals(currentUserId)) {
      return ResponseEntity
          .status(HttpStatus.FORBIDDEN)
          .body("You don't have permission to update this task");
    }

    Utils.copyNonNullProperties(taskModel, task);

    var taskUpdated = this.taskRepository.save(task);

    return ResponseEntity.ok(taskUpdated);

  }

}
