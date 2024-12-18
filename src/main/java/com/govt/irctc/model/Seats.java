package com.govt.irctc.model;

import com.govt.irctc.dto.SeatDto;
import com.govt.irctc.enums.SeatType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Seats extends BaseModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int totalNumberOfSeats;
    private int oneAcSeats;
    private int twoAcSeats;
    private int threeAcSeats;
    private int sleeperSeats;
    private int generalSeats;
    private SeatType seatType;
    private double firstAcPrice;
    private double secondAcPrice;
    private double thirdAcPrice;
    private double sleeperPrice;
    private double generalPrice;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Train train;

    public SeatDto convertToDto() {
        SeatDto seatDto = new SeatDto();

        seatDto.setTrainNumber(train.getTrainNumber());
        seatDto.setTotalNumberOfSeats(totalNumberOfSeats);
        seatDto.setOneAcSeats(oneAcSeats);
        seatDto.setTwoAcSeats(twoAcSeats);
        seatDto.setThreeAcSeats(threeAcSeats);
        seatDto.setSleeperSeats(sleeperSeats);
        seatDto.setGeneralSeats(generalSeats);
        seatDto.setFirstAcPrice(firstAcPrice);
        seatDto.setSecondAcPrice(secondAcPrice);
        seatDto.setThirdAcPrice(thirdAcPrice);
        seatDto.setSleeperPrice(sleeperPrice);
        seatDto.setGeneralPrice(generalPrice);

        return seatDto;
    }
}
