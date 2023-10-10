package rs.raf.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.raf.demo.model.Machine;
import rs.raf.demo.model.Status;

import java.util.Date;
import java.util.List;

@Repository
public interface MachineRepository extends JpaRepository<Machine, Long> {
   /*
    @Query("SELECT m FROM Machine m WHERE m.user.userId = :userId " +
            "AND (:name IS NULL OR m.name LIKE CONCAT('%', :name, '%')) " +
            "AND (:dateFrom IS NULL OR m.creationDate >= :dateFrom) " +
            "AND (:dateTo IS NULL OR m.creationDate <= :dateTo)")
    List<Machine> searchMachines(
            @Param("userId") Long userId,
            @Param("name") String name,
            @Param("dateFrom") Date dateFrom,
            @Param("dateTo") Date dateTo
    );*/


    @Query("SELECT m FROM Machine m WHERE " +
            "m.user.userId = :userId " +
            "AND (:name IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND ((CAST(:dateFrom AS date) IS NULL AND CAST(:dateTo AS date) IS NULL) " +
            "OR (m.creationDate >= CAST(:dateFrom AS date) AND m.creationDate <= CAST(:dateTo AS date)))")
    List<Machine> searchMachines(
            @Param("userId") Long userId,
            @Param("name") String name,
            @Param("dateFrom") Date dateFrom,
            @Param("dateTo") Date dateTo
    );


    List<Machine> findByUser_UserIdAndNameContainingIgnoreCaseAndCreationDateBetween(
            Long userId, String name, Date dateFrom, Date dateTo);


    @Query("select m from Machine m where m.active = true and m.user.userId = :userId")
    List<Machine> findActiveMachines(@Param("userId") Long userId);
}
