package com.miniprojetspring.Service;

import com.miniprojetspring.Model.Project;

import java.util.UUID;

public interface ProjectService {
    Project getProjectById(String id);
}
