package org.tillerino.ppaddict;

import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.google.inject.servlet.ServletModule;
import org.mockito.Mockito;
import org.tillerino.ppaddict.auth.FakeAuthenticatorService;
import org.tillerino.ppaddict.auth.FakeAuthenticatorWebsite;
import org.tillerino.ppaddict.server.PpaddictBackend;
import org.tillerino.ppaddict.server.PpaddictUserDataService;
import org.tillerino.ppaddict.server.auth.AuthArriveService;
import org.tillerino.ppaddict.server.auth.AuthenticatorService;
import org.tillerino.ppaddict.server.auth.implementations.OsuOauth;
import org.tillerino.ppaddict.util.Clock;
import org.tillerino.ppaddict.web.data.repos.PpaddictLinkKeyRepository;
import org.tillerino.ppaddict.web.data.repos.PpaddictUserRepository;
import tillerino.tillerinobot.BotBackend;
import tillerino.tillerinobot.LocalConsoleTillerinobot;

import javax.inject.Singleton;
import java.util.List;


public class PpaddictTestModule extends ServletModule {

  @Override
  protected void configureServlets() {
    bind(String.class).annotatedWith(Names.named("ppaddict.auth.returnURL")).toInstance(
        "http://localhost:8080" + AuthArriveService.PATH);

    bind(String.class).annotatedWith(Names.named("ppaddict.apiauth.key")).toInstance("ppaddict-app-key");

    bind(Boolean.class).annotatedWith(Names.named("tillerinobot.test.persistentBackend"))
        .toInstance(true);

    bind(PpaddictBackend.class).to(TestBackend.class).in(Singleton.class);

    install(new LocalConsoleTillerinobot() {
      @Override
      protected void installMore() {
        // don't call super
      }
    });

    serve(FakeAuthenticatorWebsite.PATH).with(FakeAuthenticatorWebsite.class);

    serve("/showErrorPage").with(ProducesError.class);

    install(new PpaddictModule() {
      @Override
      protected List<AuthenticatorService> createAuthServices(String returnUrl) {
        List<AuthenticatorService> services = super.createAuthServices(returnUrl);
        services.add(new FakeAuthenticatorService());
        return services;
      }
    });
  }

  @Provides
  @Singleton
  public PpaddictUserDataService getPpaddictUserDataService(PpaddictUserRepository users, PpaddictLinkKeyRepository linkKeys, Clock clock, BotBackend botBackend) {
    final String osuOAuthPrefix = OsuOauth.OSU_AUTH_SERVICE_IDENTIFIER + ":";

    PpaddictUserDataService ppaddictUserDataService = Mockito.spy(new PpaddictUserDataService(users, linkKeys, clock));
    Mockito.doAnswer(x -> {
      String id = x.getArgument(0);
      String displayName = x.getArgument(1);
      int osuId = Integer.parseInt(id.substring(osuOAuthPrefix.length()));
      ((tillerino.tillerinobot.TestBackend) botBackend).hintUser(displayName, false, 100000, 1000, osuId);

      return x.callRealMethod();
    }).when(ppaddictUserDataService).getLinkString(Mockito.startsWith(osuOAuthPrefix), Mockito.anyString());
    return ppaddictUserDataService
    ;
  }
}
