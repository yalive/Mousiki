package com.secureappinc.musicplayer.ui.charts

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.SingleFragmentActivity
import com.secureappinc.musicplayer.ui.home.models.ChartModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`

/**
 * **************************************
 * Created by Abdelhadi on 2019-06-19.
 * **************************************
 */
@RunWith(AndroidJUnit4::class)
class ChartsFragmentTest {

    val charts = MutableLiveData<List<ChartModel>>()
    val chartDetail = MutableLiveData<ChartModel>()

    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val rule = ActivityTestRule<SingleFragmentActivity>(SingleFragmentActivity::class.java)

    private val fragment = ChartsFragment()

    @Before
    fun setUp() {
        rule.doOnFragmentAttached {
            val viewModel = fragment.viewModel
            `when`(viewModel.charts).thenReturn(charts)
            `when`(viewModel.chartDetail).thenReturn(chartDetail)
        }
        rule.activity.setFragment(fragment)
    }

    @UiThreadTest
    @Test
    fun testDummy() {
        charts.postValue(
            listOf(
                ChartModel("Title 0", R.drawable.img_chart_0, "0"),
                ChartModel("Title 1", R.drawable.img_chart_0, "1"),
                ChartModel("Title 2", R.drawable.img_chart_0, "2"),
                ChartModel("Title 3", R.drawable.img_chart_0, "3"),
                ChartModel("Title 4", R.drawable.img_chart_0, "4"),
                ChartModel("Title 5", R.drawable.img_chart_0, "5")
            )
        )
    }
}

fun ActivityTestRule<SingleFragmentActivity>.doOnFragmentAttached(onAttached: () -> Unit) {
    activity.supportFragmentManager.registerFragmentLifecycleCallbacks(object :
        FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentAttached(fm: FragmentManager, f: Fragment, context: Context) {
            onAttached()
        }
    }, true)
}
