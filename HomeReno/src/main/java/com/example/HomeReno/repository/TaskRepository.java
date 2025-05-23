package com.example.HomeReno.repository;

import com.example.HomeReno.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

}
