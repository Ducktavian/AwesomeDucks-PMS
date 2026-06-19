package com.motorph.model;


/**
 *
 * @author Ducktavian
 */
public class LeaveType {

    private final int leaveTypeId;
    private final String leaveTypeName;

    public LeaveType(int leaveTypeId, String leaveTypeName) {
        this.leaveTypeId   = leaveTypeId;
        this.leaveTypeName = leaveTypeName;
    }

    public int getLeaveTypeId()       { return leaveTypeId; }
    public String getLeaveTypeName()  { return leaveTypeName; }

    @Override
    public String toString() { return leaveTypeName; }
}