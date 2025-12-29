package by.neverko.schoolclass.mapper;

import by.neverko.schoolclass.dto.GradeDto;
import by.neverko.schoolclass.entity.Grade;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GradeMapper {

    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "subject.id", target = "subjectId")
    @Mapping(source = "teacher.id", target = "teacherId")
    GradeDto toDtoBase(Grade grade);

    default GradeDto toDto(Grade grade) {
        GradeDto dto = toDtoBase(grade);
        if (grade.getStudent() != null) {
            dto.setStudentName(grade.getStudent().getName());
        }
        if (grade.getSubject() != null) {
            dto.setSubjectName(grade.getSubject().getName());
        }
        if (grade.getTeacher() != null) {
            dto.setTeacherName(grade.getTeacher().getName());
        }
        return dto;
    }

    @Mapping(target = "student", ignore = true)
    @Mapping(target = "subject", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "id", ignore = true)
    Grade toEntity(GradeDto gradeDto);

    default GradeDto toDtoWithNames(Grade grade) {
        GradeDto dto = toDto(grade);
        if (grade.getStudent() != null) {
            dto.setStudentName(grade.getStudent().getName());
        }
        if (grade.getSubject() != null) {
            dto.setSubjectName(grade.getSubject().getName());
        }
        if (grade.getTeacher() != null) {
            dto.setTeacherName(grade.getTeacher().getName());
        }
        return dto;
    }
}
