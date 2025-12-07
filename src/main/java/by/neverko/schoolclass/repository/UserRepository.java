package by.neverko.schoolclass.repository;

import by.neverko.schoolclass.entity.Role;
import by.neverko.schoolclass.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role);

    List<User> findByClassEntityIdAndRole(Long classId, Role role);

    Optional<User> findByIdAndRole(Long Id, Role role);

    List<User> findByStudentIdAndRole(Long studentId, Role role);

    @Query("SELECT u FROM User u WHERE u.classEntity.id = :classId AND u.role = Role.CLASS_TEACHER")
    Optional<User> findClassTeacherByClassId(Long classId);

    @Query("SELECT u FROM User u JOIN u.subjects s WHERE s.id = :subjectId AND u.role = :role")
    List<User> findTeachersBySubjectId(Long subjectId, Role role);

    List<User> findParentsByStudentId(Long studentId);

//    @Query("SELECT u FROM User u WHERE u.student.id = :studentId AND u.role = 'PARENT'")
//    List<User> findParentsByStudentId(Long studentId);

    boolean existsByTelegramChatId(String chatId);

    @Query("SELECT EXISTS " +
            "(SELECT 1 FROM Grade g WHERE g.teacher.id = :teacherId AND g.student.id = :studentId)")
    boolean isTeacherTeachingStudent(@Param("teacherId") Long teacherId, @Param("studentId") Long studentId);
}
