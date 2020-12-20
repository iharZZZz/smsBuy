package smolka.smsapi.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import smolka.smsapi.model.Activation;
import smolka.smsapi.model.UserKey;

import java.time.LocalDateTime;
import java.util.List;

public interface ActivationRepository extends JpaRepository<Activation, Long> {

    @EntityGraph(value = "activation.all")
    List<Activation> findAllActivationsByPlannedFinishDateLessThanEqual(LocalDateTime dateTime);

    @EntityGraph(value = "activation.all")
    List<Activation> findAllActivationsByStatus(Integer status);

    @EntityGraph(value = "activation.all")
    List<Activation> findAllActivationsByUserKey(UserKey userKey);

    @EntityGraph(value = "activation.all")
    Activation findActivationByIdAndUserKey(Long id, UserKey userKey);

    @EntityGraph(value = "activation.all")
    Activation findActivationById(Long id);
}
