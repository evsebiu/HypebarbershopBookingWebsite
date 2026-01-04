package com.hype.barbershop.Model.DTO;

import com.hype.barbershop.Model.Entity.BarberSchedule;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class WeeklyScheduleDTO {
    private List<DailyScheduleDTO> dailySchedules = new ArrayList<>();
}
