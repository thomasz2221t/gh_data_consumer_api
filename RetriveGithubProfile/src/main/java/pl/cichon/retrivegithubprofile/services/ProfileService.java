package pl.cichon.retrivegithubprofile.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import pl.cichon.retrivegithubprofile.data.GHProfile;
import pl.cichon.retrivegithubprofile.data.RepositoryBranch;
import pl.cichon.retrivegithubprofile.exceptions.ProfileNotFoundException;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProfileService {
    
    @Value("${github.api.url}")
    private String API_URL;
    private final String SEPARATOR = "/";

    public List<GHProfile> getGHProfiles(String userNickname) throws ProfileNotFoundException {
        String url = API_URL + "users" + SEPARATOR + userNickname + SEPARATOR + "repos";
        try {
            List<Map<String, Object>> response = getUserDataFromAPI(url);

            return getProfileDetails(response);
        } catch (RestClientException e) {
            throw new ProfileNotFoundException(e.getMessage());
        }
    }

    public List<Map<String, Object>> getUserDataFromAPI(String url) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        return restTemplate.getForObject(url, List.class, headers);
    }

    private List<GHProfile> getProfileDetails(List<Map<String, Object>> response) {
        return Optional.ofNullable(response)
                .orElse(Collections.emptyList())
                .stream()
                .filter(repository -> Objects.equals(repository.get("fork").toString(), "false"))
                .map(repository -> {
                    String repositoryName = repository.get("name").toString();
                    String ownerLogin = repository.containsKey("owner") && repository.get("owner") instanceof Map<?, ?>
                            ? ((Map<String, Object>) repository.get("owner")).get("login").toString()
                            : "";
                    List<RepositoryBranch> branches = getRepositoryBranches(ownerLogin, repositoryName);

                    return new GHProfile(repositoryName, ownerLogin, branches);
                }).toList();
    }

    private List<RepositoryBranch> getRepositoryBranches(String ownerLogin, String repositoryName) {
        String branchUrl = API_URL + "repos" + SEPARATOR + ownerLogin + SEPARATOR +  repositoryName + SEPARATOR + "branches" ;
        List<Map<String, Object>> branchResponse = getUserDataFromAPI(branchUrl);

        return Optional.ofNullable(branchResponse)
                .orElse(Collections.emptyList())
                .stream()
                .map(branch -> {
                    String branchName = branch.get("name").toString();
                    String lastCommit = branch.containsKey("commit") && branch.get("commit") instanceof Map<?, ?>
                            ? ((Map<String, Object>) branch.get("commit")).get("sha").toString()
                            : "";
                    return new RepositoryBranch(branchName, lastCommit);
                }).toList();
    }
}