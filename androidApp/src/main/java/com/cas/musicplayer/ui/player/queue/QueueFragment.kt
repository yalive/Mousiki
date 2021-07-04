package com.cas.musicplayer.ui.player.queue

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.addRipple
import com.cas.common.extensions.onClick
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.FragmentQueueBinding
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.player.iconId
import com.cas.musicplayer.player.services.PlaybackLiveData
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.tmp.observeEvent
import com.cas.musicplayer.ui.base.darkStatusBar
import com.cas.musicplayer.ui.bottomsheet.TrackOptionsFragment
import com.cas.musicplayer.ui.popular.SongsDiffUtil
import com.cas.musicplayer.utils.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.domain.models.imgUrlDefault
import com.mousiki.shared.preference.UserPrefs
import com.mousiki.shared.utils.AnalyticsApi
import com.mousiki.shared.utils.Constants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class QueueFragment : Fragment(R.layout.fragment_queue), KoinComponent {

    private val binding by viewBinding(FragmentQueueBinding::bind)
    private val analyticsApi by lazy { get<AnalyticsApi>() }
    private val viewModel: QueueViewModel by viewModel {
        Injector.queueViewModel
    }
    private var onCloseQueue: (() -> Unit)? = null
    private val adapter: QueueAdapter by lazy {
        QueueAdapter(viewModel) { holder ->
            itemTouchHelper.startDrag(holder)
        }
    }
    private val itemTouchHelper by lazy {
        val callback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.START or ItemTouchHelper.END
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val from = viewHolder.adapterPosition
                val to = target.adapterPosition
                viewModel.swapItems(from, to)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                viewModel.removeTrackFromQueue(position)
            }

            override fun getSwipeDirs(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val position = viewHolder.adapterPosition
                if (viewModel.isCurrentTrack(position)) return 0
                return super.getSwipeDirs(recyclerView, viewHolder)
            }

            override fun isLongPressDragEnabled() = false

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    viewHolder?.itemView?.setBackgroundColor(Color.parseColor("#40ffffff"))
                }
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                viewHolder.itemView.addRipple()
            }

        }
        ItemTouchHelper(callback)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnClickListener {
            // Just to prevent player slide trigger
        }
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
        binding.btnClose.onClick {
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
            activity?.findViewById<ViewGroup>(R.id.queueFragmentContainer)?.isVisible = false
            onCloseQueue?.invoke()
        }
        DeviceInset.observe(viewLifecycleOwner, Observer { inset ->
            binding.topBar.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = inset.top
            }
        })
        binding.btnPlayOption.setImageResource(
            UserPrefs.getCurrentPlaybackSort().iconId(requireContext())
        )
        binding.recyclerView.adapter = adapter
        observe(viewModel.queue) { newList ->
            val firstTime = adapter.dataItems.isEmpty()
            val diffCallback = SongsDiffUtil(adapter.dataItems, newList)
            adapter.submitList(newList, diffCallback)
            if (firstTime) {
                val currentTrackIndex =
                    newList.indexOfFirst { PlayerQueue.isCurrentTrack(it.track) }
                (binding.recyclerView.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(
                    currentTrackIndex,
                    requireContext().screenSize().heightPx / 3
                )
            }
        }
        observeEvent(viewModel.onClickTrack) { track ->
            val bottomSheetFragment = TrackOptionsFragment()
            val bundle = Bundle()
            bundle.putParcelable(Constants.MUSIC_TRACK_KEY, track)
            bottomSheetFragment.arguments = bundle
            bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
            bottomSheetFragment.onDismissed = {
            }
        }
        observe(PlaybackLiveData) { state ->
            if (state == PlayerConstants.PlayerState.PLAYING
                || state == PlayerConstants.PlayerState.PAUSED
                || state == PlayerConstants.PlayerState.ENDED
            ) {
                viewModel.updateQueue(state)
            }
        }

        PlayerQueue.observe(viewLifecycleOwner, Observer { track ->
            loadAndBlurImage(track)
        })
        binding.btnPlayOption.onClick {
            val nextSort = UserPrefs.getCurrentPlaybackSort().next()
            binding.btnPlayOption.setImageResource(nextSort.iconId(requireContext()))
            UserPrefs.saveSort(nextSort)
        }
    }

    override fun onResume() {
        super.onResume()
        analyticsApi.logScreenView(javaClass.simpleName)
        darkStatusBar()
    }

    private fun loadAndBlurImage(video: Track) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val bitmap = binding.imgBlured.getBitmap(video.imgUrlDefault, 500) ?: return@launch
                val blurredBitmap = withContext(Dispatchers.Default) {
                    BlurImage.fastblur(bitmap, 0.1f, 50)
                }
                binding.imgBlured.setImageBitmap(blurredBitmap)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            } catch (error: OutOfMemoryError) {
                FirebaseCrashlytics.getInstance().recordException(error)
            }
        }
    }

    fun doOnClose(callback: () -> Unit) {
        this.onCloseQueue = callback
    }
}
