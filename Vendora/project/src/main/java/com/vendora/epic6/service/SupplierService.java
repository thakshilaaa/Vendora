package com.vendora.epic6.service;

import com.vendora.epic2.repository.ProductRepository;
import com.vendora.epic1.model.User;
import com.vendora.epic1.model.enums.District;
import com.vendora.epic1.model.enums.Province;
import com.vendora.epic1.model.enums.RoleType;
import com.vendora.epic1.model.enums.UserStatus;
import com.vendora.epic1.repository.UserRepository;
import com.vendora.epic6.dto.SupplierRegistrationDto;
import com.vendora.epic6.model.Supplier;
import com.vendora.epic6.model.SupplierStatus;
import com.vendora.epic6.repository.SupplierRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic for supplier registration / approval / rejection.
 */
@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public SupplierService(SupplierRepository supplierRepository,
                           UserRepository userRepository,
                           ProductRepository productRepository,
                           PasswordEncoder passwordEncoder,
                           EmailService emailService) {
        this.supplierRepository = supplierRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public Supplier register(SupplierRegistrationDto dto) {
        supplierRepository.findByEmail(dto.getEmail()).ifPresent(s -> {
            throw new IllegalStateException("A supplier with this email already exists.");
        });

        Supplier s = new Supplier();
        s.setCompanyName(dto.getCompanyName());
        s.setContactPerson(dto.getContactPerson());
        s.setEmail(dto.getEmail());
        s.setPhone(dto.getPhone());
        s.setAddress(dto.getAddress());
        s.setBusinessAddress(dto.getAddress());
        s.setStatus(SupplierStatus.PENDING);
        return supplierRepository.save(s);
    }

    public List<Supplier> pending() {
        return supplierRepository.findByStatus(SupplierStatus.PENDING);
    }

    public List<Supplier> all() {
        return supplierRepository.findAll();
    }

    public List<Supplier> search(String q) {
        if (q == null || q.isBlank()) return all();
        return supplierRepository
                .findByCompanyNameContainingIgnoreCaseOrEmailContainingIgnoreCase(q, q);
    }

    public Supplier byId(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found: " + id));
    }

    public Supplier byUser(User user) {
        return supplierRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("No supplier profile linked to user"));
    }

    /**
     * Approve a supplier: create a User account with role SUPPLIER, default
     * password equal to the supplier's email, then email the credentials.
     */
    @Transactional
    public Supplier approve(Long supplierId) {
        Supplier s = byId(supplierId);
        if (s.getStatus() == SupplierStatus.APPROVED) {
            return s;
        }

        String tempPassword = s.getEmail();   // simple "password = email" rule

        User user = new User();
        user.setEmail(s.getEmail());
        user.setFullName(s.getContactPerson());
        user.setPhone(s.getPhone() != null && !s.getPhone().isBlank() ? s.getPhone() : "0000000000");
        user.setPasswordHash(passwordEncoder.encode(tempPassword));
        user.setRole(RoleType.ROLE_SUPPLIER);
        user.setStatus(UserStatus.ACTIVE);
        user.setEmailVerified(true);
        user.setNic(String.format("1%011d", s.getId()));
        user.setAddressLine1(s.getAddress() != null && !s.getAddress().isBlank() ? s.getAddress() : "N/A");
        user.setCity("Colombo");
        user.setDistrict(District.COLOMBO);
        user.setProvince(Province.WESTERN);
        userRepository.save(user);

        s.setUser(user);
        s.setStatus(SupplierStatus.APPROVED);
        supplierRepository.save(s);

        emailService.sendApprovalEmail(s.getEmail(), s.getCompanyName(), tempPassword);
        return s;
    }

    /** Reject: remove the pending row and email the supplier. */
    @Transactional
    public void reject(Long supplierId) {
        Supplier s = byId(supplierId);
        emailService.sendRejectionEmail(s.getEmail(), s.getCompanyName());
        supplierRepository.delete(s);
    }

    @Transactional
    public void delete(Long supplierId) {
        Supplier s = byId(supplierId);
        Long userId = s.getUser() != null ? s.getUser().getId() : null;
        if (s.getUser() != null) {
            productRepository.deleteBySupplierId(s.getUser().getId());
        }
        supplierRepository.delete(s);
        if (userId != null) {
            userRepository.findById(userId).ifPresent(userRepository::delete);
        }
    }
}
