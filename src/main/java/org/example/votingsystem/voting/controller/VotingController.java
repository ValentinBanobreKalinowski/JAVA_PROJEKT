package org.example.votingsystem.voting.controller;

import org.example.votingsystem.voting.model.*;
import org.example.votingsystem.voting.services.VotingService;
import org.example.votingsystem.user.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    public String createVoteForm() {
        return "create-vote";
    }

    @PostMapping("/admin/create-vote")
    @PreAuthorize("hasRole('ADMIN')")
    public String createVote(@RequestParam String title,
                           @RequestParam(required = false) String description,
                           @RequestParam String startTime,
                           @RequestParam String endTime,
                           @RequestParam VotingType type,
                           @RequestParam("options[]") List<String> options,
                           RedirectAttributes redirectAttributes) {

        Voting voting = new Voting();
        voting.setTitle(title);
        voting.setDescription(description);
        voting.setStartTime(LocalDateTime.parse(startTime));
        voting.setEndTime(LocalDateTime.parse(endTime));
        voting.setType(type);
        voting.setOptions(options);

        votingService.createVoting(voting);
        redirectAttributes.addFlashAttribute("success", "Głosowanie utworzone pomyślnie!");
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/admin/finish-vote/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String finishVote(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        votingService.finishVote(id);
        redirectAttributes.addFlashAttribute("success", "Głosowanie zakończone pomyślnie!");
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
            redirectAttributes.addFlashAttribute("error", "Już zagłosowałeś!");
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
            String option = chosenOption != null ? chosenOption : String.join(",", chosenOptions);
            votingService.castVote(user, id, option);
            redirectAttributes.addFlashAttribute("success", "Głos oddany pomyślnie!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/user/votes";
    }

    @GetMapping("/user/results/{id}")
    public String userViewResults(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Voting voting = votingService.getVotingById(id);

        if (voting.getEndTime().isAfter(LocalDateTime.now())) {
            redirectAttributes.addFlashAttribute("error", "Wyniki będą dostępne po zakończeniu głosowania!");
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

    @GetMapping("/admin/edit-vote/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editVoteForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Voting voting = votingService.getVotingById(id);

        if (!votingService.canEditVoting(id)) {
            redirectAttributes.addFlashAttribute("error", "Nie można edytować głosowania, które już się rozpoczęło lub zakończyło.");
            return "redirect:/admin/dashboard";
        }

        model.addAttribute("voting", voting);
        return "edit-vote";
    }

    @PostMapping("/admin/edit-vote/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editVote(@PathVariable Long id,
                          @RequestParam String title,
                          @RequestParam(required = false) String description,
                          @RequestParam String startTime,
                          @RequestParam String endTime,
                          @RequestParam VotingType type,
                          @RequestParam("options[]") List<String> options,
                          RedirectAttributes redirectAttributes) {
        try {
            Voting updatedVoting = new Voting();
            updatedVoting.setTitle(title);
            updatedVoting.setDescription(description);
            updatedVoting.setStartTime(LocalDateTime.parse(startTime));
            updatedVoting.setEndTime(LocalDateTime.parse(endTime));
            updatedVoting.setType(type);
            updatedVoting.setOptions(options);

            votingService.updateVoting(id, updatedVoting);
            redirectAttributes.addFlashAttribute("success", "Głosowanie zaktualizowane pomyślnie!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }
}
