package rs.raf.demo.requests;

import lombok.Data;

@Data
public class TimeSchedule {

    private int year;
    private String month;
    private int day;
    private int hour;
    private int minute;
    private int second;
}
