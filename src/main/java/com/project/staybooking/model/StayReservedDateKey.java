package com.project.staybooking.model;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

// 让两个column同时作为primary key
@Embeddable //Since we’re going to use StayReservedDateKey as the primary key of the StayReservedDate table, we need to mark it as @Embeddable, add a serialVersionUID and implement both hashCode() and equals().
public class StayReservedDateKey implements Serializable { //
    private static final long serialVersionUID = 1L;

    // 两个要作为primary key的id
    private Long stay_id;
    private LocalDate date;

    // no parameter constructor： 把db里的记录convert成 java obj时，先调用空constructor创造出user的对象，然后通过reflection判断有哪些field
    public StayReservedDateKey() {} //hibernate：把java和数据库的table match


    public StayReservedDateKey(Long stay_id, LocalDate date) {
        this.stay_id = stay_id;
        this.date = date;
    }

    public Long getStay_id() {
        return stay_id;
    }

    public StayReservedDateKey setStay_id(Long stay_id) {
        this.stay_id = stay_id;
        return this;
    }

    public LocalDate getDate() {
        return date;
    }

    public StayReservedDateKey setDate(LocalDate date) {
        this.date = date;
        return this;
    }

    // 比较两个instance什么时候相等 （如果没有override，就会比较地址）
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StayReservedDateKey that = (StayReservedDateKey) o;
        return stay_id.equals(that.stay_id) && date.equals(that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stay_id, date);
    }

}
