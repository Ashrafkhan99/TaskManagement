package com.at.taskmanagement.controller;

import com.at.taskmanagement.entity.Team;
import com.at.taskmanagement.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    public ResponseEntity<Team> createTeam(@RequestParam String name,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(teamService.createTeam(name, userDetails));
    }

    @PostMapping("/add-member")
    public ResponseEntity<Team> addMember(@RequestParam UUID userId,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(teamService.addMember(userId, userDetails));
    }
}
