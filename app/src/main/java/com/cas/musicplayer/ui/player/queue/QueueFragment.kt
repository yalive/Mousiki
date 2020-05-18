package com.cas.musicplayer.ui.player.queue

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
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
import com.cas.common.extensions.observe
import com.cas.common.extensions.observeEvent
import com.cas.common.extensions.onClick
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.di.injector.injector
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.player.services.PlaybackLiveData
import com.cas.musicplayer.ui.bottomsheet.TrackOptionsFragment
import com.cas.musicplayer.ui.popular.SongsDiffUtil
import com.cas.musicplayer.utils.*
import com.crashlytics.android.Crashlytics
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import kotlinx.android.synthetic.main.queue_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QueueFragment : Fragment() {

    private val viewModel: QueueViewModel by viewModel {
        injector.queueViewModel
    }

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.queue_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemTouchHelper.attachToRecyclerView(recyclerView)
        btnClose.onClick {
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
            activity?.findViewById<ViewGroup>(R.id.queueFragmentContainer)?.isVisible = false
            PlayerQueue.showVideo()
        }
        DeviceInset.observe(viewLifecycleOwner, Observer { inset ->
            topBar.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = inset.top
            }
        })
        btnPlayOption.setImageResource(UserPrefs.getSort().icon)
        recyclerView.adapter = adapter
        observe(viewModel.queue) { newList ->
            val firstTime = adapter.dataItems.isEmpty()
            val diffCallback = SongsDiffUtil(adapter.dataItems, newList)
            adapter.submitList(newList, diffCallback)
            if (firstTime) {
                val currentTrackIndex =
                    newList.indexOfFirst { PlayerQueue.isCurrentTrack(it.track) }
                (recyclerView.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(
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
                lifecycleScope.launch {
                    delay(100)
                    PlayerQueue.hideVideo()
                }
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
        btnPlayOption.onClick {
            val nextSort = UserPrefs.getSort().next()
            btnPlayOption.setImageResource(nextSort.icon)
            UserPrefs.saveSort(nextSort)
        }
    }

    private fun loadAndBlurImage(video: MusicTrack) {
        lifecycleScope.launch {
            try {
                val bitmap = imgBlured?.getBitmap(video.imgUrlDefault) ?: return@launch
                val blurredBitmap = withContext(Dispatchers.Default) {
                    BlurImage.fastblur(bitmap, 0.1f, 30)
                }
                imgBlured?.setImageBitmap(blurredBitmap)
            } catch (e: Exception) {
                Crashlytics.logException(e)
            } catch (error: OutOfMemoryError) {
                Crashlytics.logException(error)
            }
        }
    }
}
