package com.miniprojetspring.Repository;

import com.miniprojetspring.Model.UserStory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserStoryRepository extends JpaRepository<UserStory, UUID> {

}
