package com.jee.app.repositories;
import com.jee.app.model.Donation;
import com.jee.app.model.CharityAction;
import com.jee.app.model.Users;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface DonationRepository extends JpaRepository<Donation, Long> {
    List<Donation> findByUser(Users user);
    List<Donation> findByAction(CharityAction action);   
}