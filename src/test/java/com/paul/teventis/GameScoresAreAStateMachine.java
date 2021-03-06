package com.paul.teventis;

import com.google.common.collect.ImmutableList;
import com.paul.teventis.events.Event;
import com.paul.teventis.game.Game;
import com.paul.teventis.game.PlayerOneScored;
import com.paul.teventis.game.PlayerTwoScored;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class GameScoresAreAStateMachine {
    private static final List<Event> threePointsEach = Arrays.asList(
            new PlayerOneScored(), new PlayerOneScored(), new PlayerOneScored(),
            new PlayerTwoScored(), new PlayerTwoScored(), new PlayerTwoScored());

    private List<Event> playersScoringEvents;
    private String expectedScore;
    private String reportedScore = "";

    // love -> 15 -> 30 -> 40
    // pointPlayerTwo no players have scored... love-all
    // pointPlayerTwo player one has scored... 15-love
    // pointPlayerTwo player two has scored... love-15
    // pointPlayerTwo both have scored three times... deuce

    @Parameterized.Parameters(name = "Standard Game: {index}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                        {"love all", ImmutableList.of()},
                        {"15-love", ImmutableList.of(new PlayerOneScored())},
                        {"15-all", ImmutableList.of(new PlayerOneScored(), new PlayerTwoScored())},
                        {"30-love", ImmutableList.of(new PlayerOneScored(), new PlayerOneScored())},
                        {"30-15", ImmutableList.of(new PlayerOneScored(), new PlayerOneScored(), new PlayerTwoScored())},
                        {"40-15", ImmutableList.of(new PlayerOneScored(), new PlayerOneScored(), new PlayerTwoScored(), new PlayerOneScored())},
                        {"40-love", ImmutableList.of(new PlayerOneScored(), new PlayerOneScored(), new PlayerOneScored())},
                        {"40-15", ImmutableList.of(new PlayerOneScored(), new PlayerOneScored(), new PlayerOneScored(), new PlayerTwoScored())},
                        {"Game player one", ImmutableList.of(new PlayerOneScored(), new PlayerOneScored(), new PlayerOneScored(), new PlayerTwoScored(), new PlayerOneScored())},
                        {"40-30", ImmutableList.of(new PlayerOneScored(), new PlayerOneScored(), new PlayerOneScored(), new PlayerTwoScored(), new PlayerTwoScored())},
                        {"Game player one", ImmutableList.of(new PlayerOneScored(), new PlayerOneScored(), new PlayerOneScored(), new PlayerTwoScored(), new PlayerTwoScored(), new PlayerOneScored())},
                        {"Game player one", ImmutableList.of(new PlayerOneScored(), new PlayerOneScored(), new PlayerOneScored(), new PlayerOneScored())},
                        {"love-15", ImmutableList.of(new PlayerTwoScored())},
                        {"15-all", ImmutableList.of(new PlayerTwoScored(), new PlayerOneScored())},
                        {"love-30", ImmutableList.of(new PlayerTwoScored(), new PlayerTwoScored())},
                        {"15-30", ImmutableList.of(new PlayerTwoScored(), new PlayerTwoScored(), new PlayerOneScored())},
                        {"love-40", ImmutableList.of(new PlayerTwoScored(), new PlayerTwoScored(), new PlayerTwoScored())},
                        {"15-40", ImmutableList.of(new PlayerTwoScored(), new PlayerTwoScored(), new PlayerTwoScored(), new PlayerOneScored())},
                        {"Game player two", ImmutableList.of(new PlayerTwoScored(), new PlayerTwoScored(), new PlayerTwoScored(), new PlayerTwoScored())},
                        {"15-all", ImmutableList.of(new PlayerOneScored(), new PlayerTwoScored())},
                        {"15-30", ImmutableList.of(new PlayerOneScored(), new PlayerTwoScored(), new PlayerTwoScored())},
                        {"30-all", ImmutableList.of(new PlayerOneScored(), new PlayerTwoScored(), new PlayerTwoScored(), new PlayerOneScored())},
                        {"40-30", ImmutableList.of(new PlayerOneScored(), new PlayerTwoScored(), new PlayerTwoScored(), new PlayerOneScored(), new PlayerOneScored())},
                        {"15-40", ImmutableList.of(new PlayerOneScored(), new PlayerTwoScored(), new PlayerTwoScored(), new PlayerTwoScored())},
                        {"30-40", ImmutableList.of(new PlayerOneScored(), new PlayerTwoScored(), new PlayerTwoScored(), new PlayerTwoScored(), new PlayerOneScored())},
                        {"deuce", ImmutableList.of(new PlayerOneScored(), new PlayerTwoScored(), new PlayerTwoScored(), new PlayerTwoScored(), new PlayerOneScored(), new PlayerOneScored())},
                        {"Game player two", ImmutableList.of(new PlayerOneScored(), new PlayerTwoScored(), new PlayerTwoScored(), new PlayerTwoScored(), new PlayerOneScored(), new PlayerTwoScored())},
                        {"Game player two", ImmutableList.of(new PlayerOneScored(), new PlayerTwoScored(), new PlayerTwoScored(), new PlayerTwoScored(), new PlayerTwoScored())},
                        {"30-all", ImmutableList.of(new PlayerOneScored(), new PlayerTwoScored(), new PlayerOneScored(), new PlayerTwoScored())},
                        {"30-40", ImmutableList.of(new PlayerOneScored(), new PlayerTwoScored(), new PlayerOneScored(), new PlayerTwoScored(), new PlayerTwoScored())},
                        {"deuce", threePointsEach},
                        {"advantage player one", concat(threePointsEach, ImmutableList.of(new PlayerOneScored()))},
                        {"advantage player two", concat(threePointsEach, ImmutableList.of(new PlayerTwoScored()))},
                        {"Game player two", concat(threePointsEach, ImmutableList.of(new PlayerTwoScored(), new PlayerTwoScored()))},
                        {"deuce", concat(threePointsEach, ImmutableList.of(new PlayerTwoScored(), new PlayerOneScored()))},
                        {"Game player one", concat(threePointsEach, ImmutableList.of(new PlayerOneScored(), new PlayerOneScored()))},
                        {"deuce", concat(threePointsEach, ImmutableList.of(new PlayerOneScored(), new PlayerTwoScored()))},
                        {"Game player two", concat(threePointsEach, ImmutableList.of(new PlayerOneScored(), new PlayerTwoScored(), new PlayerTwoScored(), new PlayerTwoScored()))},
                        // game resets score if someone has won
                        {"Game player two", ImmutableList.of(
                                new PlayerOneScored(),
                                new PlayerOneScored(),
                                new PlayerOneScored(),
                                new PlayerOneScored(), //game player one
                                new PlayerTwoScored(),
                                new PlayerTwoScored(),
                                new PlayerTwoScored(),
                                new PlayerTwoScored()) // game player two
                        },
                }
        );
    }

    private static Object concat(final List<Event> a, final List<Event> b) {
        return Stream.of(a, b).flatMap(List::stream).collect(Collectors.toList());
    }

    public GameScoresAreAStateMachine(final String expectedScore, final List<Event> playersScoringEvents) {
        //parameters are injected here...
        this.playersScoringEvents = playersScoringEvents;
        this.expectedScore = expectedScore;
    }

    @Test
    public void PlayersWinningPointsChangesTheGamesScore() {
        final Game game = new Game();

        game.subscribeToScore(s -> this.reportedScore = s);

        playersScoringEvents.forEach(game::when);
        assertThat(this.reportedScore).isEqualTo(expectedScore);
    }
}


