package org.example.votingsystem.voting.controller;

import jakarta.validation.Valid;
import org.example.votingsystem.voting.dto.VotingCreateDto;
import org.example.votingsystem.voting.model.*;
import org.example.votingsystem.voting.services.VotingService;
import org.example.votingsystem.user.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
public class VotingController {

    private final VotingService votingService;

    public VotingController(VotingService votingService) {
        this.votingService = votingService;
    }

    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDashboard(Model model) {
        List<Voting> votings = votingService.getAllVotings();
        model.addAttribute("votings", votings);
        return "admin-dashboard";
    }

    @GetMapping("/admin/create-vote")
    @PreAuthorize("hasRole('ADMIN')")
    public String createVoteForm(Model model) {
        model.addAttribute("votingForm", new VotingCreateDto());
        return "create-vote";
    }

    @PostMapping("/admin/create-vote")
    @PreAuthorize("hasRole('ADMIN')")
    public String createVote(@Valid @ModelAttribute("votingForm") VotingCreateDto dto,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "create-vote";
        }

        List<String> filteredOptions = dto.getOptions().stream()
                .filter(opt -> opt != null && !opt.trim().isEmpty())
                .toList();

        if (filteredOptions.size() < 2) {
            bindingResult.rejectValue("options", "error.options", "Musisz podać co najmniej 2 niepuste opcje.");
            return "create-vote";
        }

        Voting voting = new Voting();
        voting.setTitle(dto.getTitle());
        voting.setDescription(dto.getDescription());
        voting.setStartTime(dto.getStartTime());
        voting.setEndTime(dto.getEndTime());
        voting.setType(dto.getType());
        voting.setOptions(filteredOptions);

        try {
            votingService.createVoting(voting);
            redirectAttributes.addFlashAttribute("success", "Vote created successfully!");
            return "redirect:/admin/dashboard";
        } catch (IllegalArgumentException e) {
            model.addAttribute("businessError", e.getMessage());
            return "create-vote";
        }
    }

    @PostMapping("/admin/finish-vote/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String finishVote(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        votingService.finishVote(id);
        redirectAttributes.addFlashAttribute("success", "Vote finished successfully!");
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/admin/vote-results/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminViewResults(@PathVariable Long id, Model model) {
        Voting voting = votingService.getVotingById(id);
        Map<String, Integer> results = votingService.calculateResults(id);

        int totalVotes = results.values().stream().mapToInt(Integer::intValue).sum();
        int maxVotes = results.values().stream().mapToInt(Integer::intValue).max().orElse(0);

        model.addAttribute("voting", voting);
        model.addAttribute("results", results);
        model.addAttribute("totalVotes", totalVotes);
        model.addAttribute("maxVotes", maxVotes);
        model.addAttribute("isActive", voting.getEndTime().isAfter(LocalDateTime.now()));
        model.addAttribute("isAdmin", true);
        return "vote-results";
    }

    @GetMapping("/user/votes")
    @PreAuthorize("hasRole('USER')")
    public String userVotes(@AuthenticationPrincipal User user, Model model) {
        List<Voting> votings = votingService.getActiveVotings();
        votings.forEach(v -> v.setHasVoted(votingService.hasUserVoted(user.getId(), v.getId())));
        model.addAttribute("votings", votings);
        return "user-votes";
    }

    @GetMapping("/user/vote/{id}")
    @PreAuthorize("hasRole('USER')")
    public String castVoteForm(@PathVariable Long id, @AuthenticationPrincipal User user, Model model, RedirectAttributes redirectAttributes) {
        if (votingService.hasUserVoted(user.getId(), id)) {
            redirectAttributes.addFlashAttribute("error", "You have already voted!");
            return "redirect:/user/votes";
        }

        Voting voting = votingService.getVotingById(id);
        model.addAttribute("voting", voting);
        return "cast-vote";
    }

    @PostMapping("/user/vote/{id}")
    @PreAuthorize("hasRole('USER')")
    public String castVote(@PathVariable Long id,
                           @RequestParam(required = false) String chosenOption,
                           @RequestParam(required = false) List<String> chosenOptions,
                           @AuthenticationPrincipal User user,
                           RedirectAttributes redirectAttributes) {
        try {
            String option = chosenOption != null ? chosenOption : (chosenOptions != null ? String.join(",", chosenOptions) : null);

            if (option == null || option.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Musisz wybrać jakąś opcję, aby zagłosować!");
                return "redirect:/user/vote/" + id;
            }

            votingService.castVote(user, id, option);
            redirectAttributes.addFlashAttribute("success", "Vote cast successfully!");
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/user/votes";
    }

    @GetMapping("/user/results/{id}")
    public String userViewResults(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Voting voting = votingService.getVotingById(id);

        if (voting.getEndTime().isAfter(LocalDateTime.now())) {
            redirectAttributes.addFlashAttribute("error", "Results are not available until voting ends!");
            return "redirect:/user/votes";
        }

        Map<String, Integer> results = votingService.calculateResults(id);

        int totalVotes = results.values().stream().mapToInt(Integer::intValue).sum();
        int maxVotes = results.values().stream().mapToInt(Integer::intValue).max().orElse(0);

        model.addAttribute("voting", voting);
        model.addAttribute("results", results);
        model.addAttribute("totalVotes", totalVotes);
        model.addAttribute("maxVotes", maxVotes);
        model.addAttribute("isActive", false);
        model.addAttribute("isAdmin", false);
        return "vote-results";
    }

    @GetMapping("/user/history")
    @PreAuthorize("hasRole('USER')")
    public String votingHistory(@AuthenticationPrincipal User user, Model model) {
        List<Voting> endedVotings = votingService.getEndedVotings();
        endedVotings.forEach(v -> v.setHasVoted(votingService.hasUserVoted(user.getId(), v.getId())));
        model.addAttribute("votings", endedVotings);
        return "voting-history";
    }
}