package by.neverko.schoolclass.service;

import by.neverko.schoolclass.entity.Schedule;
import by.neverko.schoolclass.repository.ScheduleRepository;
import by.neverko.schoolclass.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final SecurityUtils securityUtils;

    public List<Schedule> getScheduleForClass(Long classId) {
        return scheduleRepository.findByClassEntityIdOrderByDayOfWeekAscLessonNumberAsc(classId);
    }

    @Transactional
    public Schedule createOrUpdateSchedule(Long currentUserId, Schedule schedule) {
        Long classId = schedule.getClassEntity().getId();
        securityUtils.validateIsClassTeacher(currentUserId, classId);
        return scheduleRepository.save(schedule);
    }

    @Transactional
    public void deleteSchedule(Long currentId, Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Расписание не найдено"));
        securityUtils.validateIsClassTeacher(currentId, schedule.getClassEntity().getId());
        scheduleRepository.deleteById(scheduleId);
    }
}
