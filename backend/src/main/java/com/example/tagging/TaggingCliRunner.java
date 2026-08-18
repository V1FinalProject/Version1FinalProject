package com.example.tagging;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

// CLI for exercising the tagging feature against the mock nominations spreadsheet.
// Only runs on the "tagging" profile so it never starts during normal application
// boot, e.g.: mvn spring-boot:run -Dspring-boot.run.profiles=tagging
@Component
@Profile("tagging")
public class TaggingCliRunner implements CommandLineRunner {

    private final NominationExcelLoader loader;
    private final TaggingService taggingService;

    public TaggingCliRunner(NominationExcelLoader loader, TaggingService taggingService) {
        this.loader = loader;
        this.taggingService = taggingService;
    }

    @Override
    public void run(String... args) {
        List<Nomination> nominations = loader.loadAll();
        System.out.println("Loaded " + nominations.size() + " nominations.");

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("Enter a nomination number (1-" + nominations.size() + "), or 'quit' to exit: ");
                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("quit")) {
                    break;
                }

                Optional<Nomination> target = findById(nominations, input);
                if (target.isEmpty()) {
                    System.out.println("No nomination found for input \"" + input
                            + "\". Please enter a number between 1 and " + nominations.size() + ".");
                    continue;
                }

                printFlags(target.get(), nominations);
            }
        }

        System.out.println("Goodbye.");
    }

    private Optional<Nomination> findById(List<Nomination> nominations, String input) {
        try {
            int id = Integer.parseInt(input);
            return nominations.stream().filter(nomination -> nomination.id() == id).findFirst();
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private void printFlags(Nomination nomination, List<Nomination> allNominations) {
        System.out.println();
        System.out.println("Nomination #" + nomination.id() + ": " + nomination.nominatorName() + " -> "
                + nomination.nomineeName() + " (" + nomination.category() + ")");

        List<FlagResult> flags = taggingService.evaluate(nomination, allNominations);

        if (flags.isEmpty()) {
            System.out.println("No flags raised");
        } else {
            for (FlagResult flag : flags) {
                System.out.println("- [" + flag.tagName() + "] " + flag.reasoning());
            }
        }
        System.out.println();
    }
}
