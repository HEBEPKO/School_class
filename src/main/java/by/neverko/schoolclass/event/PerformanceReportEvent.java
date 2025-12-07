package by.neverko.schoolclass.event;


import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PerformanceReportEvent extends ApplicationEvent {
    private final Long studentId;

    public PerformanceReportEvent(Object source, Long studentId) {
        super(source);
        this.studentId = studentId;
    }
}
