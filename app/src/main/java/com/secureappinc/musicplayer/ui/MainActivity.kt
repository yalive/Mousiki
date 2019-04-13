package com.secureappinc.musicplayer.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.dpToPixel
import com.secureappinc.musicplayer.models.EmplacementOut
import com.secureappinc.musicplayer.services.DragBottomPanelLiveData
import com.secureappinc.musicplayer.services.DragPanelInfo
import com.secureappinc.musicplayer.services.PlaybackLiveData
import com.secureappinc.musicplayer.ui.bottompanel.BottomPanelFragment
import com.secureappinc.musicplayer.utils.VideoEmplacementLiveData
import com.secureappinc.musicplayer.utils.canDrawOverApps
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.yarolegovich.slidingrootnav.SlidingRootNav
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    companion object {
        const val EXTRAS_FROM_PLAY_SERVICE = "from_player_service"
    }

    private val tag = "MainActivity"
    private var contentMenu: ViewGroup? = null
    private lateinit var slidingMenu: SlidingRootNav
    private lateinit var navController: NavController

    lateinit var slidingPaneLayout: SlidingUpPanelLayout

    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.secureappinc.musicplayer.R.layout.activity_main)
        slidingPaneLayout = findViewById(R.id.sliding_layout)

        setSupportActionBar(toolbar)

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
            // For now show large video
            // TODO: Support when playlist is visible
            slidingPaneLayout.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
            VideoEmplacementLiveData.center()
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

        slidingPaneLayout.panelState = SlidingUpPanelLayout.PanelState.HIDDEN

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
    }


    private fun showHomeIcon() {
        toolbar.setNavigationIcon(R.drawable.ic_menu_toolbar)
        toolbar.title = "Music Player"
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
            .withMenuLayout(com.secureappinc.musicplayer.R.layout.menu_left_drawer)
            .withDragDistance(240)
            .withRootViewScale(0.9f)
            .addDragListener {
                Log.d(tag, "Progress=$it")
                contentMenu?.translationX = dpToPixel(260 * (it - 1), this)
                contentMenu?.scaleX = 1.0f - 0.4f * (1 - it)
                contentMenu?.scaleY = 1.0f - 0.4f * (1 - it)
            }
            .inject();
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(com.secureappinc.musicplayer.R.menu.menu_toolbar, menu)
        return true
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
            slidingPaneLayout.panelState = SlidingUpPanelLayout.PanelState.HIDDEN
        }
    }
}
