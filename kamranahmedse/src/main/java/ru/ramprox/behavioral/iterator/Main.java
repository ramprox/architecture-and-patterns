package ru.ramprox.behavioral.iterator;

import java.util.Iterator;

public class Main {
    public static void main(String[] args) {
        StationList stationList = new StationList();
        stationList.addStation(new RadioStation(89));
        stationList.addStation(new RadioStation(101));
        stationList.addStation(new RadioStation(102));
        stationList.addStation(new RadioStation(103.2f));

        for(RadioStation station : stationList) {
            System.out.println(station.getFrequency());
        }

        stationList.removeStation(new RadioStation(89));

        for(RadioStation station : stationList) {
            System.out.println(station.getFrequency());
        }

        Iterator<RadioStation> iterator = stationList.iterator();
        while(iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }
}
