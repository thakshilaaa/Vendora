-- =============================================================================
-- Vendora – complete MySQL 8+ database schema
-- Migrations (existing DBs) — run only if you created users before this schema:
--   ALTER TABLE users DROP INDEX email;      -- or DROP INDEX <name> (see SHOW INDEX FROM users;)
--   ALTER TABLE users DROP INDEX phone;     -- if multiple accounts may share a phone
-- Matches JPA entities under com.vendora.* (see package comments per section).
-- Usage:
--   mysql -u root -p < "Vendora - Database.sql"
-- Or run this file in your client after selecting / creating the database.
--
-- role/status enums align with com.vendora.epic1.model.enums.RoleType and
-- com.vendora.epic1.model.enums.UserStatus.
--
-- Module → main tables (epic6 has only one @Entity: Supplier → suppliers;
--     supplier dashboards also use products + users. supplier_profiles is the
--     assignment/legacy profile keyed by user_id, separate from suppliers.)
--   epic1: users, email_verification_tokens, password_reset_tokens, user_profiles,
--          login_audits, account_deletion_requests, partnership_applications
--   statics: statics_partnership_applications
--   epic2: products
--   epic3: carts, cart_items
--   epic4: orders, payments
--   epic5: deliveries, delivery_assignments, delivery_status_history,
--          return_requests, failure_logs
--   epic6: suppliers
--   shared: pending_registrations, supplier_profiles, delivery_personnel_profiles
-- =============================================================================

SET NAMES utf8mb4;
SET time_zone = '+00:00';

CREATE DATABASE IF NOT EXISTS vendora
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE vendora;

-- -----------------------------------------------------------------------------
-- 1) Core: users
-- -----------------------------------------------------------------------------

CREATE TABLE users (
    user_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_code VARCHAR(20) NOT NULL UNIQUE,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    nic VARCHAR(12) NOT NULL UNIQUE,
    first_name VARCHAR(100) NULL,
    last_name VARCHAR(100) NULL,
    address_line_1 VARCHAR(255) NOT NULL,
    address_line_2 VARCHAR(255) NULL,
    city VARCHAR(100) NOT NULL,
    district ENUM(
        'COLOMBO','GAMPAHA','KALUTARA','KANDY','MATALE','NUWARA_ELIYA',
        'GALLE','MATARA','HAMBANTOTA','JAFFNA','KILINOCHCHI','MANNAR',
        'MULLAITIVU','VAVUNIYA','TRINCOMALEE','BATTICALOA','AMPARA',
        'KURUNEGALA','PUTTALAM','ANURADHAPURA','POLONNARUWA',
        'BADULLA','MONARAGALA','RATNAPURA','KEGALLE'
    ) NOT NULL,
    province ENUM(
        'WESTERN','CENTRAL','SOUTHERN','NORTHERN','EASTERN',
        'NORTH_WESTERN','NORTH_CENTRAL','UVA','SABARAGAMUWA'
    ) NOT NULL,
    postal_code VARCHAR(10) NULL,
    phone_number VARCHAR(15) NULL,
    role ENUM('ROLE_CUSTOMER','ROLE_ADMIN','ROLE_SUPPLIER','ROLE_DELIVERY') NOT NULL,
    status ENUM(
        'ACTIVE','INACTIVE','PENDING_VERIFICATION','SUSPENDED','REJECTED','DELETED'
    ) NOT NULL DEFAULT 'ACTIVE',
    profile_picture_url VARCHAR(500) NULL,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    verification_method VARCHAR(32) NULL DEFAULT 'EMAIL',
    last_login_at DATETIME(6) NULL,
    failed_login_attempts INT NOT NULL DEFAULT 0,
    account_locked_until DATETIME(6) NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    password_hash VARCHAR(255) NOT NULL,
    password VARCHAR(255) NULL,
    CONSTRAINT chk_users_email CHECK (email LIKE '%@%.%'),
    CONSTRAINT chk_users_phone CHECK (phone REGEXP '^(\\+94|0)(7[0-9]{8}|1[0-9]{8}|2[0-9]{8}|3[0-9]{8}|4[0-9]{8}|5[0-9]{8}|6[0-9]{8}|8[0-9]{8}|9[0-9]{8})$'),
    CONSTRAINT chk_users_nic CHECK (nic REGEXP '^([0-9]{9}[VvXx]|[0-9]{12})$')
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 2) Registration & partner flows
-- -----------------------------------------------------------------------------

CREATE TABLE pending_registrations (
    pending_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    pending_code VARCHAR(20) NOT NULL UNIQUE,
    role ENUM('SUPPLIER','DELIVERY_PERSON') NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    nic VARCHAR(12) NOT NULL,
    address_line_1 VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    district VARCHAR(100) NOT NULL,
    province VARCHAR(100) NOT NULL,
    postal_code VARCHAR(10) NULL,
    profile_picture_url VARCHAR(500) NULL,
    password_hash VARCHAR(255) NOT NULL,
    business_name VARCHAR(200) NULL,
    business_registration_number VARCHAR(100) NULL,
    tin_number VARCHAR(50) NULL,
    product_category VARCHAR(150) NULL,
    product_details TEXT NULL,
    emergency_contact_name VARCHAR(150) NULL,
    emergency_contact_phone VARCHAR(15) NULL,
    vehicle_type ENUM('BIKE','THREE_WHEELER','CAR','VAN','LORRY') NULL,
    vehicle_model VARCHAR(150) NULL,
    licence_plate VARCHAR(20) NULL,
    status ENUM('PENDING','APPROVED','REJECTED','EXPIRED') NOT NULL DEFAULT 'PENDING',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE supplier_profiles (
    supplier_id BIGINT UNSIGNED PRIMARY KEY,
    business_name VARCHAR(200) NOT NULL,
    business_registration_number VARCHAR(100) NOT NULL UNIQUE,
    tin_number VARCHAR(50) NULL,
    product_category VARCHAR(150) NULL,
    product_details TEXT NULL,
    approval_status ENUM('APPROVED','SUSPENDED') NOT NULL DEFAULT 'APPROVED',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_supplier_profiles_user FOREIGN KEY (supplier_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE delivery_personnel_profiles (
    delivery_person_id BIGINT UNSIGNED PRIMARY KEY,
    emergency_contact_name VARCHAR(150) NOT NULL,
    emergency_contact_phone VARCHAR(15) NOT NULL,
    vehicle_type ENUM('BIKE','THREE_WHEELER','CAR','VAN','LORRY') NOT NULL,
    vehicle_model VARCHAR(150) NOT NULL,
    licence_plate VARCHAR(20) NOT NULL UNIQUE,
    service_area_district VARCHAR(100) NOT NULL,
    availability_status ENUM('AVAILABLE','UNAVAILABLE','ON_DELIVERY','SUSPENDED') NOT NULL DEFAULT 'AVAILABLE',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_delivery_profiles_user FOREIGN KEY (delivery_person_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- com.vendora.epic6.model.Supplier
CREATE TABLE suppliers (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    company_name VARCHAR(200) NOT NULL,
    contact_person VARCHAR(200) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(50) NOT NULL,
    address VARCHAR(500) NOT NULL,
    business_address VARCHAR(500) NULL,
    status ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING',
    user_id BIGINT UNSIGNED NULL UNIQUE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_suppliers_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- com.vendora.epic1.model.PartnershipApplication
CREATE TABLE partnership_applications (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    applicant_type ENUM('SUPPLIER','DELIVERY') NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    contact_person_name VARCHAR(255) NULL,
    email VARCHAR(255) NOT NULL,
    phone_number VARCHAR(100) NULL,
    nic VARCHAR(20) NULL,
    address_line1 VARCHAR(500) NULL,
    business_address VARCHAR(500) NULL,
    province VARCHAR(100) NULL,
    district VARCHAR(100) NULL,
    city VARCHAR(100) NULL,
    postal_code VARCHAR(20) NULL,
    business_name VARCHAR(500) NULL,
    business_reg_number VARCHAR(200) NULL,
    tin_number VARCHAR(100) NULL,
    product_category VARCHAR(200) NULL,
    product_details TEXT NULL,
    status ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING',
    review_note TEXT NULL,
    reviewed_at DATETIME(6) NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- com.vendora.statics.model.PartnershipApplication
CREATE TABLE statics_partnership_applications (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    application_type VARCHAR(40) NOT NULL,
    business_name VARCHAR(500) NOT NULL,
    contact_person_name VARCHAR(500) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone_number VARCHAR(100) NOT NULL,
    business_address VARCHAR(2000) NOT NULL,
    business_registration_number VARCHAR(200) NULL,
    product_category VARCHAR(500) NULL,
    service_areas VARCHAR(1000) NULL,
    fleet_size VARCHAR(100) NULL,
    description VARCHAR(1000) NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    submitted_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    reviewed_at DATETIME(6) NULL,
    admin_remarks VARCHAR(1000) NULL,
    reviewed_by_id BIGINT UNSIGNED NULL,
    user_id BIGINT UNSIGNED NULL,
    CONSTRAINT fk_statics_partnership_reviewer FOREIGN KEY (reviewed_by_id) REFERENCES users(user_id) ON DELETE SET NULL,
    CONSTRAINT fk_statics_partnership_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- 3) Auth & profile (epic1)
-- -----------------------------------------------------------------------------

CREATE TABLE email_verification_tokens (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    token_type VARCHAR(20) NULL,
    user_id BIGINT UNSIGNED NOT NULL,
    expiry_date DATETIME(6) NOT NULL,
    CONSTRAINT fk_evt_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE password_reset_tokens (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT UNSIGNED NOT NULL,
    expiry_date DATETIME(6) NOT NULL,
    CONSTRAINT fk_prt_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE user_profiles (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL UNIQUE,
    phone_number VARCHAR(100) NULL,
    address VARCHAR(2000) NULL,
    city VARCHAR(200) NULL,
    country VARCHAR(200) NULL,
    created_at DATETIME(6) NULL,
    updated_at DATETIME(6) NULL,
    CONSTRAINT fk_user_profiles_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE login_audits (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    login_time DATETIME(6) NULL,
    ip_address VARCHAR(100) NULL,
    user_agent VARCHAR(2000) NULL,
    CONSTRAINT fk_login_audits_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE account_deletion_requests (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    request_date DATETIME(6) NULL,
    processed BIT(1) NOT NULL DEFAULT 0,
    CONSTRAINT fk_adr_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- 4) Catalog: com.vendora.epic2.model.Product
-- -----------------------------------------------------------------------------

CREATE TABLE products (
    product_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    supplier_id BIGINT UNSIGNED NOT NULL,
    name VARCHAR(200) NOT NULL,
    brand VARCHAR(100) NOT NULL,
    sku VARCHAR(100) NULL UNIQUE,
    barcode VARCHAR(50) NULL UNIQUE,
    category ENUM('COSMETICS','SKINCARE','HAIRCARE','BODYCARE','FRAGRANCE','BEAUTY_TOOLS') NOT NULL,
    description TEXT NULL,
    ingredients TEXT NULL,
    usage_instructions TEXT NULL,
    price DECIMAL(10,2) NOT NULL,
    cost_price DECIMAL(10,2) NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    low_stock_threshold INT NOT NULL DEFAULT 1,
    unit VARCHAR(50) NULL,
    shade VARCHAR(100) NULL,
    skin_type VARCHAR(100) NULL,
    volume VARCHAR(100) NULL,
    manufacture_date DATE NULL,
    expiry_date DATE NULL,
    image LONGBLOB NULL,
    image_content_type VARCHAR(50) NULL,
    status ENUM('ACTIVE','INACTIVE','DISCONTINUED') NOT NULL DEFAULT 'ACTIVE',
    country_of_origin VARCHAR(100) NULL,
    tags TEXT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_products_supplier_profile FOREIGN KEY (supplier_id) REFERENCES supplier_profiles(supplier_id) ON DELETE CASCADE,
    CONSTRAINT chk_products_price CHECK (price > 0),
    CONSTRAINT chk_products_stock CHECK (stock_quantity >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- 5) Cart: com.vendora.epic3
-- -----------------------------------------------------------------------------

CREATE TABLE carts (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL UNIQUE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_carts_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE cart_items (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    cart_id BIGINT UNSIGNED NOT NULL,
    product_id BIGINT UNSIGNED NOT NULL,
    quantity INT NOT NULL,
    CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_items_product FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- 6) Orders & payments: com.vendora.epic4
-- Order PK column is "id" (JPA). Deliveries below reference orders(id).
-- -----------------------------------------------------------------------------

CREATE TABLE orders (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NULL,
    last_name VARCHAR(255) NULL,
    product VARCHAR(2000) NULL,
    amount DOUBLE NULL,
    status VARCHAR(100) NULL,
    payment_status VARCHAR(100) NULL,
    phone VARCHAR(50) NULL,
    payment_method VARCHAR(100) NULL,
    user_id BIGINT UNSIGNED NULL,
    KEY idx_orders_user (user_id),
    CONSTRAINT fk_orders_user_jpa FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- com.vendora.epic4.model.Payment
CREATE TABLE payments (
    payment_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT UNSIGNED NULL,
    status VARCHAR(100) NULL,
    payment_method VARCHAR(100) NULL,
    amount DOUBLE NULL,
    payment_date DATETIME(6) NULL,
    transaction_id VARCHAR(500) NULL,
    KEY idx_payments_order (order_id),
    CONSTRAINT fk_payments_order_jpa FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- 7) Delivery: com.vendora.epic5 (UUID CHAR(36) keys; FKs to orders.id, users.user_id)
-- -----------------------------------------------------------------------------

CREATE TABLE deliveries (
    id CHAR(36) NOT NULL PRIMARY KEY,
    order_id BIGINT UNSIGNED NOT NULL,
    customer_id BIGINT UNSIGNED NOT NULL,
    customer_district VARCHAR(50) NOT NULL,
    agent_id BIGINT UNSIGNED NULL,
    tracking_number VARCHAR(50) NOT NULL,
    status ENUM('PENDING','ASSIGNED','OUT_FOR_DELIVERY','DELIVERED','FAILED','RETURNED') NOT NULL DEFAULT 'PENDING',
    delivery_address TEXT NOT NULL,
    notes TEXT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    picked_up_at DATETIME(6) NULL,
    delivered_at DATETIME(6) NULL,
    CONSTRAINT uq_deliveries_tracking UNIQUE (tracking_number),
    CONSTRAINT fk_deliveries_order_e5 FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_deliveries_customer_e5 FOREIGN KEY (customer_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_deliveries_agent_e5 FOREIGN KEY (agent_id) REFERENCES users(user_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE delivery_assignments (
    id CHAR(36) NOT NULL PRIMARY KEY,
    delivery_id CHAR(36) NOT NULL,
    agent_id BIGINT UNSIGNED NOT NULL,
    status ENUM('PENDING','ACCEPTED','REJECTED') NOT NULL DEFAULT 'PENDING',
    rejection_reason TEXT NULL,
    assigned_at DATETIME(6) NOT NULL,
    responded_at DATETIME(6) NULL,
    CONSTRAINT fk_da_delivery_e5 FOREIGN KEY (delivery_id) REFERENCES deliveries(id) ON DELETE CASCADE,
    CONSTRAINT fk_da_agent_e5 FOREIGN KEY (agent_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE delivery_status_history (
    id CHAR(36) NOT NULL PRIMARY KEY,
    delivery_id CHAR(36) NOT NULL,
    status ENUM('PENDING','ASSIGNED','OUT_FOR_DELIVERY','DELIVERED','FAILED','RETURNED') NOT NULL,
    changed_by BIGINT UNSIGNED NULL,
    changed_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_dsh_delivery_e5 FOREIGN KEY (delivery_id) REFERENCES deliveries(id) ON DELETE CASCADE,
    CONSTRAINT fk_dsh_user_e5 FOREIGN KEY (changed_by) REFERENCES users(user_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE return_requests (
    id CHAR(36) NOT NULL PRIMARY KEY,
    delivery_id CHAR(36) NOT NULL,
    customer_id BIGINT UNSIGNED NOT NULL,
    agent_id BIGINT UNSIGNED NULL,
    reason_code VARCHAR(50) NOT NULL,
    description TEXT NULL,
    status ENUM('REQUESTED','APPROVED','REJECTED','PICKUP_SCHEDULED','PICKED_UP','COMPLETED','CANCELLED') NOT NULL DEFAULT 'REQUESTED',
    requested_at DATETIME(6) NOT NULL,
    completed_at DATETIME(6) NULL,
    CONSTRAINT fk_rr_delivery FOREIGN KEY (delivery_id) REFERENCES deliveries(id) ON DELETE CASCADE,
    CONSTRAINT fk_rr_customer FOREIGN KEY (customer_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_rr_agent FOREIGN KEY (agent_id) REFERENCES users(user_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE failure_logs (
    id CHAR(36) NOT NULL PRIMARY KEY,
    delivery_id CHAR(36) NOT NULL,
    logged_by BIGINT UNSIGNED NOT NULL,
    reason_code VARCHAR(50) NOT NULL,
    description TEXT NULL,
    attempt_number INT NOT NULL DEFAULT 1,
    logged_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_fl_delivery FOREIGN KEY (delivery_id) REFERENCES deliveries(id) ON DELETE CASCADE,
    CONSTRAINT fk_fl_user FOREIGN KEY (logged_by) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- End of schema
-- =============================================================================
