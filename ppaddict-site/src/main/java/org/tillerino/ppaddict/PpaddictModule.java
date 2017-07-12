package org.tillerino.ppaddict;

import org.tillerino.ppaddict.server.BeatmapTableServiceImpl;
import org.tillerino.ppaddict.server.EntityManagerFilter;
import org.tillerino.ppaddict.server.RateLimiterSettingsFilter;
import org.tillerino.ppaddict.server.RecommendationsServiceImpl;
import org.tillerino.ppaddict.server.UserDataServiceImpl;
import org.tillerino.ppaddict.server.auth.AuthModule;

import com.google.inject.servlet.ServletModule;

public class PpaddictModule extends ServletModule {
  @Override
  protected void configureServlets() {
    serve("/ppaddict/beatmaps").with(BeatmapTableServiceImpl.class);
    serve("/ppaddict/user").with(UserDataServiceImpl.class);
    serve("/ppaddict/recommendations").with(RecommendationsServiceImpl.class);
    filter("/ppaddict/*").through(EntityManagerFilter.class);
    filter("/ppaddict/*").through(RateLimiterSettingsFilter.class);
    install(new AuthModule());
  }
}
