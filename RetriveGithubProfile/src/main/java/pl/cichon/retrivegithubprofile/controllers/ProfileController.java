package pl.cichon.retrivegithubprofile.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.cichon.retrivegithubprofile.data.GHProfile;
import pl.cichon.retrivegithubprofile.exceptions.ProfileNotFoundException;
import pl.cichon.retrivegithubprofile.services.ProfileService;

import java.util.List;

@RequestMapping("/profile")
@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/{name}")
    public List<GHProfile> getProfile(@PathVariable String name) throws ProfileNotFoundException {
        return profileService.getGHProfiles(name);
    }
}
