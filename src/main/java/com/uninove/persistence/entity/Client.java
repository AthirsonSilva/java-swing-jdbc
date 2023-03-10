package com.uninove.persistence.entity;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Client {
    private UUID id;
    private String name;
    private String phone;
    private String gender;
    private double income;
}
