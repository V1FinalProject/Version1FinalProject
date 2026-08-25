package com.example.tagging;

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
record Quarter(int year, int number) implements Comparable<Quarter> {

    private static final Pattern LABEL = Pattern.compile("Q([1-4])\\s+(\\d{4})");

    static Quarter parse(String label) {
        Matcher matcher = LABEL.matcher(label == null ? "" : label.trim());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Not a quarter label (expected \"Q<1-4> <year>\"): \"" + label + "\"");
        }
        return new Quarter(Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(1)));
    }

    Quarter previous() {
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
