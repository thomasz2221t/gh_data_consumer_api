package pl.cichon.retrivegithubprofile;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@ExtendWith(WireMockExtension.class)
public class ProfileIntegrationTest {

    @LocalServerPort
    private int port;

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().port(61432))
            .build();

    @Autowired
    private TestRestTemplate testRestTemplate;

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("github.api.url", () -> "https://api.github.com/");
    }

    @BeforeAll
    public static void setup() {
        wireMockServer.stubFor(get(urlEqualTo("http://localhost:61432/users/testuser/repos"))
                        .withHeader("Accept", matching("application/json"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("""
                                    [
                                        {
                                            "name": "repo1",
                                            "owner": { "login": "testuser" },
                                            "fork": false
                                        },
                                        {
                                            "name": "repo2",
                                            "owner": { "login": "testuser" },
                                            "fork": true
                                        },
                                        {
                                            "name": "repo3",
                                            "owner": { "login": "testuser" },
                                            "fork": false
                                        }
                                    ]
                                    """)
                        ));
        wireMockServer.stubFor(get(urlEqualTo("http://localhost:61432/repos/testuser/repo1/branches"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("""
                                    [
                                        {
                                            "name": "main",
                                            "commit": { "sha": "7b6fe5b7222a9b5a5f63aa3821abf10f8d9c1f63" },
                                        },
                                        {
                                            "name": "branch1",
                                            "commit": { "sha": "72e8fec5d9bf967fa1f3e8f3662be098a633a877" },
                                        },
                                        {
                                            "name": "branch2",
                                            "commit": { "sha": "166298620ea4ff4bc63870d909656c81a7582031" },
                                        }
                                    ]
                                    """)
                        ));

        wireMockServer.stubFor(get(urlEqualTo("/repos/testuser/repo2/branches"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("""
                                    [
                                        {
                                            "name": "main",
                                            "commit": { "sha": "7b6fe5b7222a9b5a5f63aa3821abf10f8d9c1f64" },
                                        },
                                        {
                                            "name": "branch1",
                                            "commit": { "sha": "72e8fec5d9bf967fa1f3e8f3662be098a633a878" },
                                        },
                                        {
                                            "name": "branch2",
                                            "commit": { "sha": "166298620ea4ff4bc63870d909656c81a7582039" },
                                        }
                                    ]
                                    """)
                        ));

        wireMockServer.stubFor(get(urlEqualTo("http://localhost:61432/repos/testuser/repo3/branches"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("""
                                    [
                                        {
                                            "name": "main",
                                            "commit": { "sha": "7b6fe5b7222a9b5a5f63aa3821abf10f8d9c1f59" },
                                        },
                                        {
                                            "name": "branch1",
                                            "commit": { "sha": "72e8fec5d9bf967fa1f3e8f3662be098a633a866" },
                                        },
                                        {
                                            "name": "branch2",
                                            "commit": { "sha": "166298620ea4ff4bc63870d909656c81a7582022" },
                                        }
                                    ]
                                    """)
                        ));

        wireMockServer.stubFor(get(urlEqualTo("http://localhost:61432/users/unknownuser/repos"))
                .withHeader("Accept", matching("application/json"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withBody("""
                                {
                                    "status": "404",
                                    "message": "User not found"
                                }
                                """)));
    }

    @Test
    public void givenCorrectName_whenGettingProfile_thenShouldReturnProfileWithNonForkRepositories() {
        String url = "http://localhost:" + port + "/profile/thomasz2221t";

        ResponseEntity<String> response = testRestTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsIgnoringWhitespaces("""
                [{"repositoryName":"cvapr_car_parts_detection","ownerLogin":"thomasz2221t","branches":[{"name":"gui","sha":"db2be5abdc1c61a3f03ee0db1d8c6ff90ee2a90a"},{"name":"main","sha":"42469b85544889ca908c44edb384f6373e0f3ffe"}]},
                """);
    }

    @Test
    public void givenNonExistingUser_whenGettingProfile_thenShouldReturnHttp404 () {
        String url = "http://localhost:" + port + "/profile/thomaszz2221t";

        ResponseEntity<String> response = testRestTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualToIgnoringWhitespace("""
                    {
                        "status": 404,
                        "message": "User profile not found"
                    }
                """);
    }
}
