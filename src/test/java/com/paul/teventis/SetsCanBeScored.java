package com.paul.teventis;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class SetsCanBeScored {

    private final String setScoreAnnounced;
    private final List<Event> pointsScored;

    @Parameterized.Parameters(name = "Set Scoring: {index}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                        {"0-0", ImmutableList.of()},
//                        {"1-0", winOneGame()}
                }
        );
    }

    private static Object winOneGame() {
        return ImmutableList.of(
                new PlayerOneScored(),
                new PlayerOneScored(),
                new PlayerOneScored(),
                new PlayerOneScored()
        );
    }

    public SetsCanBeScored(String setScoreAnnounced, List<Event> pointsScored) {
        this.setScoreAnnounced = setScoreAnnounced;
        this.pointsScored = pointsScored;
    }

    @Test
    public void scoreIsAnnounced() {
        final FakeEventStream inMemoryEventStream = new FakeEventStream();
        inMemoryEventStream.addAll(pointsScored);

        new Set(inMemoryEventStream);
        final SetScoreAnnounced scoreAnnounced = (SetScoreAnnounced) inMemoryEventStream.readLast();

        assertThat(scoreAnnounced.toString()).isEqualTo(setScoreAnnounced);
    }
}

class SetScoreAnnounced implements Event {
    private final String score;

    public SetScoreAnnounced(final String score) {

        this.score = score;
    }

    @Override
    public String toString() {
        return score;
    }
}

class Set {

    public Set(final EventStream eventStream) {
        eventStream.write(new SetScoreAnnounced("0-0"));

        eventStream.readAll().forEach(e -> {

        });
    }
}