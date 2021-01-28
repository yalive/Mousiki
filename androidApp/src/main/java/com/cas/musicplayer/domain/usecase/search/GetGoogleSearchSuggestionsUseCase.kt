package com.cas.musicplayer.domain.usecase.search

import com.cas.musicplayer.data.repositories.SearchRepository
import com.mousiki.shared.utils.getCurrentLocale
import com.cas.musicplayer.utils.getLanguage
import javax.inject.Inject

/**
 *********************************************
 * Created by Abdelhadi on 2019-11-20.
 *
 *********************************************
 */
class GetGoogleSearchSuggestionsUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    suspend operator fun invoke(query: String): List<String> {
        val url =
            "https://clients1.google.com/complete/search?client=youtube&hl=${getLanguage()}&ql=${getCurrentLocale()}&gs_rn=23&gs_ri=youtube&ds=yt&cp=${query.length}&q=${query.replace(
                " ",
                "+"
            )}"
        return repository.getSuggestions(url)
    }
}