package game.entity.battle;

import game.enums.FiringResult;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BattleStatistics implements Serializable {

    private Map<FiringResult, Integer> statisticsFirst =  new HashMap<>();
    private Map<FiringResult, Integer> statisticsSecond = new HashMap<>();

    private static final long serialVersionUID = 3348698559001402439L;

    public BattleStatistics() {
        initStatistics();
    }

    public void initStatistics(){
        for(FiringResult firingResult:FiringResult.values()) {
            statisticsFirst.put(firingResult, 0);
            statisticsSecond.put(firingResult, 0);
        }
    }

    public void updateStatisticsFirst(FiringResult firingResult){
        updateStatistics(statisticsFirst, firingResult);
    }

    public void updateStatisticsSecond(FiringResult firingResult){
        updateStatistics(statisticsSecond, firingResult);
    }

    /**Увеличить статистику по любому результату на 1; Но если результат = уничтожение, то надо увеличить
     * ещё и статистику попаданий*/
    private void updateStatistics(Map<FiringResult, Integer> statistics, FiringResult firingResult) {
        statistics.put(firingResult, statistics.get(firingResult) + 1);
        if(firingResult == FiringResult.DESTROYED){
            statistics.put(FiringResult.AIMED, statistics.get(FiringResult.AIMED) + 1);
        }
    }

    public Map<FiringResult, Integer> getStatisticsFirst() {
        return statisticsFirst;
    }

    public Map<FiringResult, Integer> getStatisticsSecond() {
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
