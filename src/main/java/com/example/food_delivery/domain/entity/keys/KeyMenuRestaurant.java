package com.example.food_delivery.domain.entity.keys;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class KeyMenuRestaurant implements Serializable {

    @Column(name = "cate_id")
    private int cateId;

    @Column(name = "res_id")
    private int resId;
}
