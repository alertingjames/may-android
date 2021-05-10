package bookclub.technion.maymsgphoto.provider;

/**
 * Created by a on 5/14/2017.
 */

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by sodha on 4/3/16.
 */
public class SuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "bookclub.technion.maymessageapp.provider.SuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
