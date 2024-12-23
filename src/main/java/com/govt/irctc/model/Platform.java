package com.govt.irctc.model;

import com.govt.irctc.enums.PlatformStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Platform extends BaseModel {
    private int platformNumber;

    @Enumerated(EnumType.STRING)
    private PlatformStatus platformStatus;

    @ManyToOne
    private Station station;

    @OneToMany(mappedBy = "platform", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Train> trains;
}
