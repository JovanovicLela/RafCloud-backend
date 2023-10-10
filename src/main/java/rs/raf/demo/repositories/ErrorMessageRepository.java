package rs.raf.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.raf.demo.model.ErrorMessage;

import java.util.List;

@Repository
public interface ErrorMessageRepository extends JpaRepository<ErrorMessage, Long> {

    @Query("select em from ErrorMessage em where em.machine.user.userId = :userId")
    List<ErrorMessage> findErrorMessages(@Param("userId") Long userId);
}
