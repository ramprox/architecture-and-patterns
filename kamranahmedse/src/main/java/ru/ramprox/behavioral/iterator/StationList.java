package ru.ramprox.behavioral.iterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StationList implements Iterable<RadioStation> {
    private List<RadioStation> stations = new ArrayList<>();

    public void addStation(RadioStation station) {
        stations.add(station);
    }

    public boolean removeStation(RadioStation station) {
        float toRemoveFrequency = station.getFrequency();
        return stations.removeIf(radioStation -> radioStation.getFrequency() == toRemoveFrequency);
    }

    @Override
    public Iterator<RadioStation> iterator() {
        return new StationListIterator();
    }

    private class StationListIterator implements Iterator<RadioStation> {

        private int currentPosition = 0;

        @Override
        public boolean hasNext() {
            return currentPosition < StationList.this.stations.size();
        }

        @Override
        public RadioStation next() {
            return StationList.this.stations.get(currentPosition++);
        }

        @Override
        public void remove() {
            if(currentPosition == 0) {
                throw new IllegalStateException("Current position 0");
            }
            StationList.this.stations.remove(--currentPosition);
        }
    }
}
