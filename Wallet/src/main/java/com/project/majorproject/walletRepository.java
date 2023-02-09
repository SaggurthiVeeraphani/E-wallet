package com.project.majorproject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface walletRepository extends JpaRepository<wallet,Integer> {
}
