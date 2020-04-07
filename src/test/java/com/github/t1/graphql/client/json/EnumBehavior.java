package com.github.t1.graphql.client.json;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.t1.graphql.client.json.EnumBehavior.Episode.EMPIRE;
import static com.github.t1.graphql.client.json.EnumBehavior.Episode.JEDI;
import static com.github.t1.graphql.client.json.EnumBehavior.Episode.NEWHOPE;
import static org.assertj.core.api.BDDAssertions.then;

class EnumBehavior {
    private final GraphQlClientFixture fixture = new GraphQlClientFixture();

    enum Episode {
        NEWHOPE,
        EMPIRE,
        JEDI
    }

    interface EpisodeApi {
        Episode episode();
    }

    @Test void shouldCallEnumQuery() {
        fixture.returnsData("\"episode\":\"JEDI\"");
        EpisodeApi api = fixture.builder().build(EpisodeApi.class);

        Episode episode = api.episode();

        then(fixture.query()).isEqualTo("episode");
        then(episode).isEqualTo(JEDI);
    }


    interface EpisodesApi {
        List<Episode> episodes();
    }

    @Test void shouldCallEnumListQuery() {
        fixture.returnsData("\"episodes\":[\"NEWHOPE\",\"EMPIRE\",\"JEDI\"]");
        EpisodesApi api = fixture.builder().build(EpisodesApi.class);

        List<Episode> episode = api.episodes();

        then(fixture.query()).isEqualTo("episodes");
        then(episode).containsExactly(NEWHOPE, EMPIRE, JEDI);
    }
}
