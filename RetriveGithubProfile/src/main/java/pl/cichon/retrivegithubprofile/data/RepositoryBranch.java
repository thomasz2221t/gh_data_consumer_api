package pl.cichon.retrivegithubprofile.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RepositoryBranch {
    public String name;
    public String sha;

    public RepositoryBranch(String name, String sha) {
        this.name = name;
        this.sha = sha;
    }
}
