package pl.cichon.retrivegithubprofile.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class GHProfile {
    public String repositoryName;
    public String ownerLogin;
    public List<RepositoryBranch> branches;

    public GHProfile(String name, String ownerLogin, List<RepositoryBranch> branches) {
        this.repositoryName = name;
        this.ownerLogin = ownerLogin;
        this.branches = branches;
    }
}
