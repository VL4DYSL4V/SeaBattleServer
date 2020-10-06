package game.entity.battle;

import game.enums.FiringResult;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BattleStatistics implements Serializable {

    private Map<String, Integer> statisticsFirst =  new HashMap<>();
    private Map<String, Integer> statisticsSecond = new HashMap<>();

    private static final long serialVersionUID = 3348698559001402439L;

    {
        statisticsSecond.put("aimed", 0);
        statisticsSecond.put("missed", 0);
        statisticsSecond.put("destroyed", 0);
        statisticsFirst.put("aimed", 0);
        statisticsFirst.put("missed", 0);
        statisticsFirst.put("destroyed", 0);
    }

    public BattleStatistics() {}

    public void updateStatisticsFirst(FiringResult firingResult){
        updateStatistics(statisticsFirst, firingResult);
    }

    public void updateStatisticsSecond(FiringResult firingResult){
        updateStatistics(statisticsSecond, firingResult);
    }

    private void updateStatistics(Map<String, Integer> statistics, FiringResult firingResult) {
        if (firingResult == FiringResult.AIMED) {
            incrementAiming(statistics);
        } else if (firingResult == FiringResult.DESTROYED) {
            incrementDestroyed(statistics);
        } else {
            incrementMissed(statistics);
        }
    }

    private void incrementAiming(Map<String, Integer> statistics) {
        statistics.put("aimed", statisticsSecond.get("aimed") + 1);
    }

    private void incrementDestroyed(Map<String, Integer> statistics) {
        statistics.put("destroyed", statisticsSecond.get("destroyed") + 1);
    }

    private void incrementMissed(Map<String, Integer> statistics) {
        statistics.put("missed", statisticsSecond.get("missed") + 1);
    }

    public Map<String, Integer> getStatisticsFirst() {
        return statisticsFirst;
    }

    public Map<String, Integer> getStatisticsSecond() {
        return statisticsSecond;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BattleStatistics that = (BattleStatistics) o;
        return Objects.equals(statisticsFirst, that.statisticsFirst) &&
                Objects.equals(statisticsSecond, that.statisticsSecond);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statisticsFirst, statisticsSecond);
    }

    @Override
    public String toString() {
        return "BattleStatistics{" +
                "statisticsFirst=" + statisticsFirst +
                ", statisticsSecond=" + statisticsSecond +
                '}';
    }
}
