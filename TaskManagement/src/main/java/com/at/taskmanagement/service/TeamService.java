package com.at.taskmanagement.service;

import com.at.taskmanagement.entity.Team;
import com.at.taskmanagement.entity.User;
import com.at.taskmanagement.repos.TeamRepository;
import com.at.taskmanagement.repos.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public Team createTeam(String teamName, UserDetails userDetails) {
        User creator = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (creator.getTeam() != null) {
            throw new RuntimeException("You are already part of a team");
        }

        if (teamRepository.findByName(teamName).isPresent()) {
            throw new RuntimeException("Team name already exists");
        }

        Team team = Team.builder()
                .name(teamName)
                .users(new java.util.ArrayList<>())
                .build();

        team.getUsers().add(creator);
        creator.setTeam(team);

        teamRepository.save(team); // cascade saves the user update too
        return team;
    }

    public Team addMember(UUID userId, UserDetails requesterDetails) {
        User requester = userRepository.findByUsername(requesterDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Requesting user not found"));

        if (requester.getTeam() == null) {
            throw new RuntimeException("You must be in a team to add others");
        }

        User userToAdd = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Target user not found"));

        if (userToAdd.getTeam() != null) {
            throw new RuntimeException("User already belongs to another team");
        }

        userToAdd.setTeam(requester.getTeam());
        userRepository.save(userToAdd);

        return requester.getTeam();
    }
}
