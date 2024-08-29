package com.StayHub.payload;


import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
@Data
public class DateDto {

    @NotNull
    @FutureOrPresent
    LocalDate checkIn;

    @NotNull @FutureOrPresent
    LocalDate checkOut;

}
