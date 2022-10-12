package com.project.staybooking.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "stay_reserved_date") //3 columns: stay, stay id,date (from StayReservedDateKey)
public class StayReservedDate implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId //二个primary key
    private StayReservedDateKey id;

    // column stay_id also is a forenign key of table stay
    @MapsId("stay_id") //用之前已经存在的stay_id做foreign key
    @ManyToOne //默认创建第三张中间表，@joincolunm不加
    private Stay stay;//foreign key，实现多对一

    public StayReservedDate() {}

    public StayReservedDate(StayReservedDateKey id, Stay stay) {
        this.id = id;
        this.stay = stay;
    }

    public StayReservedDateKey getId() {
        return id;
    }

    public Stay getStay() {
        return stay;
    }

}