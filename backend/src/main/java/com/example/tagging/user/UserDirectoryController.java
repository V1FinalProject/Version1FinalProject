package com.example.tagging.user;

import com.example.tagging.auth.AccountRole;
import com.example.tagging.nomination.NominationStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

/**
 * Who can be nominated. Backs the nominee picker on the form - the
 * coordinator account is left out here so the UI never offers it as an
 * option, on top of the server-side check in {@link NominationStore#add} that
 * would reject it anyway if someone POSTed around the picker.
 */
@RestController
@RequestMapping("/api/users")
public class UserDirectoryController {

    private final UserAccountRepository users;

    public UserDirectoryController(UserAccountRepository users) {
        this.users = users;
    }

    @GetMapping("/nominatable")
    public List<NominatableUser> nominatable() {
        return users.findAll().stream()
                .filter(account -> account.getRole() != AccountRole.COORDINATOR)
                .map(NominatableUser::from)
                .sorted(Comparator.comparing(NominatableUser::name))
                .toList();
    }
}
