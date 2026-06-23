package com.motorph.model;

import java.time.LocalDate;

/**
 * Represents one employee row as returned by v_full_employee_profile.
 *
 * Display-only fields (position, status, supervisor name) come from the view.
 * ID fields (positionId, employmentStatusId, immediateSupervisorId) are the
 * raw FK values stored in the employee table — they are needed by the DAO
 * when writing back to the database.
 */
public class Employee {

    // ── Core identity ────────────────────────────────────────────────────────
    protected String    employeeId;
    protected String    lastName;
    protected String    firstName;
    protected LocalDate birthday;
    protected String    address;
    protected String    phoneNumber;
    protected String    email;

    // ── Government IDs ───────────────────────────────────────────────────────
    protected String SSSNumber;
    protected String philhealthNumber;
    protected String TIN;
    protected String pagIbigNumber;

    // ── Human-readable display strings (from the view) ───────────────────────
    protected String status;               // employment_status.status_type
    protected String position;             // employee_position.position_name
    protected String immediateSupervisor;  // CONCAT(sup.first_name, ' ', sup.last_name)

    // ── Compensation ─────────────────────────────────────────────────────────
    protected double basicSalary;
    protected double riceSubsidy;
    protected double phoneAllowance;
    protected double clothingAllowance;

    /**
     * Stored from the compensation table (DB is the source of truth).
     * Falls back to the computed value if not explicitly set (e.g. before
     * a first DB round-trip).
     */
    protected double hourlyRate;

    // ── Raw FK columns (needed by DAO for INSERT / UPDATE) ───────────────────
    protected Integer positionId;
    protected Integer immediateSupervisorId;   // nullable — no supervisor
    protected Integer employmentStatusId;

    // ── Constructors ─────────────────────────────────────────────────────────

    /** No-arg constructor — used by frameworks and manual builders. */
    public Employee() {}

    /**
     * Full constructor used by {@code JdbcEmployeeDAO.mapRow()} when reading
     * from the {@code v_full_employee_profile} view.
     *
     * Parameter order matches the view columns that matter to this model.
     * {@code hourlyRate} is taken directly from {@code compensation.hourly_rate}
     * so that manually-overridden DB values are respected.
     */
    public Employee(
            String    employeeId,
            String    lastName,
            String    firstName,
            LocalDate birthday,
            String    address,
            String    phoneNumber,
            String    SSSNumber,
            String    philhealthNumber,
            String    TIN,
            String    pagIbigNumber,
            String    status,
            String    position,
            String    immediateSupervisor,
            double    basicSalary,
            double    riceSubsidy,
            double    phoneAllowance,
            double    clothingAllowance,
            double    hourlyRate,           // ← added: read from compensation table
            String    email
    ) {
        this.employeeId          = employeeId;
        this.lastName            = lastName;
        this.firstName           = firstName;
        this.birthday            = birthday;
        this.address             = address;
        this.phoneNumber         = phoneNumber;
        this.SSSNumber           = SSSNumber;
        this.philhealthNumber    = philhealthNumber;
        this.TIN                 = TIN;
        this.pagIbigNumber       = pagIbigNumber;
        this.status              = status;
        this.position            = position;
        this.immediateSupervisor = immediateSupervisor;
        this.basicSalary         = basicSalary;
        this.riceSubsidy         = riceSubsidy;
        this.phoneAllowance      = phoneAllowance;
        this.clothingAllowance   = clothingAllowance;
        this.hourlyRate          = hourlyRate;
        this.email               = email;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String    getEmployeeId()          { return employeeId; }
    public String    getLastName()             { return lastName; }
    public String    getFirstName()            { return firstName; }
    public LocalDate getBirthday()             { return birthday; }
    public String    getAddress()              { return address; }
    public String    getPhoneNumber()          { return phoneNumber; }
    public String    getEmail()                { return email; }

    public String    getSSSNumber()            { return SSSNumber; }
    public String    getPhilhealthNumber()     { return philhealthNumber; }
    public String    getTIN()                  { return TIN; }
    public String    getPagIbigNumber()        { return pagIbigNumber; }

    public String    getStatus()               { return status; }
    public String    getPosition()             { return position; }
    public String    getImmediateSupervisor()  { return immediateSupervisor; }

    public double    getBasicSalary()          { return basicSalary; }
    public double    getRiceSubsidy()          { return riceSubsidy; }
    public double    getPhoneAllowance()       { return phoneAllowance; }
    public double    getClothingAllowance()    { return clothingAllowance; }

    public Integer   getPositionId()           { return positionId; }
    public Integer   getImmediateSupervisorId(){ return immediateSupervisorId; }
    public Integer   getEmploymentStatusId()   { return employmentStatusId; }

    // ── Setters for FK ID fields ─────────────────────────────────────────────
    // These are set by callers (e.g. the UI or service layer) before
    // handing the Employee object to DAO.save() or DAO.update().

    public void setPositionId(Integer positionId)                     { this.positionId = positionId; }
    public void setImmediateSupervisorId(Integer immediateSupervisorId){ this.immediateSupervisorId = immediateSupervisorId; }
    public void setEmploymentStatusId(Integer employmentStatusId)      { this.employmentStatusId = employmentStatusId; }

    // ── Computed / convenience methods ───────────────────────────────────────

    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Returns the stored hourly rate if it was read from the DB,
     * otherwise falls back to deriving it from basicSalary
     * (21 working days × 8 hours, rounded to 2 decimal places).
     */
    public double getHourlyRate() {
        if (hourlyRate > 0) return hourlyRate;
        double raw = basicSalary / (21 * 8);
        return Math.round(raw * 100.0) / 100.0;
    }

    public double getDailyRate()        { return getHourlyRate() * 8; }
    public double getSemiMonthlyRate()  { return basicSalary / 2; }

    public double getTotalAllowances() {
        return riceSubsidy + clothingAllowance + phoneAllowance;
    }
    
    
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Employee{");
        sb.append("id='").append(employeeId).append('\'');
        sb.append(", name='").append(getFullName()).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", position='").append(position).append('\'');
        sb.append(", status='").append(status).append('\'');
        sb.append(", supervisor='").append(immediateSupervisor).append('\'');
        sb.append(", basicSalary=").append(basicSalary);
        sb.append(", hourlyRate=").append(getHourlyRate());
        sb.append(", totalAllowances=").append(getTotalAllowances());
        sb.append('}');
        return sb.toString();
    }
}
