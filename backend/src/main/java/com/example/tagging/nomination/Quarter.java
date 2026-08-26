package com.example.tagging.nomination;

import com.example.tagging.review.ReciprocityService;
import com.example.tagging.flagging.RepeatNominationChecker;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A programme quarter like {@code "Q3 2026"} - the nomination round label the
 * frontend sends as {@code CURRENT_QUARTER}, not a calendar quarter derived
 * from a timestamp. The two don't line up (the programme's quarters don't
 * follow the calendar), so every {@link Nomination} carries its own quarter
 * label rather than one being inferred from {@code timestamp}.
 *
 * Comparable so "the quarter before this one" and "is this nomination within
 * the last N quarters" are simple to express - used by
 * {@link RepeatNominationChecker} and {@link ReciprocityService}.
 */
public record Quarter(int year, int number) implements Comparable<Quarter> {

    /**
     * The programme round currently open for nominating and reviewing - the
     * backend's copy of what the frontend calls {@code CURRENT_QUARTER}.
     * There's no way to share one constant across the language boundary, so
     * this is updated by hand alongside the frontend's each round, same as
     * {@link NominationExcelLoader}'s old per-loader copy this replaces.
     */
    public static final String CURRENT_QUARTER = "Q4 2026";

    private static final Pattern LABEL = Pattern.compile("Q([1-4])\\s+(\\d{4})");

    public static Quarter parse(String label) {
        Matcher matcher = LABEL.matcher(label == null ? "" : label.trim());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Not a quarter label (expected \"Q<1-4> <year>\"): \"" + label + "\"");
        }
        return new Quarter(Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(1)));
    }

    public Quarter previous() {
        return number == 1 ? new Quarter(year - 1, 4) : new Quarter(year, number - 1);
    }

    @Override
    public int compareTo(Quarter other) {
        return year != other.year ? Integer.compare(year, other.year) : Integer.compare(number, other.number);
    }

    @Override
    public String toString() {
        return "Q" + number + " " + year;
    }
}
