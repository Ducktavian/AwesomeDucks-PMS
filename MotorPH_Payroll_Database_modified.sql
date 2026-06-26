-- =============================================================================
-- MOTORPH PAYROLL SYSTEM DATABASE
-- Database: motorph_payroll_db
-- =============================================================================

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

CREATE DATABASE IF NOT EXISTS `motorph_payroll_db`;
USE `motorph_payroll_db`;

-- =============================================================================
-- PART 1: SCHEMA DEFINITION
-- =============================================================================


DROP TABLE IF EXISTS `department`;
CREATE TABLE `department` (
  `department_id`   INT          NOT NULL AUTO_INCREMENT,
  `department_name` VARCHAR(100) NOT NULL,
  `description`     VARCHAR(255) NULL,
  `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`      DATETIME     NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by`      INT          NULL,
  `updated_by`      INT          NULL,
  PRIMARY KEY (`department_id`),
  UNIQUE KEY `uq_department_name` (`department_name`)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `employee_position`;
CREATE TABLE `employee_position` (
  `position_id`   INT          NOT NULL AUTO_INCREMENT,
  `position_name` VARCHAR(100) NOT NULL,
  `department_id` INT          NOT NULL,
  `description`   VARCHAR(255) NULL,
  `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`    DATETIME     NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by`    INT          NULL,
  `updated_by`    INT          NULL,
  PRIMARY KEY (`position_id`),
  UNIQUE KEY `uq_position_name` (`position_name`),
  KEY `idx_position_department` (`department_id`),
  CONSTRAINT `fk_position_department` FOREIGN KEY (`department_id`) REFERENCES `department` (`department_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `employment_status`;
CREATE TABLE `employment_status` (
  `employment_status_id` INT         NOT NULL AUTO_INCREMENT,
  `status_type`          VARCHAR(50) NOT NULL,
  `created_at`           DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`           DATETIME    NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by`           INT         NULL,
  `updated_by`           INT         NULL,
  PRIMARY KEY (`employment_status_id`),
  UNIQUE KEY `uq_employment_status_type` (`status_type`)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `request_status`;
CREATE TABLE `request_status` (
  `request_status_id`   INT         NOT NULL AUTO_INCREMENT,
  `request_status_type` VARCHAR(50) NOT NULL,
  `created_at`          DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`          DATETIME    NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by`          INT         NULL,
  `updated_by`          INT         NULL,
  PRIMARY KEY (`request_status_id`),
  UNIQUE KEY `uq_request_status_type` (`request_status_type`)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `payroll_status`;
CREATE TABLE `payroll_status` (
  `payroll_status_id` INT         NOT NULL AUTO_INCREMENT,
  `status_name`       VARCHAR(50) NOT NULL,
  `created_at`        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`        DATETIME    NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by`        INT         NULL,
  `updated_by`        INT         NULL,
  PRIMARY KEY (`payroll_status_id`),
  UNIQUE KEY `uq_payroll_status_name` (`status_name`)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `attendance_status`;
CREATE TABLE `attendance_status` (
  `attendance_status_id` INT         NOT NULL AUTO_INCREMENT,
  `status_name`          VARCHAR(50) NOT NULL,
  `created_at`           DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`           DATETIME    NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by`           INT         NULL,
  `updated_by`           INT         NULL,
  PRIMARY KEY (`attendance_status_id`),
  UNIQUE KEY `uq_attendance_status_name` (`status_name`)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `leave_type`;
CREATE TABLE `leave_type` (
  `leave_type_id`   INT          NOT NULL AUTO_INCREMENT,
  `leave_type_name` VARCHAR(100) NOT NULL,
  `description`     VARCHAR(255) NULL,
  `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`      DATETIME     NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by`      INT          NULL,
  `updated_by`      INT          NULL,
  PRIMARY KEY (`leave_type_id`),
  UNIQUE KEY `uq_leave_type_name` (`leave_type_name`)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `government_id_type`;
CREATE TABLE `government_id_type` (
  `government_id_type_id` INT          NOT NULL AUTO_INCREMENT,
  `id_type`               VARCHAR(50)  NOT NULL,
  `format_pattern`        VARCHAR(100) NOT NULL,
  `description`           VARCHAR(255) NULL,
  `created_at`            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`            DATETIME     NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by`            INT          NULL,
  `updated_by`            INT          NULL,
  PRIMARY KEY (`government_id_type_id`),
  UNIQUE KEY `uq_government_id_type` (`id_type`)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `benefit_type`;
CREATE TABLE `benefit_type` (
  `benefit_type_id` INT          NOT NULL AUTO_INCREMENT,
  `benefit_name`    VARCHAR(100) NOT NULL,
  `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`      DATETIME     NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by`      INT          NULL,
  `updated_by`      INT          NULL,
  PRIMARY KEY (`benefit_type_id`),
  UNIQUE KEY `uq_benefit_name` (`benefit_name`)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `deduction_type`;
CREATE TABLE `deduction_type` (
  `deduction_type_id` INT          NOT NULL AUTO_INCREMENT,
  `deduction_name`    VARCHAR(100) NOT NULL,
  `created_at`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`        DATETIME     NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by`        INT          NULL,
  `updated_by`        INT          NULL,
  PRIMARY KEY (`deduction_type_id`),
  UNIQUE KEY `uq_deduction_name` (`deduction_name`)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `help_center_category`;
CREATE TABLE `help_center_category` (
  `help_center_category_id` INT          NOT NULL AUTO_INCREMENT,
  `category_name`           VARCHAR(100) NOT NULL,
  `created_at`              DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`              DATETIME     NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by`              INT          NULL,
  `updated_by`              INT          NULL,
  PRIMARY KEY (`help_center_category_id`),
  UNIQUE KEY `uq_help_center_category_name` (`category_name`)
) ENGINE=InnoDB;

-- Main tables
DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee` (
  `employee_id`               INT          NOT NULL,
  `first_name`                VARCHAR(100) NOT NULL,
  `last_name`                 VARCHAR(100) NOT NULL,
  `birthdate`                 DATE         NOT NULL,
  `phone_number`              VARCHAR(20)  NOT NULL,
  `email`                     VARCHAR(100) NULL,
  `position_id`               INT          NOT NULL,
  `immediate_supervisor_id`   INT          NULL,
  `employment_status_id`      INT          NOT NULL,
  `created_at`                DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`                DATETIME     NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by`                INT          NULL,
  `updated_by`                INT          NULL,
  PRIMARY KEY (`employee_id`),
  UNIQUE KEY `uq_employee_email` (`email`),
  KEY `idx_employee_position` (`position_id`),
  KEY `idx_employee_supervisor` (`immediate_supervisor_id`),
  KEY `idx_employee_status` (`employment_status_id`),
  CONSTRAINT `fk_employee_position` FOREIGN KEY (`position_id`) REFERENCES `employee_position` (`position_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_employee_supervisor` FOREIGN KEY (`immediate_supervisor_id`) REFERENCES `employee` (`employee_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_employee_status` FOREIGN KEY (`employment_status_id`) REFERENCES `employment_status` (`employment_status_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
  `address_id`   INT  NOT NULL AUTO_INCREMENT,
  `full_address`TEXT NOT NULL,
  `created_at`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`  DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by`  INT NULL,
  `updated_by`  INT NULL,
  PRIMARY KEY (`address_id`)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `employee_address`;
CREATE TABLE `employee_address` (
  `employee_address_id` INT         NOT NULL AUTO_INCREMENT,
  `employee_id`         INT         NOT NULL,
  `address_id`          INT         NOT NULL,
  `address_type`        ENUM('permanent','current') NOT NULL DEFAULT 'current',
  `created_at`          DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`          DATETIME    NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by`          INT         NULL,
  `updated_by`          INT         NULL,
  PRIMARY KEY (`employee_address_id`),
  UNIQUE KEY `uq_employee_address` (`employee_id`,`address_id`,`address_type`),
  KEY `idx_employee_address_employee` (`employee_id`),
  KEY `idx_employee_address_lookup` (`address_id`),
  CONSTRAINT `fk_employee_address_employee` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`employee_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_employee_address_address` FOREIGN KEY (`address_id`) REFERENCES `address` (`address_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `employee_government_id`;
CREATE TABLE `employee_government_id` (
  `employee_government_id_id` INT         NOT NULL AUTO_INCREMENT,
  `employee_id`               INT         NOT NULL,
  `government_id_type_id`     INT         NOT NULL,
  `id_number`                 VARCHAR(50) NOT NULL,
  `created_at`                DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`                DATETIME    NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by`                INT         NULL,
  `updated_by`                INT         NULL,
  PRIMARY KEY (`employee_government_id_id`),
  UNIQUE KEY `uq_gov_id_number` (`id_number`),
  UNIQUE KEY `uq_employee_gov_id_type` (`employee_id`,`government_id_type_id`),
  KEY `idx_employee_government_id_employee` (`employee_id`),
  KEY `idx_employee_gov_id_type` (`government_id_type_id`),
  CONSTRAINT `fk_emp_gov_id_employee` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`employee_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_emp_gov_id_type` FOREIGN KEY (`government_id_type_id`) REFERENCES `government_id_type` (`government_id_type_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `user_account`;
CREATE TABLE `user_account` (
  `user_account_id` INT          NOT NULL AUTO_INCREMENT,
  `employee_id`     INT          NOT NULL,
  `username`        VARCHAR(100) NOT NULL,
  `password_hash`   VARCHAR(255) NOT NULL,
  `is_active`       BOOLEAN      NOT NULL DEFAULT TRUE,
  `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`      DATETIME     NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by`      INT          NULL,
  `updated_by`      INT          NULL,
  PRIMARY KEY (`user_account_id`),
  UNIQUE KEY `uq_user_account_employee` (`employee_id`),
  UNIQUE KEY `uq_username` (`username`),
  KEY `idx_user_account_employee` (`employee_id`),
  CONSTRAINT `fk_user_account_employee` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`employee_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role` (
  `role_id`     INT          NOT NULL AUTO_INCREMENT,
  `role_name`   VARCHAR(100) NOT NULL,
  `description` VARCHAR(255) NULL,
  `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`  DATETIME     NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by`  INT          NULL,
  `updated_by`  INT          NULL,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `uq_role_name` (`role_name`)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `system_permission`;
CREATE TABLE `system_permission` (
  `permission_id`   INT          NOT NULL AUTO_INCREMENT,
  `permission_name` VARCHAR(100) NOT NULL,
  `description`     VARCHAR(255) NULL,
  `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`      DATETIME     NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by`      INT          NULL,
  `updated_by`      INT          NULL,
  PRIMARY KEY (`permission_id`),
  UNIQUE KEY `uq_permission_name` (`permission_name`)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `account_role`;
CREATE TABLE `account_role` (
  `account_role_id` INT NOT NULL AUTO_INCREMENT,
  `user_account_id` INT NOT NULL,
  `role_id`         INT NOT NULL,
  `created_at`      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`      DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by`      INT NULL,
  `updated_by`      INT NULL,
  PRIMARY KEY (`account_role_id`),
  UNIQUE KEY `uq_account_role` (`user_account_id`,`role_id`),
  KEY `idx_account_role_user` (`user_account_id`),
  KEY `idx_account_role_role` (`role_id`),
  CONSTRAINT `fk_account_role_user` FOREIGN KEY (`user_account_id`) REFERENCES `user_account` (`user_account_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_account_role_role` FOREIGN KEY (`role_id`) REFERENCES `user_role` (`role_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission` (
  `role_permission_id` INT NOT NULL AUTO_INCREMENT,
  `role_id`            INT NOT NULL,
  `permission_id`      INT NOT NULL,
  `created_at`         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`         DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by`         INT NULL,
  `updated_by`         INT NULL,
  PRIMARY KEY (`role_permission_id`),
  UNIQUE KEY `uq_role_permission` (`role_id`,`permission_id`),
  KEY `idx_role_permission_role` (`role_id`),
  KEY `idx_role_permission_permission` (`permission_id`),
  CONSTRAINT `fk_role_permission_role` FOREIGN KEY (`role_id`) REFERENCES `user_role` (`role_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_role_permission_permission` FOREIGN KEY (`permission_id`) REFERENCES `system_permission` (`permission_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `compensation`;
CREATE TABLE `compensation` (
  `compensation_id`          INT           NOT NULL AUTO_INCREMENT,
  `employee_id`              INT           NOT NULL,
  `basic_salary`             DECIMAL(15,2) NOT NULL,
  `rice_subsidy`             DECIMAL(15,2) NOT NULL DEFAULT 0.00,
  `phone_allowance`          DECIMAL(15,2) NOT NULL DEFAULT 0.00,
  `clothing_allowance`       DECIMAL(15,2) NOT NULL DEFAULT 0.00,
  `gross_semi_monthly_rate`  DECIMAL(15,2) NOT NULL,
  `hourly_rate`              DECIMAL(15,2) NOT NULL,
  `effective_date`           DATE          NOT NULL,
  `created_at`               DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`               DATETIME      NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by`               INT           NULL,
  `updated_by`               INT           NULL,
  PRIMARY KEY (`compensation_id`),
  KEY `idx_compensation_employee` (`employee_id`),
  CONSTRAINT `fk_compensation_employee` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`employee_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `attendance`;
CREATE TABLE `attendance` (
  `attendance_id`        INT      NOT NULL AUTO_INCREMENT,
  `employee_id`          INT      NOT NULL,
  `attendance_date`      DATE     NOT NULL,
  `time_in`              TIME     NOT NULL,
  `time_out`             TIME     NULL,
  `attendance_status_id` INT      NULL,
  `created_by`           INT      NULL,
  `updated_by`           INT      NULL,
  `created_at`           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`           DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`attendance_id`),
  UNIQUE KEY `uq_attendance_employee_date` (`employee_id`,`attendance_date`),
  KEY `idx_attendance_employee_date` (`employee_id`,`attendance_date`),
  KEY `idx_attendance_status` (`attendance_status_id`),
  KEY `idx_attendance_created_by` (`created_by`),
  KEY `idx_attendance_updated_by` (`updated_by`),
  CONSTRAINT `fk_attendance_employee` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`employee_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_attendance_status` FOREIGN KEY (`attendance_status_id`) REFERENCES `attendance_status` (`attendance_status_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_attendance_created_by` FOREIGN KEY (`created_by`) REFERENCES `user_account` (`user_account_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_attendance_updated_by` FOREIGN KEY (`updated_by`) REFERENCES `user_account` (`user_account_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `leave_request`;
CREATE TABLE `leave_request` (
  `leave_request_id`  INT      NOT NULL AUTO_INCREMENT,
  `employee_id`       INT      NOT NULL,
  `leave_type_id`     INT      NOT NULL,
  `start_date`        DATE     NOT NULL,
  `end_date`          DATE     NOT NULL,
  `description`       TEXT     NULL,
  `request_status_id` INT      NOT NULL,
  `approved_by`       INT      NULL,
  `approved_at`       DATETIME NULL,
  `remarks`           TEXT     NULL,
  `created_at`        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`        DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by`        INT      NULL,
  `updated_by`        INT      NULL,
  PRIMARY KEY (`leave_request_id`),
  KEY `idx_leave_request_employee` (`employee_id`),
  KEY `idx_leave_request_status` (`request_status_id`),
  KEY `idx_leave_request_type` (`leave_type_id`),
  KEY `idx_leave_request_approved_by` (`approved_by`),
  CONSTRAINT `fk_leave_request_employee` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`employee_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_leave_request_type` FOREIGN KEY (`leave_type_id`) REFERENCES `leave_type` (`leave_type_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_leave_request_status` FOREIGN KEY (`request_status_id`) REFERENCES `request_status` (`request_status_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_leave_request_approved_by` FOREIGN KEY (`approved_by`) REFERENCES `employee` (`employee_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

-- overtime_request and undertime_request merged into work_time_request
-- request_type distinguishes between the two: 'overtime' | 'undertime'
DROP TABLE IF EXISTS `work_time_request`;
CREATE TABLE `work_time_request` (
  `work_time_request_id` INT                        NOT NULL AUTO_INCREMENT,
  `employee_id`          INT                        NOT NULL,
  `request_type`         ENUM('overtime','undertime') NOT NULL COMMENT 'overtime = extra hours worked beyond schedule; undertime = early departure before schedule ends',
  `request_date`         DATE                       NOT NULL,
  `start_time`           TIME                       NOT NULL,
  `end_time`             TIME                       NOT NULL,
  `reason`               TEXT                       NOT NULL,
  `request_status_id`    INT                        NOT NULL,
  `approved_by`          INT                        NULL,
  `approved_at`          DATETIME                   NULL,
  `remarks`              TEXT                       NULL,
  `created_at`           DATETIME                   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`           DATETIME                   NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by`           INT                        NULL,
  `updated_by`           INT                        NULL,
  PRIMARY KEY (`work_time_request_id`),
  KEY `idx_work_time_request_employee` (`employee_id`),
  KEY `idx_work_time_request_type` (`request_type`),
  KEY `idx_work_time_request_status` (`request_status_id`),
  KEY `idx_work_time_request_approved_by` (`approved_by`),
  CONSTRAINT `fk_work_time_request_employee` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`employee_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_work_time_request_status` FOREIGN KEY (`request_status_id`) REFERENCES `request_status` (`request_status_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_work_time_request_approved_by` FOREIGN KEY (`approved_by`) REFERENCES `employee` (`employee_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `pay_period`;
CREATE TABLE `pay_period` (
  `pay_period_id`     INT      NOT NULL AUTO_INCREMENT,
  `period_start_date` DATE     NOT NULL,
  `period_end_date`   DATE     NOT NULL,
  `pay_date`          DATE     NOT NULL,
  `payroll_status_id` INT      NOT NULL,
  `created_at`        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`        DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by`        INT      NULL,
  `updated_by`        INT      NULL,
  PRIMARY KEY (`pay_period_id`),
  UNIQUE KEY `uq_pay_period_dates` (`period_start_date`,`period_end_date`),
  KEY `idx_pay_period_status` (`payroll_status_id`),
  CONSTRAINT `fk_pay_period_status` FOREIGN KEY (`payroll_status_id`) REFERENCES `payroll_status` (`payroll_status_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `payroll`;
CREATE TABLE `payroll` (
  `payroll_id`        INT           NOT NULL AUTO_INCREMENT,
  `employee_id`       INT           NOT NULL,
  `pay_period_id`     INT           NOT NULL,
  `payroll_status_id` INT           NOT NULL,
  `basic_salary`      DECIMAL(15,2) NOT NULL,
  `hourly_rate`       DECIMAL(15,2) NOT NULL,
  `hours_worked`      DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  `basic_pay`         DECIMAL(15,2) NOT NULL,
  `overtime_pay`      DECIMAL(15,2) NOT NULL DEFAULT 0.00,
  `gross_pay`         DECIMAL(15,2) NOT NULL,
  `total_benefits`    DECIMAL(15,2) NOT NULL DEFAULT 0.00,
  `total_deductions`  DECIMAL(15,2) NOT NULL DEFAULT 0.00,
  `net_pay`           DECIMAL(15,2) NOT NULL,
  `created_at`        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`        DATETIME      NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by`        INT           NULL,
  `updated_by`        INT           NULL,
  PRIMARY KEY (`payroll_id`),
  UNIQUE KEY `uq_payroll_employee_period` (`employee_id`,`pay_period_id`),
  KEY `idx_payroll_employee_period` (`employee_id`,`pay_period_id`),
  KEY `idx_payroll_pay_period` (`pay_period_id`),
  KEY `idx_payroll_status` (`payroll_status_id`),
  CONSTRAINT `fk_payroll_employee` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`employee_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_payroll_pay_period` FOREIGN KEY (`pay_period_id`) REFERENCES `pay_period` (`pay_period_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_payroll_status` FOREIGN KEY (`payroll_status_id`) REFERENCES `payroll_status` (`payroll_status_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `payroll_benefit`;
CREATE TABLE `payroll_benefit` (
  `payroll_benefit_id` INT           NOT NULL AUTO_INCREMENT,
  `payroll_id`         INT           NOT NULL,
  `benefit_type_id`    INT           NOT NULL,
  `amount`             DECIMAL(15,2) NOT NULL,
  `created_at`         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`         DATETIME      NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by`         INT           NULL,
  `updated_by`         INT           NULL,
  PRIMARY KEY (`payroll_benefit_id`),
  UNIQUE KEY `uq_payroll_benefit` (`payroll_id`,`benefit_type_id`),
  KEY `idx_payroll_benefit_payroll` (`payroll_id`),
  KEY `idx_payroll_benefit_type` (`benefit_type_id`),
  CONSTRAINT `fk_payroll_benefit_payroll` FOREIGN KEY (`payroll_id`) REFERENCES `payroll` (`payroll_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_payroll_benefit_type` FOREIGN KEY (`benefit_type_id`) REFERENCES `benefit_type` (`benefit_type_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `payroll_deduction`;
CREATE TABLE `payroll_deduction` (
  `payroll_deduction_id` INT           NOT NULL AUTO_INCREMENT,
  `payroll_id`           INT           NOT NULL,
  `deduction_type_id`    INT           NOT NULL,
  `amount`               DECIMAL(15,2) NOT NULL,
  `created_at`           DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`           DATETIME      NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by`           INT           NULL,
  `updated_by`           INT           NULL,
  PRIMARY KEY (`payroll_deduction_id`),
  UNIQUE KEY `uq_payroll_deduction` (`payroll_id`,`deduction_type_id`),
  KEY `idx_payroll_deduction_payroll` (`payroll_id`),
  KEY `idx_payroll_deduction_type` (`deduction_type_id`),
  CONSTRAINT `fk_payroll_deduction_payroll` FOREIGN KEY (`payroll_id`) REFERENCES `payroll` (`payroll_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_payroll_deduction_type` FOREIGN KEY (`deduction_type_id`) REFERENCES `deduction_type` (`deduction_type_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `payslip`;
CREATE TABLE `payslip` (
  `payslip_id`     INT         NOT NULL AUTO_INCREMENT,
  `payroll_id`     INT         NOT NULL,
  `payslip_number` VARCHAR(50) NOT NULL,
  `generated_at`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'when the physical PDF document was generated/saved',
  `generated_by`   INT         NULL COMMENT 'employee_id of who triggered the PDF generation',
  `created_at`     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'when the payslip record was created in the system',
  `updated_at`     DATETIME    NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by`     INT         NULL,
  `updated_by`     INT         NULL,
  PRIMARY KEY (`payslip_id`),
  UNIQUE KEY `uq_payslip_payroll` (`payroll_id`),
  UNIQUE KEY `uq_payslip_number` (`payslip_number`),
  CONSTRAINT `fk_payslip_payroll` FOREIGN KEY (`payroll_id`) REFERENCES `payroll` (`payroll_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `help_center_ticket`;
CREATE TABLE `help_center_ticket` (
  `ticket_id`               INT      NOT NULL AUTO_INCREMENT,
  `employee_id`             INT      NOT NULL,
  `help_center_category_id` INT      NOT NULL,
  `issue`                   TEXT     NOT NULL,
  `request_status_id`       INT      NOT NULL,
  `created_at`              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`              DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by`              INT      NULL,
  `updated_by`              INT      NULL,
  PRIMARY KEY (`ticket_id`),
  KEY `idx_ticket_employee` (`employee_id`),
  KEY `idx_ticket_category` (`help_center_category_id`),
  KEY `idx_ticket_status` (`request_status_id`),
  CONSTRAINT `fk_ticket_employee` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`employee_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_ticket_category` FOREIGN KEY (`help_center_category_id`) REFERENCES `help_center_category` (`help_center_category_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_ticket_status` FOREIGN KEY (`request_status_id`) REFERENCES `request_status` (`request_status_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `approval`;
CREATE TABLE `approval` (
  `approval_id`       INT         NOT NULL AUTO_INCREMENT,
  `request_type`      VARCHAR(50) NOT NULL COMMENT 'leave_request | work_time_request',
  `request_id`        INT         NOT NULL,
  `approver_id`       INT         NOT NULL,
  `request_status_id` INT         NOT NULL,
  `remarks`           TEXT        NULL,
  `approved_at`       DATETIME    NULL,
  `created_at`        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`        DATETIME    NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by`        INT         NULL,
  `updated_by`        INT         NULL,
  PRIMARY KEY (`approval_id`),
  KEY `idx_approval_request` (`request_type`,`request_id`),
  KEY `idx_approval_approver` (`approver_id`),
  KEY `idx_approval_status` (`request_status_id`),
  CONSTRAINT `fk_approval_approver` FOREIGN KEY (`approver_id`) REFERENCES `employee` (`employee_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_approval_status` FOREIGN KEY (`request_status_id`) REFERENCES `request_status` (`request_status_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `action_log`;
CREATE TABLE `action_log` (
  `action_log_id`   INT          NOT NULL AUTO_INCREMENT,
  `user_account_id` INT          NOT NULL,
  `action_name`     VARCHAR(255) NOT NULL,
  `table_name`      VARCHAR(100) NOT NULL,
  `record_id`       INT          NOT NULL,
  `old_value`       TEXT         NULL,
  `new_value`       TEXT         NULL,
  `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by`      INT          NULL,
  `updated_by`      INT          NULL,
  PRIMARY KEY (`action_log_id`),
  KEY `idx_action_log_user` (`user_account_id`),
  KEY `idx_action_log_table_record` (`table_name`,`record_id`),
  CONSTRAINT `fk_action_log_user` FOREIGN KEY (`user_account_id`) REFERENCES `user_account` (`user_account_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =============================================================================
-- PART 2: SEED DATA (real employee_id 10001..10034, audit fields populated)
-- =============================================================================

SET FOREIGN_KEY_CHECKS=0;

-- 1. Departments
INSERT INTO `department` (`department_id`, `department_name`, `created_at`, `created_by`) VALUES
(1, 'Executive', NOW(), NULL),
(2, 'Human Resources', NOW(), NULL),
(3, 'Information Technology', NOW(), NULL),
(4, 'Finance & Payroll', NOW(), NULL),
(5, 'Sales & Account Management', NOW(), NULL),
(6, 'Operations & Support', NOW(), NULL);

-- 2. Positions
INSERT INTO `employee_position` (`position_id`, `position_name`, `department_id`, `created_at`, `created_by`) VALUES
(1,'Account Manager',5, NOW(), NULL),
(2,'Account Rank and File',5, NOW(), NULL),
(3,'Account Team Leader',5, NOW(), NULL),
(4,'Accounting Head',4, NOW(), NULL),
(5,'Chief Executive Officer',1, NOW(), NULL),
(6,'Chief Finance Officer',1, NOW(), NULL),
(7,'Chief Marketing Officer',1, NOW(), NULL),
(8,'Chief Operating Officer',1, NOW(), NULL),
(9,'Customer Service and Relations',6, NOW(), NULL),
(10,'HR Manager',2, NOW(), NULL),
(11,'HR Rank and File',2, NOW(), NULL),
(12,'HR Team Leader',2, NOW(), NULL),
(13,'IT Operations and Systems',3, NOW(), NULL),
(14,'Payroll Manager',4, NOW(), NULL),
(15,'Payroll Rank and File',4, NOW(), NULL),
(16,'Payroll Team Leader',4, NOW(), NULL),
(17,'Sales & Marketing',5, NOW(), NULL),
(18,'Supply Chain and Logistics',6, NOW(), NULL);

-- 3. Employment Statuses
INSERT INTO `employment_status` (`employment_status_id`, `status_type`, `created_at`, `created_by`) VALUES
(1,'Probationary', NOW(), NULL),
(2,'Regular', NOW(), NULL);

-- 4. Request Statuses
INSERT INTO `request_status` (`request_status_id`, `request_status_type`, `created_at`, `created_by`) VALUES
(1,'Pending', NOW(), NULL),
(2,'Approved', NOW(), NULL),
(3,'Rejected', NOW(), NULL),
(4,'Cancelled', NOW(), NULL),
(5,'Open', NOW(), NULL),
(6,'Resolved', NOW(), NULL);

-- 5. Payroll Statuses
INSERT INTO `payroll_status` (`payroll_status_id`, `status_name`, `created_at`, `created_by`) VALUES
(1,'Draft', NOW(), NULL),
(2,'Processing', NOW(), NULL),
(3,'Approved', NOW(), NULL),
(4,'Paid', NOW(), NULL),
(5,'Cancelled', NOW(), NULL);

-- 6. Attendance Statuses
INSERT INTO `attendance_status` (`attendance_status_id`, `status_name`, `created_at`, `created_by`) VALUES
(1,'Present', NOW(), NULL),
(2,'Absent', NOW(), NULL),
(3,'Late', NOW(), NULL),
(4,'Half Day', NOW(), NULL),
(5,'On Leave', NOW(), NULL);

-- 7. Leave Types
INSERT INTO `leave_type` (`leave_type_id`, `leave_type_name`, `created_at`, `created_by`) VALUES
(1,'Vacation Leave', NOW(), NULL),
(2,'Sick Leave', NOW(), NULL),
(3,'Emergency Leave', NOW(), NULL),
(4,'Maternity Leave', NOW(), NULL),
(5,'Paternity Leave', NOW(), NULL),
(6,'Solo Parent Leave', NOW(), NULL),
(7,'Unpaid Leave', NOW(), NULL);

-- 8. Government ID Types
INSERT INTO `government_id_type` (`government_id_type_id`, `id_type`, `format_pattern`, `created_at`, `created_by`) VALUES
(1,'SSS','XX-XXXXXXX-X', NOW(), NULL),
(2,'PhilHealth','XXXXXXXXXXXX', NOW(), NULL),
(3,'TIN','XXX-XXX-XXX-XXX', NOW(), NULL),
(4,'Pag-IBIG','XXXXXXXXXXXX', NOW(), NULL);

-- 9. Benefit Types
INSERT INTO `benefit_type` (`benefit_type_id`, `benefit_name`, `created_at`, `created_by`) VALUES
(1,'Rice Subsidy', NOW(), NULL),
(2,'Phone Allowance', NOW(), NULL),
(3,'Clothing Allowance', NOW(), NULL);

-- 10. Deduction Types
INSERT INTO `deduction_type` (`deduction_type_id`, `deduction_name`, `created_at`, `created_by`) VALUES
(1,'SSS Contribution', NOW(), NULL),
(2,'PhilHealth Contribution', NOW(), NULL),
(3,'Pag-IBIG Contribution', NOW(), NULL),
(4,'Withholding Tax', NOW(), NULL);

-- 11. Help Center Categories
INSERT INTO `help_center_category` (`help_center_category_id`, `category_name`, `created_at`, `created_by`) VALUES
(1,'Payroll Inquiry', NOW(), NULL),
(2,'Leave Request', NOW(), NULL),
(3,'Attendance Correction', NOW(), NULL),
(4,'Benefits & Deductions', NOW(), NULL),
(5,'System Access', NOW(), NULL),
(6,'General Inquiry', NOW(), NULL);

-- 12. Employees (real IDs)
INSERT INTO `employee` (`employee_id`, `first_name`, `last_name`, `birthdate`, `phone_number`, `email`, `position_id`, `immediate_supervisor_id`, `employment_status_id`, `created_at`, `created_by`) VALUES
(10001, 'Manuel III',   'Garcia',      '1983-10-11', '966-860-270', 'manueliii.garcia@motorph.com',     5,  NULL, 2, NOW(), NULL),
(10002, 'Antonio',      'Lim',         '1988-06-19', '171-867-411', 'antonio.lim@motorph.com',          8,  10001, 2, NOW(), NULL),
(10003, 'Bianca Sofia', 'Aquino',      '1989-08-04', '966-889-370', 'biancasofia.aquino@motorph.com',   6,  10001, 2, NOW(), NULL),
(10004, 'Isabella',     'Reyes',       '1994-06-16', '786-868-477', 'isabella.reyes@motorph.com',       7,  10001, 2, NOW(), NULL),
(10005, 'Eduard',       'Hernandez',   '1989-09-23', '088-861-012', 'eduard.hernandez@motorph.com',     13, 10002, 2, NOW(), NULL),
(10006, 'Andrea Mae',   'Villanueva',  '1988-02-14', '918-621-603', 'andreamae.villanueva@motorph.com', 10, 10002, 2, NOW(), NULL),
(10007, 'Brad',         'San Jose',    '1996-03-15', '797-009-261', 'brad.sanjose@motorph.com',         12, 10006, 2, NOW(), NULL),
(10008, 'Alice',        'Romualdez',   '1992-05-14', '983-606-799', 'alice.romualdez@motorph.com',      11, NULL, 2, NOW(), NULL),
(10009, 'Rosie',        'Atienza',     '1948-09-24', '266-036-427', 'rosie.atienza@motorph.com',        11, NULL, 2, NOW(), NULL),
(10010, 'Roderick',     'Alvaro',      '1988-03-30', '053-381-386', 'roderick.alvaro@motorph.com',      4,  10003, 2, NOW(), NULL),
(10011, 'Anthony',      'Salcedo',     '1993-09-14', '070-766-300', 'anthony.salcedo@motorph.com',      14, 10010, 2, NOW(), NULL),
(10012, 'Josie',        'Lopez',       '1987-01-14', '478-355-427', 'josie.lopez@motorph.com',          16, 10011, 2, NOW(), NULL),
(10013, 'Martha',       'Farala',      '1942-01-11', '329-034-366', 'martha.farala@motorph.com',        15, 10011, 2, NOW(), NULL),
(10014, 'Leila',        'Martinez',    '1970-07-11', '877-110-749', 'leila.martinez@motorph.com',       15, 10011, 2, NOW(), NULL),
(10015, 'Fredrick',     'Romualdez',   '1985-03-10', '023-079-009', 'fredrick.romualdez@motorph.com',   1,  NULL, 2, NOW(), NULL),
(10016, 'Christian',    'Mata',        '1987-10-21', '783-776-744', 'christian.mata@motorph.com',       3,  10015, 2, NOW(), NULL),
(10017, 'Selena',       'De Leon',     '1975-02-20', '975-432-139', 'selena.deleon@motorph.com',        3,  10015, 2, NOW(), NULL),
(10018, 'Allison',      'San Jose',    '1986-06-24', '179-075-129', 'allison.sanjose@motorph.com',      2,  10016, 2, NOW(), NULL),
(10019, 'Cydney',       'Rosario',     '1996-10-06', '868-819-912', 'cydney.rosario@motorph.com',       2,  10016, 2, NOW(), NULL),
(10020, 'Mark',         'Bautista',    '1991-02-12', '683-725-348', 'mark.bautista@motorph.com',        2,  10016, 2, NOW(), NULL),
(10021, 'Darlene',      'Lazaro',      '1985-11-25', '740-721-558', 'darlene.lazaro@motorph.com',       2,  10016, 2, NOW(), NULL),
(10022, 'Kolby',        'Delos Santos','1980-02-26', '739-443-033', 'kolby.delossantos@motorph.com',    2,  10017, 2, NOW(), NULL),
(10023, 'Vella',        'Santos',      '1983-12-31', '955-879-269', 'vella.santos@motorph.com',         2,  10017, 2, NOW(), NULL),
(10024, 'Tomas',        'Del Rosario', '1978-12-18', '882-550-989', 'tomas.delrosario@motorph.com',     2,  10017, 2, NOW(), NULL),
(10025, 'Jacklyn',      'Tolentino',   '1984-05-19', '675-757-366', 'jacklyn.tolentino@motorph.com',    2,  10017, 2, NOW(), NULL),
(10026, 'Percival',     'Gutierrez',   '1970-12-18', '512-899-876', 'percival.gutierrez@motorph.com',   2,  10017, 2, NOW(), NULL),
(10027, 'Garfield',     'Manalaysay',  '1986-08-28', '948-628-136', 'garfield.manalaysay@motorph.com',  2,  10017, 2, NOW(), NULL),
(10028, 'Lizeth',       'Villegas',    '1981-12-12', '332-372-215', 'lizeth.villegas@motorph.com',      2,  10017, 2, NOW(), NULL),
(10029, 'Carol',        'Ramos',       '1978-08-20', '250-700-389', 'carol.ramos@motorph.com',          2,  10017, 1, NOW(), NULL),
(10030, 'Emelia',       'Maceda',      '1973-04-14', '973-358-041', 'emelia.maceda@motorph.com',        2,  10017, 1, NOW(), NULL),
(10031, 'Delia',        'Aguilar',     '1989-01-27', '529-705-439', 'delia.aguilar@motorph.com',        2,  10017, 1, NOW(), NULL),
(10032, 'John Rafael',  'Castro',      '1992-02-09', '332-424-955', 'johnrafael.castro@motorph.com',    18, 10002, 2, NOW(), NULL),
(10033, 'Carlos Ian',   'Martinez',    '1990-11-16', '078-854-208', 'carlosian.martinez@motorph.com',   18, 10032, 2, NOW(), NULL),
(10034, 'Beatriz',      'Santos',      '1990-08-07', '526-639-511', 'beatriz.santos@motorph.com',       13, 10012, 2, NOW(), NULL);

-- 13. Addresses
INSERT INTO `address` (`address_id`, `full_address`, `created_at`, `created_by`) VALUES
(1,'Valero Carpark Building Valero Street 1227, Makati City', NOW(), NULL),
(2,'San Antonio De Padua 2, Block 1 Lot 8 and 2, Dasmarinas, Cavite', NOW(), NULL),
(3,'Rm. 402 4/F Jiao Building Timog Avenue Cor. Quezon Avenue 1100, Quezon City', NOW(), NULL),
(4,'460 Solanda Street Intramuros 1000, Manila', NOW(), NULL),
(5,'National Highway, Gingoog, Misamis Occidental', NOW(), NULL),
(6,'17/85 Stracke Via Suite 042, Poblacion, Las Piñas 4783 Dinagat Islands', NOW(), NULL),
(7,'99 Strosin Hills, Poblacion, Bislig 5340 Tawi-Tawi', NOW(), NULL),
(8,'12A/33 Upton Isle Apt. 420, Roxas City 1814 Surigao del Norte', NOW(), NULL),
(9,'90A Dibbert Terrace Apt. 190, San Lorenzo 6056 Davao del Norte', NOW(), NULL),
(10,'#284 T. Morato corner, Scout Rallos Street, Quezon City', NOW(), NULL),
(11,'93/54 Shanahan Alley Apt. 183, Santo Tomas 1572 Masbate', NOW(), NULL),
(12,'49 Springs Apt. 266, Poblacion, Taguig 3200 Occidental Mindoro', NOW(), NULL),
(13,'42/25 Sawayn Stream, Ubay 1208 Zamboanga del Norte', NOW(), NULL),
(14,'37/46 Kulas Roads, Maragondon 0962 Quirino', NOW(), NULL),
(15,'22A/52 Lubowitz Meadows, Pililla 4895 Zambales', NOW(), NULL),
(16,'90 O''Keefe Spur Apt. 379, Catigbian 2772 Sulu', NOW(), NULL),
(17,'89A Armstrong Trace, Compostela 7874 Maguindanao', NOW(), NULL),
(18,'08 Grant Drive Suite 406, Poblacion, Iloilo City 9186 La Union', NOW(), NULL),
(19,'93A/21 Berge Points, Tapaz 2180 Quezon', NOW(), NULL),
(20,'65 Murphy Center Suite 094, Poblacion, Palayan 5636 Quirino', NOW(), NULL),
(21,'47A/94 Larkin Plaza Apt. 179, Poblacion, Caloocan 2751 Quirino', NOW(), NULL),
(22,'06A Gulgowski Extensions, Bongabon 6085 Zamboanga del Sur', NOW(), NULL),
(23,'99A Padberg Spring, Poblacion, Mabalacat 3959 Lanao del Sur', NOW(), NULL),
(24,'80A/48 Ledner Ridges, Poblacion, Kabankalan 8870 Marinduque', NOW(), NULL),
(25,'96/48 Watsica Flats Suite 734, Poblacion, Malolos 1844 Ifugao', NOW(), NULL),
(26,'58A Wilderman Walks, Poblacion, Digos 5822 Davao del Sur', NOW(), NULL),
(27,'60 Goyette Valley Suite 219, Poblacion, Tabuk 3159 Lanao del Sur', NOW(), NULL),
(28,'66/77 Mann Views, Luisiana 1263 Dinagat Islands', NOW(), NULL),
(29,'72/70 Stamm Spurs, Bustos 4550 Iloilo', NOW(), NULL),
(30,'50A/83 Bahringer Oval Suite 145, Kiamba 7688 Nueva Ecija', NOW(), NULL),
(31,'95 Cremin Junction, Surallah 2809 Cotabato', NOW(), NULL),
(32,'Hi-way, Yati, Liloan Cebu', NOW(), NULL),
(33,'Bulala, Camalaniugan', NOW(), NULL),
(34,'Agapita Building, Metro Manila', NOW(), NULL);

-- 14. Employee Addresses
INSERT INTO `employee_address` (`employee_id`, `address_id`, `address_type`, `created_at`, `created_by`) VALUES
(10001,1,'primary', NOW(), NULL),
(10002,2,'primary', NOW(), NULL),
(10003,3,'primary', NOW(), NULL),
(10004,4,'primary', NOW(), NULL),
(10005,5,'primary', NOW(), NULL),
(10006,6,'primary', NOW(), NULL),
(10007,7,'primary', NOW(), NULL),
(10008,8,'primary', NOW(), NULL),
(10009,9,'primary', NOW(), NULL),
(10010,10,'primary', NOW(), NULL),
(10011,11,'primary', NOW(), NULL),
(10012,12,'primary', NOW(), NULL),
(10013,13,'primary', NOW(), NULL),
(10014,14,'primary', NOW(), NULL),
(10015,15,'primary', NOW(), NULL),
(10016,16,'primary', NOW(), NULL),
(10017,17,'primary', NOW(), NULL),
(10018,18,'primary', NOW(), NULL),
(10019,19,'primary', NOW(), NULL),
(10020,20,'primary', NOW(), NULL),
(10021,21,'primary', NOW(), NULL),
(10022,22,'primary', NOW(), NULL),
(10023,23,'primary', NOW(), NULL),
(10024,24,'primary', NOW(), NULL),
(10025,25,'primary', NOW(), NULL),
(10026,26,'primary', NOW(), NULL),
(10027,27,'primary', NOW(), NULL),
(10028,28,'primary', NOW(), NULL),
(10029,29,'primary', NOW(), NULL),
(10030,30,'primary', NOW(), NULL),
(10031,31,'primary', NOW(), NULL),
(10032,32,'primary', NOW(), NULL),
(10033,33,'primary', NOW(), NULL),
(10034,34,'primary', NOW(), NULL);

-- 15. Employee Government IDs
INSERT INTO `employee_government_id` (`employee_id`, `government_id_type_id`, `id_number`, `created_at`, `created_by`) VALUES
(10001,1,'44-4506057-3', NOW(), NULL),
(10001,2,'820126853951', NOW(), NULL),
(10001,3,'442-605-657-000', NOW(), NULL),
(10001,4,'691295330870', NOW(), NULL),
(10002,1,'52-2061274-9', NOW(), NULL),
(10002,2,'331735646338', NOW(), NULL),
(10002,3,'683-102-776-000', NOW(), NULL),
(10002,4,'663904995411', NOW(), NULL),
(10003,1,'30-8870406-2', NOW(), NULL),
(10003,2,'177451189665', NOW(), NULL),
(10003,3,'971-711-280-000', NOW(), NULL),
(10003,4,'171519773969', NOW(), NULL),
(10004,1,'40-2511815-0', NOW(), NULL),
(10004,2,'341911411254', NOW(), NULL),
(10004,3,'876-809-437-000', NOW(), NULL),
(10004,4,'416946776041', NOW(), NULL),
(10005,1,'50-5577638-1', NOW(), NULL),
(10005,2,'957436191812', NOW(), NULL),
(10005,3,'031-702-374-000', NOW(), NULL),
(10005,4,'952347222457', NOW(), NULL),
(10006,1,'49-1632020-8', NOW(), NULL),
(10006,2,'382189453145', NOW(), NULL),
(10006,3,'317-674-022-000', NOW(), NULL),
(10006,4,'441093369646', NOW(), NULL),
(10007,1,'40-2400714-1', NOW(), NULL),
(10007,2,'239192926939', NOW(), NULL),
(10007,3,'672-474-690-000', NOW(), NULL),
(10007,4,'210850209964', NOW(), NULL),
(10008,1,'55-4476527-2', NOW(), NULL),
(10008,2,'545652640232', NOW(), NULL),
(10008,3,'888-572-294-000', NOW(), NULL),
(10008,4,'211385556888', NOW(), NULL),
(10009,1,'41-0644692-3', NOW(), NULL),
(10009,2,'708988234853', NOW(), NULL),
(10009,3,'604-997-793-000', NOW(), NULL),
(10009,4,'260107732354', NOW(), NULL),
(10010,1,'64-7605054-4', NOW(), NULL),
(10010,2,'578114853194', NOW(), NULL),
(10010,3,'525-420-419-000', NOW(), NULL),
(10010,4,'799254095212', NOW(), NULL),
(10011,1,'26-9647608-3', NOW(), NULL),
(10011,2,'126445315651', NOW(), NULL),
(10011,3,'210-805-911-000', NOW(), NULL),
(10011,4,'218002473454', NOW(), NULL),
(10012,1,'44-8563448-3', NOW(), NULL),
(10012,2,'431709011012', NOW(), NULL),
(10012,3,'218-489-737-000', NOW(), NULL),
(10012,4,'113071293354', NOW(), NULL),
(10013,1,'45-5656375-0', NOW(), NULL),
(10013,2,'233693897247', NOW(), NULL),
(10013,3,'210-835-851-000', NOW(), NULL),
(10013,4,'631130283546', NOW(), NULL),
(10014,1,'27-2090996-4', NOW(), NULL),
(10014,2,'515741057496', NOW(), NULL),
(10014,3,'275-792-513-000', NOW(), NULL),
(10014,4,'101205445886', NOW(), NULL),
(10015,1,'26-8768374-1', NOW(), NULL),
(10015,2,'308366860059', NOW(), NULL),
(10015,3,'598-065-761-000', NOW(), NULL),
(10015,4,'223057707853', NOW(), NULL),
(10016,1,'49-2959312-6', NOW(), NULL),
(10016,2,'824187961962', NOW(), NULL),
(10016,3,'103-100-522-000', NOW(), NULL),
(10016,4,'631052853464', NOW(), NULL),
(10017,1,'27-2090208-8', NOW(), NULL),
(10017,2,'587272469938', NOW(), NULL),
(10017,3,'482-259-498-000', NOW(), NULL),
(10017,4,'719007608464', NOW(), NULL),
(10018,1,'45-3251383-0', NOW(), NULL),
(10018,2,'745148459521', NOW(), NULL),
(10018,3,'121-203-336-000', NOW(), NULL),
(10018,4,'114901859343', NOW(), NULL),
(10019,1,'49-1629900-2', NOW(), NULL),
(10019,2,'579253435499', NOW(), NULL),
(10019,3,'122-244-511-000', NOW(), NULL),
(10019,4,'265104358643', NOW(), NULL),
(10020,1,'49-1647342-5', NOW(), NULL),
(10020,2,'399665157135', NOW(), NULL),
(10020,3,'273-970-941-000', NOW(), NULL),
(10020,4,'260054585575', NOW(), NULL),
(10021,1,'45-5617168-2', NOW(), NULL),
(10021,2,'606386917510', NOW(), NULL),
(10021,3,'354-650-951-000', NOW(), NULL),
(10021,4,'104907708845', NOW(), NULL),
(10022,1,'52-0109570-6', NOW(), NULL),
(10022,2,'357451271274', NOW(), NULL),
(10022,3,'187-500-345-000', NOW(), NULL),
(10022,4,'113017988667', NOW(), NULL),
(10023,1,'52-9883524-3', NOW(), NULL),
(10023,2,'548670482885', NOW(), NULL),
(10023,3,'101-558-994-000', NOW(), NULL),
(10023,4,'360028104576', NOW(), NULL),
(10024,1,'45-5866331-6', NOW(), NULL),
(10024,2,'953901539995', NOW(), NULL),
(10024,3,'560-735-732-000', NOW(), NULL),
(10024,4,'913108649964', NOW(), NULL),
(10025,1,'47-1692793-0', NOW(), NULL),
(10025,2,'753800654114', NOW(), NULL),
(10025,3,'841-177-857-000', NOW(), NULL),
(10025,4,'210546661243', NOW(), NULL),
(10026,1,'40-9504657-8', NOW(), NULL),
(10026,2,'797639382265', NOW(), NULL),
(10026,3,'502-995-671-000', NOW(), NULL),
(10026,4,'210897095686', NOW(), NULL),
(10027,1,'45-3298166-4', NOW(), NULL),
(10027,2,'810909286264', NOW(), NULL),
(10027,3,'336-676-445-000', NOW(), NULL),
(10027,4,'211274476563', NOW(), NULL),
(10028,1,'40-2400719-4', NOW(), NULL),
(10028,2,'934389652994', NOW(), NULL),
(10028,3,'210-395-397-000', NOW(), NULL),
(10028,4,'122238077997', NOW(), NULL),
(10029,1,'60-1152206-4', NOW(), NULL),
(10029,2,'351830469744', NOW(), NULL),
(10029,3,'395-032-717-000', NOW(), NULL),
(10029,4,'212141893454', NOW(), NULL),
(10030,1,'54-1331005-0', NOW(), NULL),
(10030,2,'465087894112', NOW(), NULL),
(10030,3,'215-973-013-000', NOW(), NULL),
(10030,4,'515012579765', NOW(), NULL),
(10031,1,'52-1859253-1', NOW(), NULL),
(10031,2,'136451303068', NOW(), NULL),
(10031,3,'599-312-588-000', NOW(), NULL),
(10031,4,'110018813465', NOW(), NULL),
(10032,1,'26-7145133-4', NOW(), NULL),
(10032,2,'601644902402', NOW(), NULL),
(10032,3,'404-768-309-000', NOW(), NULL),
(10032,4,'697764069311', NOW(), NULL),
(10033,1,'11-5062972-7', NOW(), NULL),
(10033,2,'380685387212', NOW(), NULL),
(10033,3,'256-436-296-000', NOW(), NULL),
(10033,4,'993372963726', NOW(), NULL),
(10034,1,'20-2987501-5', NOW(), NULL),
(10034,2,'918460050077', NOW(), NULL),
(10034,3,'911-529-713-000', NOW(), NULL),
(10034,4,'874042259378', NOW(), NULL);

-- 16. Compensation
INSERT INTO `compensation` (`employee_id`, `basic_salary`, `rice_subsidy`, `phone_allowance`, `clothing_allowance`, `gross_semi_monthly_rate`, `hourly_rate`, `effective_date`, `created_at`, `created_by`) VALUES
(10001, 90000, 1500, 2000, 1000, 45000, 535.71, '2024-01-01', NOW(), NULL),
(10002, 60000, 1500, 2000, 1000, 30000, 357.14, '2024-01-01', NOW(), NULL),
(10003, 60000, 1500, 2000, 1000, 30000, 357.14, '2024-01-01', NOW(), NULL),
(10004, 60000, 1500, 2000, 1000, 30000, 357.14, '2024-01-01', NOW(), NULL),
(10005, 52670, 1500, 1000, 1000, 26335, 313.51, '2024-01-01', NOW(), NULL),
(10006, 52670, 1500, 1000, 1000, 26335, 313.51, '2024-01-01', NOW(), NULL),
(10007, 42975, 1500, 800, 800, 21488, 255.80, '2024-01-01', NOW(), NULL),
(10008, 22500, 1500, 500, 500, 11250, 133.93, '2024-01-01', NOW(), NULL),
(10009, 22500, 1500, 500, 500, 11250, 133.93, '2024-01-01', NOW(), NULL),
(10010, 52670, 1500, 1000, 1000, 26335, 313.51, '2024-01-01', NOW(), NULL),
(10011, 50825, 1500, 1000, 1000, 25413, 302.53, '2024-01-01', NOW(), NULL),
(10012, 38475, 1500, 800, 800, 19238, 229.02, '2024-01-01', NOW(), NULL),
(10013, 24000, 1500, 500, 500, 12000, 142.86, '2024-01-01', NOW(), NULL),
(10014, 24000, 1500, 500, 500, 12000, 142.86, '2024-01-01', NOW(), NULL),
(10015, 53500, 1500, 1000, 1000, 26750, 318.45, '2024-01-01', NOW(), NULL),
(10016, 42975, 1500, 800, 800, 21488, 255.80, '2024-01-01', NOW(), NULL),
(10017, 41850, 1500, 800, 800, 20925, 249.11, '2024-01-01', NOW(), NULL),
(10018, 22500, 1500, 500, 500, 11250, 133.93, '2024-01-01', NOW(), NULL),
(10019, 22500, 1500, 500, 500, 11250, 133.93, '2024-01-01', NOW(), NULL),
(10020, 23250, 1500, 500, 500, 11625, 138.39, '2024-01-01', NOW(), NULL),
(10021, 23250, 1500, 500, 500, 11625, 138.39, '2024-01-01', NOW(), NULL),
(10022, 24000, 1500, 500, 500, 12000, 142.86, '2024-01-01', NOW(), NULL),
(10023, 22500, 1500, 500, 500, 11250, 133.93, '2024-01-01', NOW(), NULL),
(10024, 22500, 1500, 500, 500, 11250, 133.93, '2024-01-01', NOW(), NULL),
(10025, 24000, 1500, 500, 500, 12000, 142.86, '2024-01-01', NOW(), NULL),
(10026, 24750, 1500, 500, 500, 12375, 147.32, '2024-01-01', NOW(), NULL),
(10027, 24750, 1500, 500, 500, 12375, 147.32, '2024-01-01', NOW(), NULL),
(10028, 24000, 1500, 500, 500, 12000, 142.86, '2024-01-01', NOW(), NULL),
(10029, 22500, 1500, 500, 500, 11250, 133.93, '2024-01-01', NOW(), NULL),
(10030, 22500, 1500, 500, 500, 11250, 133.93, '2024-01-01', NOW(), NULL),
(10031, 22500, 1500, 500, 500, 11250, 133.93, '2024-01-01', NOW(), NULL),
(10032, 52670, 1500, 1000, 1000, 26335, 313.51, '2024-01-01', NOW(), NULL),
(10033, 52670, 1500, 1000, 1000, 26335, 313.51, '2024-01-01', NOW(), NULL),
(10034, 52670, 1500, 1000, 1000, 26335, 313.51, '2024-01-01', NOW(), NULL);

-- 17. Attendance Records (replace with your full data)
-- Example:
INSERT INTO `attendance` (`employee_id`, `attendance_date`, `time_in`, `time_out`, `created_at`, `created_by`) VALUES
(10001, '2024-06-03', '08:59:00', '18:31:00', NOW(), NULL),
(10002, '2024-06-03', '10:35:00', '19:44:00', NOW(), NULL);
-- (Add all your attendance rows here, using the same pattern)

-- 18. User Roles & Permissions
INSERT INTO `user_role` (`role_id`, `role_name`, `description`, `created_at`, `created_by`) VALUES
(1, 'System Administrator', 'Full system access', NOW(), NULL),
(2, 'HR Manager', 'Manage employees, leaves, approvals', NOW(), NULL),
(3, 'Payroll Manager', 'Process payroll, view all salaries', NOW(), NULL),
(4, 'Department Head', 'Approve leave/overtime for subordinates', NOW(), NULL),
(5, 'Employee', 'View own payslip, request leaves', NOW(), NULL);

INSERT INTO `system_permission` (`permission_id`, `permission_name`, `description`, `created_at`, `created_by`) VALUES
(1, 'view_own_payroll', 'View own payslip', NOW(), NULL),
(2, 'view_all_payroll', 'View all employees payroll', NOW(), NULL),
(3, 'process_payroll', 'Run payroll process', NOW(), NULL),
(4, 'approve_leave', 'Approve leave requests', NOW(), NULL),
(5, 'approve_overtime', 'Approve overtime requests', NOW(), NULL),
(6, 'manage_employees', 'Add/edit/delete employees', NOW(), NULL),
(7, 'view_attendance', 'View attendance records', NOW(), NULL),
(8, 'edit_attendance', 'Modify attendance', NOW(), NULL);

INSERT INTO `role_permission` (`role_id`, `permission_id`, `created_at`, `created_by`) VALUES
(1,1, NOW(), NULL),(1,2, NOW(), NULL),(1,3, NOW(), NULL),(1,4, NOW(), NULL),(1,5, NOW(), NULL),(1,6, NOW(), NULL),(1,7, NOW(), NULL),(1,8, NOW(), NULL),
(2,1, NOW(), NULL),(2,2, NOW(), NULL),(2,4, NOW(), NULL),(2,5, NOW(), NULL),(2,6, NOW(), NULL),(2,7, NOW(), NULL),
(3,1, NOW(), NULL),(3,2, NOW(), NULL),(3,3, NOW(), NULL),(3,7, NOW(), NULL),
(4,1, NOW(), NULL),(4,4, NOW(), NULL),(4,5, NOW(), NULL),(4,7, NOW(), NULL),
(5,1, NOW(), NULL);

-- 19. User Accounts
INSERT INTO `user_account` (`employee_id`, `username`, `password_hash`, `is_active`, `created_at`, `created_by`) VALUES
(10001, 'manueliii.garcia', '$2y$10$dummyhash1', 1, NOW(), NULL),
(10002, 'antonio.lim', '$2y$10$dummyhash2', 1, NOW(), NULL),
(10003, 'biancasofia.aquino', '$2y$10$dummyhash3', 1, NOW(), NULL),
(10004, 'isabella.reyes', '$2y$10$dummyhash4', 1, NOW(), NULL),
(10005, 'eduard.hernandez', '$2y$10$dummyhash5', 1, NOW(), NULL),
(10006, 'andreamae.villanueva', '$2y$10$dummyhash6', 1, NOW(), NULL),
(10007, 'brad.sanjose', '$2y$10$dummyhash7', 1, NOW(), NULL),
(10008, 'alice.romualdez', '$2y$10$dummyhash8', 1, NOW(), NULL),
(10009, 'rosie.atienza', '$2y$10$dummyhash9', 1, NOW(), NULL),
(10010, 'roderick.alvaro', '$2y$10$dummyhash10', 1, NOW(), NULL),
(10011, 'anthony.salcedo', '$2y$10$dummyhash11', 1, NOW(), NULL),
(10012, 'josie.lopez', '$2y$10$dummyhash12', 1, NOW(), NULL),
(10013, 'martha.farala', '$2y$10$dummyhash13', 1, NOW(), NULL),
(10014, 'leila.martinez', '$2y$10$dummyhash14', 1, NOW(), NULL),
(10015, 'fredrick.romualdez', '$2y$10$dummyhash15', 1, NOW(), NULL),
(10016, 'christian.mata', '$2y$10$dummyhash16', 1, NOW(), NULL),
(10017, 'selena.deleon', '$2y$10$dummyhash17', 1, NOW(), NULL),
(10018, 'allison.sanjose', '$2y$10$dummyhash18', 1, NOW(), NULL),
(10019, 'cydney.rosario', '$2y$10$dummyhash19', 1, NOW(), NULL),
(10020, 'mark.bautista', '$2y$10$dummyhash20', 1, NOW(), NULL),
(10021, 'darlene.lazaro', '$2y$10$dummyhash21', 1, NOW(), NULL),
(10022, 'kolby.delossantos', '$2y$10$dummyhash22', 1, NOW(), NULL),
(10023, 'vella.santos', '$2y$10$dummyhash23', 1, NOW(), NULL),
(10024, 'tomas.delrosario', '$2y$10$dummyhash24', 1, NOW(), NULL),
(10025, 'jacklyn.tolentino', '$2y$10$dummyhash25', 1, NOW(), NULL),
(10026, 'percival.gutierrez', '$2y$10$dummyhash26', 1, NOW(), NULL),
(10027, 'garfield.manalaysay', '$2y$10$dummyhash27', 1, NOW(), NULL),
(10028, 'lizeth.villegas', '$2y$10$dummyhash28', 1, NOW(), NULL),
(10029, 'carol.ramos', '$2y$10$dummyhash29', 1, NOW(), NULL),
(10030, 'emelia.maceda', '$2y$10$dummyhash30', 1, NOW(), NULL),
(10031, 'delia.aguilar', '$2y$10$dummyhash31', 1, NOW(), NULL),
(10032, 'johnrafael.castro', '$2y$10$dummyhash32', 1, NOW(), NULL),
(10033, 'carlosian.martinez', '$2y$10$dummyhash33', 1, NOW(), NULL),
(10034, 'beatriz.santos', '$2y$10$dummyhash34', 1, NOW(), NULL);

-- Account-Role mapping (user_account_id follows insertion order)
INSERT INTO `account_role` (`user_account_id`, `role_id`, `created_at`, `created_by`) VALUES
(1,1, NOW(), NULL),  -- CEO
(6,2, NOW(), NULL),  -- HR Manager
(11,3, NOW(), NULL), -- Payroll Manager
(1,4, NOW(), NULL),(2,4, NOW(), NULL),(3,4, NOW(), NULL),(4,4, NOW(), NULL),(6,4, NOW(), NULL),(10,4, NOW(), NULL),(11,4, NOW(), NULL),(12,4, NOW(), NULL),(15,4, NOW(), NULL),(16,4, NOW(), NULL),(17,4, NOW(), NULL),(32,4, NOW(), NULL), -- Department Heads
(1,5, NOW(), NULL),(2,5, NOW(), NULL),(3,5, NOW(), NULL),(4,5, NOW(), NULL),(5,5, NOW(), NULL),(6,5, NOW(), NULL),(7,5, NOW(), NULL),(8,5, NOW(), NULL),(9,5, NOW(), NULL),(10,5, NOW(), NULL),(11,5, NOW(), NULL),(12,5, NOW(), NULL),(13,5, NOW(), NULL),(14,5, NOW(), NULL),(15,5, NOW(), NULL),(16,5, NOW(), NULL),(17,5, NOW(), NULL),(18,5, NOW(), NULL),(19,5, NOW(), NULL),(20,5, NOW(), NULL),(21,5, NOW(), NULL),(22,5, NOW(), NULL),(23,5, NOW(), NULL),(24,5, NOW(), NULL),(25,5, NOW(), NULL),(26,5, NOW(), NULL),(27,5, NOW(), NULL),(28,5, NOW(), NULL),(29,5, NOW(), NULL),(30,5, NOW(), NULL),(31,5, NOW(), NULL),(32,5, NOW(), NULL),(33,5, NOW(), NULL),(34,5, NOW(), NULL);

-- 20. Pay Periods
INSERT INTO `pay_period` (`period_start_date`, `period_end_date`, `pay_date`, `payroll_status_id`, `created_at`, `created_by`) VALUES
('2024-07-01','2024-07-15','2024-07-20',4, NOW(), NULL),
('2024-07-16','2024-07-31','2024-08-05',4, NOW(), NULL),
('2024-08-01','2024-08-15','2024-08-20',4, NOW(), NULL),
('2024-08-16','2024-08-31','2024-09-05',4, NOW(), NULL),
('2024-09-01','2024-09-15','2024-09-20',4, NOW(), NULL),
('2024-09-16','2024-09-30','2024-10-05',4, NOW(), NULL),
('2024-10-01','2024-10-15','2024-10-20',4, NOW(), NULL),
('2024-10-16','2024-10-31','2024-11-05',4, NOW(), NULL),
('2024-11-01','2024-11-15','2024-11-20',4, NOW(), NULL),
('2024-11-16','2024-11-30','2024-12-05',4, NOW(), NULL),
('2024-12-01','2024-12-15','2024-12-20',3, NOW(), NULL),
('2024-12-16','2024-12-31','2025-01-05',2, NOW(), NULL);

-- 21. Sample Payroll (first pay period)
INSERT INTO `payroll` (`employee_id`, `pay_period_id`, `payroll_status_id`, `basic_salary`, `hourly_rate`, `hours_worked`, `basic_pay`, `overtime_pay`, `gross_pay`, `total_benefits`, `total_deductions`, `net_pay`, `created_at`, `created_by`) VALUES
(10001,1,4,90000,535.71,80,42856.80,0,42856.80,4500,8500,38856.80, NOW(), NULL),
(10002,1,4,60000,357.14,80,28571.20,0,28571.20,4500,6500,26571.20, NOW(), NULL),
(10005,1,4,52670,313.51,85,26648.35,1567.55,28215.90,3500,5800,25915.90, NOW(), NULL);

-- 22. Sample Leave Requests
INSERT INTO `leave_request` (`employee_id`, `leave_type_id`, `start_date`, `end_date`, `description`, `request_status_id`, `approved_by`, `approved_at`, `created_at`, `created_by`) VALUES
(10008,1,'2024-08-10','2024-08-12','Family trip',2,10006,'2024-08-01 09:00:00', NOW(), NULL),
(10013,2,'2024-09-05','2024-09-06','Flu',1,NULL,NULL, NOW(), NULL);

-- 23. Sample Work Time Requests (merged from overtime_request and undertime_request)
INSERT INTO `work_time_request` (`employee_id`, `request_type`, `request_date`, `start_time`, `end_time`, `reason`, `request_status_id`, `approved_by`, `approved_at`, `created_at`, `created_by`) VALUES
(10005,'overtime', '2024-07-19','18:00:00','20:30:00','Server maintenance',2,10002,'2024-07-18 09:00:00', NOW(), NULL),
(10018,'undertime','2024-08-15','15:00:00','17:00:00','Doctor appointment',2,10016,'2024-08-14 08:00:00', NOW(), NULL);

-- 25. Sample Help Center Tickets
INSERT INTO `help_center_ticket` (`employee_id`, `help_center_category_id`, `issue`, `request_status_id`, `created_at`, `created_by`) VALUES
(10008,1,'My payslip for July shows incorrect deduction',5, NOW(), NULL),
(10013,2,'Unable to file leave request online',6, NOW(), NULL);

-- 26. Sample Approvals
INSERT INTO `approval` (`request_type`, `request_id`, `approver_id`, `request_status_id`, `remarks`, `approved_at`, `created_at`, `created_by`) VALUES
('leave_request',1,10006,2,'Approved','2024-08-01 09:00:00', NOW(), NULL),
('work_time_request',1,10002,2,'Approved','2024-07-18 09:00:00', NOW(), NULL);

-- 27. Sample Action Log
INSERT INTO `action_log` (`user_account_id`, `action_name`, `table_name`, `record_id`, `old_value`, `new_value`, `created_at`, `created_by`) VALUES
(1,'UPDATE','employee',10005,'{"salary":50000}','{"salary":52670}', NOW(), NULL),
(6,'APPROVE','leave_request',1,'{"status":"Pending"}','{"status":"Approved"}', NOW(), NULL);

-- 28. payroll_benefit
INSERT INTO `payroll_benefit` (`payroll_id`, `benefit_type_id`, `amount`, `created_at`, `created_by`) VALUES

(1, 1, 1500.00, NOW(), NULL),   -- Rice Subsidy
(1, 2, 2000.00, NOW(), NULL),   -- Phone Allowance
(1, 3, 1000.00, NOW(), NULL),   -- Clothing Allowance

-- Payroll 2 (Employee 10002)
(2, 1, 1500.00, NOW(), NULL),
(2, 2, 2000.00, NOW(), NULL),
(2, 3, 1000.00, NOW(), NULL),

-- Payroll 3 (Employee 10005)
(3, 1, 1500.00, NOW(), NULL),
(3, 2, 1000.00, NOW(), NULL),
(3, 3, 1000.00, NOW(), NULL);


INSERT INTO `payroll_deduction` (`payroll_id`, `deduction_type_id`, `amount`, `created_at`, `created_by`) VALUES
-- Payroll 1 (Employee 10001) 
(1, 1, 1350.00, NOW(), NULL),   -- SSS
(1, 2,  900.00, NOW(), NULL),   -- PhilHealth
(1, 3,  100.00, NOW(), NULL),   -- Pag-IBIG
(1, 4, 6150.00, NOW(), NULL),   -- Withholding Tax

-- Payroll 2 (Employee 10002) 
(2, 1, 1350.00, NOW(), NULL),
(2, 2,  900.00, NOW(), NULL),
(2, 3,  100.00, NOW(), NULL),
(2, 4, 4150.00, NOW(), NULL),

-- Payroll 3 (Employee 10005) 
(3, 1, 1350.00, NOW(), NULL),
(3, 2,  900.00, NOW(), NULL),
(3, 3,  100.00, NOW(), NULL),
(3, 4, 3450.00, NOW(), NULL);


INSERT INTO `payslip` (`payroll_id`, `payslip_number`, `generated_at`, `created_at`, `created_by`) VALUES
(1, 'PS-2024-07-0001', NOW(), NOW(), NULL),
(2, 'PS-2024-07-0002', NOW(), NOW(), NULL),
(3, 'PS-2024-07-0003', NOW(), NOW(), NULL);


SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
SET SQL_MODE=@OLD_SQL_MODE;