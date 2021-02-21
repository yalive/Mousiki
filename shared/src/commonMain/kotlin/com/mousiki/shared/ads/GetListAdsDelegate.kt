package com.mousiki.shared.ads

import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.ui.resource.Resource
import kotlinx.coroutines.flow.MutableStateFlow

/**
 ***************************************
 * Created by Y.Abdelhadi on 5/3/20.
 ***************************************
 */
interface GetListAdsDelegate {
    suspend fun populateAdsIn(resource: MutableStateFlow<Resource<List<DisplayableItem>>?>)
    suspend fun insertAdsIn(items: List<DisplayableItem>): List<DisplayableItem>
}
