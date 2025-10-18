package dogapi;

import java.io.IOException;
import java.util.*;

/**
 * This BreedFetcher caches fetch request results to improve performance and
 * lessen the load on the underlying data source. An implementation of BreedFetcher
 * must be provided. The number of calls to the underlying fetcher are recorded.
 *
 * If a call to getSubBreeds produces a BreedNotFoundException, then it is NOT cached
 * in this implementation. The provided tests check for this behaviour.
 *
 * The cache maps the name of a breed to its list of sub breed names.
 */
public class CachingBreedFetcher implements BreedFetcher {

    private final BreedFetcher delegate;
    private final Map<String, List<String>> cache = new HashMap<>();

    private int callsMade = 0;
    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.delegate = fetcher;
    }

    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        // return statement included so that the starter code can compile and run.
        if (breed == null) throw new IllegalArgumentException("breed is null");
        String key = breed.trim().toLowerCase();

        List<String> result = cache.get(key);
        if (result != null) return result;

        callsMade++;
        List<String> newResult;
        newResult = delegate.getSubBreeds(key);
        List<String> immutable = Collections.unmodifiableList(newResult);
        cache.put(key, immutable);
        return immutable;

    }

    public int getCallsMade() {
        return callsMade;
    }
}