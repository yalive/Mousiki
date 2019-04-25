package com.secureappinc.musicplayer.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.dpToPixel
import com.secureappinc.musicplayer.models.EmplacementOut
import com.secureappinc.musicplayer.models.EmplacementPlaylist
import com.secureappinc.musicplayer.services.DragBottomPanelLiveData
import com.secureappinc.musicplayer.services.DragPanelInfo
import com.secureappinc.musicplayer.services.DragSlidePanelMonitor
import com.secureappinc.musicplayer.services.PlaybackLiveData
import com.secureappinc.musicplayer.ui.bottompanel.BottomPanelFragment
import com.secureappinc.musicplayer.utils.*
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.yarolegovich.slidingrootnav.SlidingRootNav
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder
import com.yarolegovich.slidingrootnav.callback.DragStateListener
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    companion object {
        const val EXTRAS_FROM_PLAY_SERVICE = "from_player_service"
    }

    private val tag = "MainActivity"
    private var contentMenu: ViewGroup? = null
    private lateinit var slidingMenu: SlidingRootNav
    private lateinit var navController: NavController

    private val handler = Handler()

    lateinit var slidingPaneLayout: SlidingUpPanelLayout

    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(com.secureappinc.musicplayer.R.layout.activity_main)
        slidingPaneLayout = findViewById(R.id.sliding_layout)

        setSupportActionBar(toolbar)

        collapsingToolbar.isTitleEnabled = true
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        setupMenu()
        contentMenu = findViewById<FrameLayout>(com.secureappinc.musicplayer.R.id.contentMenu)

        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(toolbar, navController)


        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.id == R.id.dashboardFragment) {
                showHomeIcon()
            }
        }

        if (!canDrawOverApps() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AlertDialog.Builder(this).setCancelable(false)
                .setMessage("Please enable the \"Draw over other apps\" permission to start the floating player window.")
                .setNegativeButton("DENY") { _, _ ->
                }.setPositiveButton("AGREE") { _, _ ->
                    //If the draw over permission is not available open the settings screen
                    //to grant the permission.
                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                    startActivityForResult(intent, 10)
                }.show()
        }


        setupBottomPanelFragment()
    }

    var isFromService = false

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        isFromService = intent?.getBooleanExtra(EXTRAS_FROM_PLAY_SERVICE, false) ?: false
    }

    override fun onResume() {
        super.onResume()

        if (!isFromService) {
            isFromService = intent.getBooleanExtra(EXTRAS_FROM_PLAY_SERVICE, false)
        }

        if (isFromService) {
            isFromService = false

            val emplacement = VideoEmplacementLiveData.oldValue1

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
        supportFragmentManager.beginTransaction().replace(R.id.bottomPanelContent, fragment).commit()

        hideBottomPanel()

        slidingPaneLayout.addPanelSlideListener(object : SlidingUpPanelLayout.SimplePanelSlideListener() {
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
                toggleMenu()
            } else {
                onBackPressed()
            }
        }
    }

    private fun toggleMenu() {
        if (slidingMenu.isMenuOpened) {
            slidingMenu.closeMenu()
        } else {
            slidingMenu.openMenu()
        }
    }

    private fun isHome() = navController.currentDestination?.id == R.id.dashboardFragment

    private fun setupMenu() {
        slidingMenu = SlidingRootNavBuilder(this)
            .addDragStateListener(object : DragStateListener {
                override fun onDragEnd(isMenuOpened: Boolean) {
                    if (isMenuOpened) {
                        viewDetectGesture.visible()
                    } else {
                        viewDetectGesture.gone()
                    }
                }

                override fun onDragStart() {
                }
            })
            .withMenuLayout(com.secureappinc.musicplayer.R.layout.menu_left_drawer)
            .withDragDistance(240)
            .withRootViewScale(0.8f)
            .addDragListener {
                Log.d(tag, "Progress=$it")
                contentMenu?.translationX = dpToPixel(260 * (it - 1), this)
                contentMenu?.scaleX = 1.0f - 0.4f * (1 - it)
                contentMenu?.scaleY = 1.0f - 0.4f * (1 - it)

                DragSlidePanelMonitor.value = it
            }
            .inject();

        findViewById<View>(R.id.btnEqualizer).setOnClickListener {
            Utils.openEqualizer(this)
        }

        findViewById<View>(R.id.btnShareApp).setOnClickListener {
            Utils.shareAppVia()
        }

        findViewById<View>(R.id.btnShareFeedback).setOnClickListener {
            Utils.shareFeedback()
        }

        findViewById<View>(R.id.btnSleepTimer).setOnClickListener {

        }

        findViewById<View>(R.id.btnPrivacyPolicy).setOnClickListener {
            Utils.openWebview(
                this,
                "http://secureappinc.com/privacy_policy_fr.html"
            )
        }

        findViewById<View>(R.id.btnSettings).setOnClickListener {
        }

        findViewById<View>(R.id.btnLibrary).setOnClickListener {
            if (navController.currentDestination?.id != R.id.dashboardFragment) {
                slidingMenu.closeMenu()
                handler.postDelayed({
                    navController.popBackStack(R.id.dashboardFragment, false)
                }, 500)

            }
        }

        viewDetectGesture.setOnTouchListener { v, event ->
            slidingMenu.closeMenu()
            viewDetectGesture.gone()
            true
        }
    }

    var searchView: SearchView? = null

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(com.secureappinc.musicplayer.R.menu.menu_toolbar, menu)
        val searchItem = menu.findItem(R.id.searchYoutubeFragment)
        searchView = searchItem.actionView as SearchView

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
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

    /**
     * Restore state: Not from service
     */
    private fun restoreOldVideoState() {

        val oldValue1 = VideoEmplacementLiveData.oldValue1
        val oldValue2 = VideoEmplacementLiveData.oldValue2

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

    fun showBottomPanel() {
        slidingPaneLayout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
    }

    fun isBottomPanelCollapsed(): Boolean {
        return slidingPaneLayout.panelState == SlidingUpPanelLayout.PanelState.COLLAPSED
    }
}
