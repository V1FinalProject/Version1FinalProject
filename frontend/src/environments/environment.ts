/**
 * Runtime configuration.
 *
 * Deployment target is Azure: the Angular build is served as a static site and
 * the Spring Boot API lives behind `apiBaseUrl`, with MySQL (Azure Database for
 * MySQL) behind that. For production, point `apiBaseUrl` at the deployed API
 * and flip `useMockApi` to false.
 */
export const environment = {
  production: false,

  /** Base URL of the Spring Boot API. */
  apiBaseUrl: '/api',

  /**
   * While the backend is still a skeleton, nominations are persisted to
   * localStorage instead of being POSTed. Set to false as soon as
   * `POST {apiBaseUrl}/nominations` exists.
   */
  useMockApi: true,
};
