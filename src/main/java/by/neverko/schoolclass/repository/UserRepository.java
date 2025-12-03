package by.neverko.schoolclass.repository;

import by.neverko.schoolclass.entity.Role;
import by.neverko.schoolclass.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role);

    List<User> findByClassEntityIdAndRole(Long classId, Role role);

    List<User> findByStudentIdAndRole(Long studentId, Role role);

    @Query("SELECT u FROM User u WHERE u.classEntity.id = :classId AND u.role = by.neverko.schoolclass.entity.Role.CLASS_TEACHER")
    Optional<User> findClassTeacherByClassId(Long classId);

    @Query("SELECT u FROM User u JOIN u.subjects s WHERE s.id = :subjectId AND u.role = :role")
    List<User> findTeachersBySubjectId(Long subjectId, Role role);
}
