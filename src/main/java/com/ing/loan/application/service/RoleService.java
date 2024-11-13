package com.ing.loan.application.service;

import com.ing.loan.application.entity.Role;
import com.ing.loan.application.repository.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role getCustomerRole() {
        return roleRepository.findFirstByAuthority("ROLE_CUSTOMER");
    }
}
