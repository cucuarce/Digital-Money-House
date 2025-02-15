package com.digital_money_house.accounts_service.repository;

import com.digital_money_house.accounts_service.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByUserId(Long id);

    Account findByCvu(String cvu);

    boolean existsByCvu(String cvu);

    boolean existsByAlias(String alias);

}
