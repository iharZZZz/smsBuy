package smolka.smsapi.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import smolka.smsapi.model.Activation;
import smolka.smsapi.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface ActivationRepository extends JpaRepository<Activation, Long> {

    @EntityGraph(value = "activation.all")
    @Query(value = "SELECT act FROM Activation act WHERE plannedFinishDate <= ?1 AND (act.status = 0 OR act.status = 1)")
    List<Activation> findAllActivationsByPlannedFinishDateLessThanEqual(LocalDateTime dateTime);

    @EntityGraph(value = "activation.all")
    List<Activation> findAllActivationsByStatus(Integer status);

    @EntityGraph(value = "activation.all")
    @Query(value = "SELECT act FROM Activation act WHERE act.user = ?1 AND (act.status = 0 OR act.status = 1)")
    List<Activation> findAllCurrentActivationsByUser(User user);

    @EntityGraph(value = "activation.all")
    Activation findActivationByIdAndUser(Long id, User user);

    @EntityGraph(value = "activation.all")
    Activation findActivationById(Long id);
}
