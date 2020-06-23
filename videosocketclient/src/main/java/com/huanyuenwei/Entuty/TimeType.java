package com.huanyuenwei.Entuty;

import java.sql.Time;

public enum TimeType {

    FIVETIME(5),
    TENTIME(10),
    SECTIME(20),
    THRETIME(30),
    THRTIME(40),
    FIVTIME(50),
    SIXVIME(60);

    private final int value;
    //构造方法必须是private或者默认
    private TimeType(int value) {
        this.value = value;
    }

    public TimeType valueOf(int value) {
        switch (value) {
            case 5:
                return TimeType.FIVETIME;
            case 10:
                return TimeType.TENTIME;
            case 20:
                return TimeType.SECTIME;
            case 30:
                return TimeType.THRETIME;
            case 40:
                return TimeType.THRTIME;
            case 50:
                return TimeType.FIVTIME;
            case 60:
                return TimeType.SIXVIME;
            default:
                return null;
        }
    }

}
