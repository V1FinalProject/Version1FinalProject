/**
 * Runtime configuration.
 *
 * Deployment target is Azure: the Angular build is served as a static site and
 * the Spring Boot API lives behind `apiBaseUrl`, with MongoDB Atlas behind
 * that. For production, point `apiBaseUrl` at the deployed API.
 *
 * In development `/api` is proxied to the Spring Boot app on :8080 by
 * `proxy.conf.json`, so the dev server is same-origin with the API and neither
 * side needs CORS configuration.
 */
export const environment = {
  production: false,

  /** Base URL of the Spring Boot API. */
  apiBaseUrl: '/api',

  /**
   * Fall back to persisting nominations in localStorage instead of POSTing
   * them. The API is live now, so this stays false — flip it back only to demo
   * the form with the backend stopped.
   */
  useMockApi: false,
};
