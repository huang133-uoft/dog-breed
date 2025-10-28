package dogapi;

import java.io.IOException;
import java.util.List;

/**
 * Interface for the service of getting sub breeds of a given dog breed.
 */
public interface BreedFetcher {

    /**
     * Fetch the list of sub breeds for the given breed.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed (may be empty but never null)
     * @throws BreedNotFoundException if the breed does not exist or the fetch fails
     */
    List<String> getSubBreeds(String breed) throws BreedNotFoundException;

    // NOTE: make this a *checked* exception (extends Exception, not RuntimeException)
    class BreedNotFoundException extends Exception {
        public BreedNotFoundException(String breed) {
            super("Breed not found: " + breed);
        }
        public BreedNotFoundException(String breed, Throwable cause) {
            super("Breed not found: " + breed, cause);
        }
        public BreedNotFoundException(String message, boolean raw) {  // helper for custom messages
            super(message);
        }
    }
}