package com.example.JavaWEB.repository;

import com.example.JavaWEB.model.Role;
import com.example.JavaWEB.model.RoleClass;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<RoleClass, Long> {
    RoleClass findRoleClassByRole(Role role);
}
