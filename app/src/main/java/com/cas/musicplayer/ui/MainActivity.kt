package com.cas.musicplayer.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.di.injector.injector
import com.cas.musicplayer.player.EmplacementFullScreen
import com.cas.musicplayer.player.EmplacementOut
import com.cas.musicplayer.player.EmplacementPlaylist
import com.cas.musicplayer.player.services.DragBottomPanelLiveData
import com.cas.musicplayer.player.services.DragPanelInfo
import com.cas.musicplayer.player.services.PlaybackLiveData
import com.cas.musicplayer.ui.bottompanel.BottomPanelFragment
import com.cas.musicplayer.ui.home.view.InsetSlidingPanelView
import com.cas.musicplayer.utils.*
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {

    lateinit var slidingPaneLayout: InsetSlidingPanelView
    private val viewModel by viewModel { injector.mainViewModel }
    private var isInHome = true
    var isLocked = false
    private lateinit var navController: NavController
    private var isFromService = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        UserPrefs.onLaunchApp()
        UserPrefs.resetNumberOfTrackClick()
        setContentView(R.layout.activity_main)
        slidingPaneLayout = findViewById(R.id.sliding_layout)
        setSupportActionBar(toolbar)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(toolbar, navController)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.id == R.id.dashboardFragment) {
                isInHome = true
                showHomeIcon()
                searchItem?.isVisible = true
            } else {
                isInHome = false
                searchItem?.isVisible = false
            }
        }

        if (!canDrawOverApps()) {
            requestDrawOverAppsPermission()
        }

        setupBottomPanelFragment()
        ViewCompat.setOnApplyWindowInsetsListener(cordinator) { v, insets ->
            if (insets.systemWindowInsetTop > 0) {
                DeviceInset.value = ScreenInset(
                    insets.systemWindowInsetLeft,
                    insets.systemWindowInsetTop,
                    insets.systemWindowInsetRight,
                    insets.systemWindowInsetBottom
                )
            }
            var consumed = false
            val viewGroup = v as ViewGroup
            for (i in 0 until viewGroup.childCount) {
                val child = viewGroup.getChildAt(i)
                // Dispatch the insets to the child
                val childResult = ViewCompat.dispatchApplyWindowInsets(child, insets)
                // If the child consumed the insets, record it
                if (childResult.isConsumed) {
                    consumed = true
                }
            }
            // If any of the children consumed the insets, return
            // an appropriate value
            if (consumed) insets.consumeSystemWindowInsets() else insets
        }

        DeviceInset.observe(this, Observer { inset ->
            sliding_layout.updatePadding(top = inset.top)
        })
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        isFromService = intent?.getBooleanExtra(EXTRAS_FROM_PLAY_SERVICE, false) ?: false
    }

    override fun onResume() {
        super.onResume()

        if (!isFromService) {
            isFromService = intent.getBooleanExtra(EXTRAS_FROM_PLAY_SERVICE, false)
        }

        val emplacement = VideoEmplacementLiveData.oldValue1

        if (emplacement is EmplacementFullScreen || isLandscape()) {
            showStatusBar()
            switchToPortrait()
            VideoEmplacementLiveData.center()
            // To be sure
            handler.postDelayed({
                VideoEmplacementLiveData.center()
            }, 1000)

        } else if (isFromService) {
            isFromService = false

            if (emplacement is EmplacementPlaylist) {
                VideoEmplacementLiveData.playlist()
            } else {
                expandBottomPanel()
                VideoEmplacementLiveData.center()
            }

        } else {
            // Restore old video state if any
            restoreOldVideoState()
        }

        /*if (slidingMenu.isMenuOpened) {
            slidingMenu.closeMenu()
        }*/

        ViewCompat.requestApplyInsets(cordinator)
    }


    override fun onPause() {
        super.onPause()
        // Save current state
        viewModel.lastVideoEmplacement = VideoEmplacementLiveData.value
        // Movable video
        VideoEmplacementLiveData.out()
    }

    private fun setupBottomPanelFragment() {

        var fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.bottomPanelContent)
        if (fragment == null) {
            fragment = BottomPanelFragment()
        }
        supportFragmentManager.beginTransaction().replace(R.id.bottomPanelContent, fragment)
            .commit()

        hideBottomPanel()

        slidingPaneLayout.addPanelSlideListener(object :
            SlidingUpPanelLayout.SimplePanelSlideListener() {
            override fun onPanelSlide(panel: View?, slideOffset: Float) {
                panel?.y?.let { y ->
                    DragBottomPanelLiveData.value = DragPanelInfo(y, slideOffset)
                }
            }

            override fun onPanelStateChanged(
                panel: View?,
                previousState: SlidingUpPanelLayout.PanelState?,
                newState: SlidingUpPanelLayout.PanelState?
            ) {
                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    VideoEmplacementLiveData.center()
                } else if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    VideoEmplacementLiveData.bottom()
                }
            }
        })

        PlaybackLiveData.observe(this, Observer {
            if (it == PlayerConstants.PlayerState.UNKNOWN) {
                hideBottomPanel()
            }
        })

    }


    private fun showHomeIcon() {
        toolbar.setNavigationIcon(R.drawable.ic_menu_toolbar)
        toolbar.title = getString(R.string.app_name)
        toolbar.setNavigationOnClickListener {
            if (isHome()) {

            } else {
                onBackPressed()
            }
        }
    }


    private fun isHome() = navController.currentDestination?.id == R.id.dashboardFragment

    var searchView: SearchView? = null
    var searchItem: MenuItem? = null

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        searchItem = menu.findItem(R.id.searchYoutubeFragment)
        searchView = searchItem?.actionView as SearchView

        searchItem?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                if (!isHome()) {
                    navController.popBackStack()
                }
                return true
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.searchYoutubeFragment && navController.currentDestination?.id != R.id.searchYoutubeFragment) {
            navController.navigate(R.id.searchYoutubeFragment)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (VideoEmplacementLiveData.value is EmplacementFullScreen) {
            showStatusBar()
            switchToPortrait()
            VideoEmplacementLiveData.center()
            return
        }

        if (isBottomPanelExpanded() && !isLocked) {
            collapseBottomPanel()
            return
        } else if (isLocked) {

            return
        }
        super.onBackPressed()
    }

    /**
     * Restore state: Not from service
     */
    private fun restoreOldVideoState() {

        val playBackState = PlaybackLiveData.value
        val lastVideoEmplacement = viewModel.lastVideoEmplacement

        if (lastVideoEmplacement != null && playBackState != null && playBackState != PlayerConstants.PlayerState.UNKNOWN) {

            // Usually for coming from background
            if (lastVideoEmplacement !is EmplacementOut) {
                VideoEmplacementLiveData.value = lastVideoEmplacement
            }

        } else {
            hideBottomPanel()
        }
    }


    fun hideBottomPanel() {
        slidingPaneLayout.panelState = SlidingUpPanelLayout.PanelState.HIDDEN
    }

    fun expandBottomPanel() {
        slidingPaneLayout.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
    }

    fun collapseBottomPanel() {
        if (!canDrawOverApps()) {
            requestDrawOverAppsPermission()
            return
        }
        slidingPaneLayout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
    }

    fun isBottomPanelCollapsed(): Boolean {
        return slidingPaneLayout.panelState == SlidingUpPanelLayout.PanelState.COLLAPSED
    }

    fun isBottomPanelExpanded(): Boolean {
        return slidingPaneLayout.panelState == SlidingUpPanelLayout.PanelState.EXPANDED
    }

    private fun requestDrawOverAppsPermission() {
        AlertDialog.Builder(this).setCancelable(false)
            .setMessage("Please enable the \"Draw over other apps\" permission to start the floating player window.")
            .setNegativeButton("DENY") { _, _ ->
            }.setPositiveButton("AGREE") { _, _ ->
                //If the draw over permission is not available open the settings screen
                //to grant the permission.
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivityForResult(intent, 10)
            }.show()
    }

    companion object {
        const val EXTRAS_FROM_PLAY_SERVICE = "from_player_service"
    }
}
