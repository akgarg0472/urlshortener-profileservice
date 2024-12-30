package com.akgarg.profile.db.repo;

import com.akgarg.profile.profile.v1.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, String> {

    Optional<Profile> findByIdAndDeletedFalse(String id);

    boolean existsByEmail(String email);

}
