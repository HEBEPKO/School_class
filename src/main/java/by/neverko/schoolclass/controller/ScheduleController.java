package by.neverko.schoolclass.controller;

import by.neverko.schoolclass.entity.Schedule;
import by.neverko.schoolclass.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<List<Schedule>> getSchedule(
            @RequestParam Long classId,
            @RequestHeader("X-User-Id") Long currentUserId
    ) {
        return ResponseEntity.ok(scheduleService.getScheduleForClass(classId));
    }

    @PostMapping
    public ResponseEntity<Schedule> createSchedule(
            @RequestParam Schedule schedule,
            @RequestHeader("X-User-Id") Long currentUserId
    ) {
        Schedule saved = scheduleService.createOrUpdateSchedule(currentUserId, schedule);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Schedule> updateSchedule(
            @PathVariable Long id,
            @RequestBody Schedule schedule,
            @RequestHeader("X-User_Id") Long currentUserId
    ) {
        schedule.setId(id);
        Schedule updated = scheduleService.createOrUpdateSchedule(currentUserId, schedule);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Schedule> deletedSchedule(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long currentUserId
    ) {
        scheduleService.deleteSchedule(currentUserId, id);
        return ResponseEntity.noContent().build();
    }

}
