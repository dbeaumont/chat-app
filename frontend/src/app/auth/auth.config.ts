
import { AuthConfig } from 'angular-oauth2-oidc';
import { environment } from '../../environments/environment';

export const authConfig: AuthConfig = {
  issuer: environment.oidc.issuer,
  clientId: environment.oidc.clientId,
  redirectUri: environment.oidc.redirectUri,
  postLogoutRedirectUri: environment.oidc.postLogoutRedirectUri,
  responseType: 'code',
  scope: environment.oidc.scope,
  showDebugInformation: false,
  useSilentRefresh: false, // optional; set to true if you configure a silent-refresh page
  strictDiscoveryDocumentValidation: false,
};
