package com.mousiki.shared.domain.usecase.search

import com.mousiki.shared.data.repository.SearchRepository
import com.mousiki.shared.utils.getCurrentLocale
import com.mousiki.shared.utils.getLanguage

/**
 *********************************************
 * Created by Abdelhadi on 2019-11-20.
 *
 *********************************************
 */
class GetGoogleSearchSuggestionsUseCase(
    private val repository: SearchRepository
) {
    suspend operator fun invoke(query: String): List<String> {
        val url =
            "https://clients1.google.com/complete/search?client=youtube&hl=${getLanguage()}&ql=${getCurrentLocale()}&gs_rn=23&gs_ri=youtube&ds=yt&cp=${query.length}&q=${
                query.replace(
                    " ",
                    "+"
                )
            }"
        return repository.getSuggestions(url)
    }
}