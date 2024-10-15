package com.vmDashboard.Vmdashboard.repository;

import com.vmDashboard.Vmdashboard.model.VM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VMRepository extends JpaRepository<VM, Long> {

    // Use Optional to handle cases where no VM is found
    @Query("SELECT v FROM VM v WHERE LOWER(v.name) = LOWER(:name)")
    Optional<VM> findByName(@Param("name") String name);
}
