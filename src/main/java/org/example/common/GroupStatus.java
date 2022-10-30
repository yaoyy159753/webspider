package org.example.common;

import java.util.Objects;

public class GroupStatus {
    private String groupId;
    private Long completedTaskCount;
    private Long taskCount;
    private String groupName;
    private Integer workSize;
    private Integer poolSize;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Long getCompletedTaskCount() {
        return completedTaskCount;
    }

    public void setCompletedTaskCount(Long completedTaskCount) {
        this.completedTaskCount = completedTaskCount;
    }

    public Long getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(Long taskCount) {
        this.taskCount = taskCount;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getWorkSize() {
        return workSize;
    }

    public void setWorkSize(Integer workSize) {
        this.workSize = workSize;
    }

    public Integer getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(Integer poolSize) {
        this.poolSize = poolSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupStatus that = (GroupStatus) o;
        return Objects.equals(groupId, that.groupId) && Objects.equals(completedTaskCount, that.completedTaskCount) && Objects.equals(taskCount, that.taskCount) && Objects.equals(groupName, that.groupName) && Objects.equals(workSize, that.workSize) && Objects.equals(poolSize, that.poolSize);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, completedTaskCount, taskCount, groupName, workSize, poolSize);
    }

    @Override
    public String toString() {
        return "GroupStatus{" +
                "groupId='" + groupId + '\'' +
                ", completedTaskCount=" + completedTaskCount +
                ", taskCount=" + taskCount +
                ", groupName='" + groupName + '\'' +
                ", workSize=" + workSize +
                ", poolSize=" + poolSize +
                '}';
    }
}
