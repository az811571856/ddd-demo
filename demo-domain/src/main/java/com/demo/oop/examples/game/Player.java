package com.demo.domain.entity;

import lombok.AccessLevel;
import lombok.Data;

@Data
public class Player {
    @Setter(AccessLevel.PROTECTED)
    private Weapon weapon;
}
