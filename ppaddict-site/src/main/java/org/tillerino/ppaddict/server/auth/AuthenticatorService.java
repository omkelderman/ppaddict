package org.tillerino.ppaddict.server.auth;

import javax.servlet.http.HttpServletRequest;

import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

public interface AuthenticatorService {
  /**
   * @return something that uniquely identifiers this AuthenticatorService. It will be used as a get-param, so no fancy characters
   */
  String getIdentifier();

  /**
   * @return How this AuthenticatorService is called in the UI.
   */
  String getDisplayName();

  OAuthService getService();

  Credentials createUser(OAuthService service, HttpServletRequest req, Token requestToken);
}
