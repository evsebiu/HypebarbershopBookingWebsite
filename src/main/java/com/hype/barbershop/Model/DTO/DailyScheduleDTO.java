package com.hype.barbershop.Model.DTO;

import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
public class DailyScheduleDTO {
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean isWorkingDay;
}
