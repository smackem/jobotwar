package net.smackem.jobotwar.web.persist.memory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * This class is not thread-safe.
 */
class StringMatchFilter extends TypedFilter<String, String> {
    private final Map<String, Pattern> patternCache = new HashMap<>();

    StringMatchFilter(Function<Object, Object> left, Function<Object, Object> right) {
        super(left, right, String.class, String.class);
    }

    @Override
    protected boolean matches(String left, String right) {
        final Pattern pattern = this.patternCache.computeIfAbsent(right, s ->
                Pattern.compile(s.replace("*", ".*?").replace("?", "."),
                        Pattern.CASE_INSENSITIVE));
        return pattern.matcher(left).matches();
    }
}
